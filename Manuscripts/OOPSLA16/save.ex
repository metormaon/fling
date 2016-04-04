Use of uninitialized value $environmentStack[-1] in hash element at /opt/texbin/latexindent line 997, <MAINFILE> line 54.
Use of uninitialized value $environmentStack[-1] in hash element at /opt/texbin/latexindent line 1021, <MAINFILE> line 54.
%! TEX root = 00.tex
What can be computed, and what can not be computed by coercing the type system
and the type checker of a certain programming language to do abstract
computations it was never meant to carry out? And, why should we care?

One concrete reason is fluent
APIs~\cite{VanDeursen:Klint:2000,Hudak:1997,Fowler:2010} are neat.
There is joy, and in many cases boost to productivity
in elegant, self-documenting code snippets such as
\begin{quote}
  \label{figure:DSL}
  \parbox[c]{44ex}{\javaInput[]{../Fragments/camel-apache.java.fragment}}
\end{quote}
(a use case of Apache Camel~\cite{Ibsen:Anstey:10}, open-source integration
framework), and,
\begin{quote}
  \javaInput[minipage,width=54ex,left=-2ex]{../Fragments/jOOQ.java.fragment}
\end{quote}
(a use case of jOOQ\urlref{http://www.jooq.org}, a framework for writing
SQL like code in \Java, much like LINQ project~\cite{Meijer:Beckman:Bierman:06}
in the context of \CSharp).

We argue that
the actual implementation of the many fluent API systems
jMock~\cite{Freeman:Pryce:06},
Hamcrest\urlref{http://hamcrest.org/JavaHamcrest/},
EasyMock\urlref{http://easymock.org/},
jOOR\urlref{https://github.com/jOOQ/jOOR},
jRTF\urlref{https://github.com/ullenboom/jrtf},
etc., is traditionally not carried out
in the neat manner it could possibly take.
Our reason for saying this is that the fundamental problem in
fluent API designs is the decision on the ‟language”.
This language is the precise definition of which sequences of method
applications are legal and which are not.

As it turns out, the question of whether a BNF definition of a such a language
can be ‟compiled” automatically into a fluent API implementation is, a
question of the computational power of the underlying language. In \Java, the problem
is particularly interesting since in \Java (unlike e.g., \CC~\cite{Gutterman:2003}),
the type system is not Turing complete.

It was shown recently that for any reasonable fluent API language definition
(specifically, one which can be recognized by an LR($k$) grammar for some~$k
≥0$), there exists some \Java implementation that realizes this
language~\cite{Gil:Levy:2016}.

The theoretical result takes a toll of exponential blowup. Specifically, the
construction builds first a determinsitic pushown automaton whose size
parameters, $g$ (number of stack symbols), and,~$q$ (number of internal
states), are polynomial in the size of the grammar. This automaton is then
by emulated by a weaker automaton, with as many as
\[
  O\left(g^{O(1)}(q²g^{O(1)})^{q⨉g^{O(1)}}\right)
\] 
stack symbols.
This weaker automaton is then ``compiled'' into a  collection of \Java types,
where there is at least one type for each of these symbols.

Is there a practical alternative? Here we present an algorithm to compile an
LL(1) grammar of a fluent API language into a \Java implementation of this
fluent API. \Fajita is a tool that implements the algorithm.


\subsection{The many trials of doing it in \Java}
Fluent APIs are neat, but design is complicated,
involving theory of automata, type theory, language design, etc.

And still, automatic generation of these exist to some extent.
One example, is fluflu\urlref{https://github.com/verhas/fluflu}: a software artifact that uses
\Java annotations to define finite state automata(henceforth, FSA), and then
compiles it to a fluent API based on this automaton. Usage example of fluflu is
depicted at \cref{figure:fluflu}.

\begin{figure}[ht]
  \caption{\label{figure:fluflu}
    Usage example for fluflu, generation a fluent
  api for the regular expression~$a^*$}
  \javaInput[minipage,left=-4ex]{fluflu.example.listing}
\end{figure}
The code excerpt in~\cref{figure:fluflu} defines a single stated FSA using
fluflu. The state is both initial and accepting, with a single self
transition, labeled with terminal~$a$. The regular language realized by the
automaton is~$L=a^*$.

Although fluent API generation was ‟achieved”, this example has many flaws, some of them are:

\begin{enumerate}
  \item The usage is complicated and the resulted code is messy.
  \item Defining a FSA is harder then writing, for example, the corresponding regular
        expression (and in some cases, the size of the FSA might be exponentially bigger)
  \item Languages defined by FSAs can only be regular, a rather small
        class of languages.
\end{enumerate}


In order to demonstrate the concept, first consider the fragment SQL queries
language defined in \cref{figure:sql-bnf}.

\begin{figure}[ht]
  \caption{\label{figure:sql-bnf}
    A BNF for a fragment of SQL select queries.
  }
  \begin{Grammar}
    \begin{aligned}
      \<Query>    & \Derives \cc{select()} \<Quant>\~\cc{from(Table.\kk{class})} \<Where> \hfill⏎
      \<Quant>    & \Derives \cc{all()} \hfill⏎
                  & \| \cc{columns(Column[].\kk{class})} \hfill⏎
      \<Where>    & \Derives \cc{where()} \cc{column(Column.\kk{class})} \<Operator> \hfill⏎
                  & \|ε \hfill⏎
      \<Operator> & \Derives \cc{equals(Expr.\kk{class})}\hfill⏎
                  & \| \cc{greaterThan(Expr.\kk{class})} \hfill⏎
                  & \|\cc{lowerThan(Expr.\kk{class})} \hfill
    \end{aligned}
  \end{Grammar}
\end{figure}

Defining a fluent API for this language with \Fajita is made
intuitive and concise as shown in \cref{figure:sql-bnf-java}.

\begin{figure}[ht]
  \caption{\label{figure:sql-bnf-java}
    A \Java code excerpt defining the BNF specification of the fragment SQL
    language defined in \cref{figure:sql-bnf}.}
  \javaInput[minipage,width=\linewidth,left=-6ex]{sql.bnf.listing}
\end{figure}

\cref{figure:sql-bnf-java} shows how can the derivation rules be defined in
such a way that the code can be read in English as one would read the
grammatical definition in \cref{figure:sql-bnf}.

The excerpt shows how can \Fajita be used to define fluent
APIs in terms of BNFs (or context-free grammars), in addition it provides
insight to solving all three issues that appeared with fluflu.

First, the code in \cref{figure:sql-bnf-java} is intuitive to write, easily
read, and type safe. The two other issues are solved by defining the API using
a BNF, as it better conforms with design of DSLs (the connection between fluent
APIs and DSLs is discussed later in this section) and provides the designer
with a much expressive tool then regular languages (the glass ceiling of
fluflu).

Except the required API for the calls to~\cc{derive($·$)},
\cc{to($·$)}, \cc{and($·$)} etc., the definition of terminals such as
\cc{select}, \cc{from}, \cc{all}, and nonterminals as \cc{Query},
\cc{Where}, \cc{Operator}, is also required.

The definition of these is possible by defining two \Java enums, one for the
nonterminals (also reffed as variables) and one for the terminals (also
referred as input symbols), for the current example we would define the types
\cc{SQLTerminals} and \cc{SQLNonTerminals} as:

\begin{quote}
  \parbox[c]{43ex}{\javaInput[minipage,width=\linewidth,left=-4ex]{sql.enums.listing}}
\end{quote}

The \cc{BNFBuilder} class receives two parameters in the constructor, being the enums
of the terminals and nonterminals.
To improve the type safeness of the API the types implement the base classes
\cc{Terminal} and \cc{NonTerminal} accordingly.

Finally, the code in \cref{figure:sql-bnf-java} allows designers to annotate
each terminal with a list of types.
These types will become the parameters of the terminal's method.

A question rises after seeing \Fajita and fluflu: how come the representation
of simple languages such as regular languages is so complex (as seen in fluflu)
and the representation of a more complex class of languages, is rather simple?
As it turns out, two factors are involved in the answer.

The first is the complexity of representing the language.
Regular language are mostly defined by regular expression, but the language of
all regular expressions is actually context-free and not regular!
(Intuitively, since regular expressions uses parenthesizing). On the contrary,
the definition of context-free languages is usually done with a BNF (or
context-free grammar), and the language of all BNF is regular!
Thus, in order to define a fluent API for generating regular languages, on needs a
context-free language fluent API, but in order to define a fluent API for
generating context-free languages, one only need a regular fluent API.
Since fluflu ‟know” only how to generate fluent APIs for regular languages,
it cannot represent a fluent API for the generation of itself.

The second factor is the practical complexity of generating the \Java types to
support the fluent API. As covered in \cref{section:example} the generation of
fluent APIs for regular languages is rather simple, while the generation of
those for context-free languages is the main challenge this paper is
confronting.

\subsection{Tell about the computational model of \CC.}

\subsection{The difficulty in understanding the \Java model}

\subsection{contributions}

\subsection{A Type Perspective on Fluent APIs}
Is it possible at all to produce a fluent API for the entire SQL language, or
XPath, HTML, regular expressions, BNFs, EBNFs, etc.? Of course, with no
operator overloading it is impossible to fully emulate tokens; method names
though make a good substitute for tokens, as done in
‟\lstinline{.when(header(foo).isEqualTo("bar")).}” (\cref{figure:DSL}). The
questions that motivate this research are:

\begin{quote}
  \begin{itemize}
    \item Given a specification of a DSL, determine whether there exists
          a fluent API that can be made for this specification?
    \item In the cases that such fluent API is possible,
          can it be produced automatically?
    \item Is it feasible to produce a Bison~\cite{Bison:manual}
          like \emph{compiler-compiler}
          to convert a language specification into a fluent API?
  \end{itemize}
\end{quote}

Inspired by the theory of formal languages and automata,
this study explores what can be done with fluent APIs in \Java.

Consider some fluent API (or DSL) specification, permitting only certain call
chains and disallowing all others.
Now, think of the formal language that defines the set of these permissible
chains.
We prove that there is always \Java type definition that
\emph{realizes} this fluent definition, provided that this
language is \emph{deterministic context-free}, where
\begin{itemize}
  \item In saying that a type definition \emph{realizes} a specification of
        fluent API, we mean that call chains that conform with the API definition
        compile correctly, and, conversely, call chains that are forbidden by the
        API definition do not type-check, resulting in an appropriate compiler
        error. \item Roughly speaking, deterministic context-free languages are
        those context-free languages that can be recognized by an LR parser†{The
          ‟L" means reading the input left to right; the ‟R" stands for rightmost
          derivation}~\cite{Aho:Sethi:Ullman:86}.
        \par
        An important property of this family is that none of its members is
        ambiguous. Also, it is generally believed that most practical programming
        languages are deterministic context-free.
\end{itemize}

A problem related to that of recognizing a formal language,
is that of parsing, i.e., creating, for input which is within the language,
a parse tree according to the language's grammar.
In the domain of fluent APIs, the distinction between recognition and parsing
is in fact the distinction between compile time and runtime.
Before a program is run, the compiler checks whether the fluent API call is
legal, and code completion tools will only suggest legal extensions of a
current call chain.

In contrast, a parse tree can only be created at runtime.
Some fluent API definitions create the parse-tree
iteratively, where each method invocations in the call chain adding
more components to this tree.
However, it is always possible to generate this tree in ‟batch” mode:
This is done by maintaining a \emph{fluent-call-list} which
starts empty and grows at runtime by having each method invoked add to it
a record storing the method's name and values of its parameters.
The list is completed at the end of the fluent-call-list, at which point it is
fed to an appropriate parser that converts it into a parse tree (or even an
AST).

\subsection{Contribution}
The answers we provide for the three questions above are:
\begin{quote}
  \begin{enumerate}
    \item If the DSL specification is that of a deterministic context-free
          language, then a fluent API exists for the language, but we do not know
          whether such a fluent API exists for more general languages.
          \par
          Recall that there are universal cubic time parsing
          algorithms~\cite{Cocke:1969,Earley:1970,Younger:1967} which can parse (and
          recognize) any context-free language. What we do not know is whether
          algorithms of this sort
          can be encoded within the framework of the \Java type system.
    \item
          There exists an algorithm to generate a fluent API that realize any
          deterministic context-free languages. Moreover, this fluent API can create
          at runtime, a parse tree for the given language. This parse tree can then be
          supplied as input to the library that implements the language's semantic.
    \item
          Unfortunately, a general purpose
          compiler-compiler is not yet feasible with the current algorithm.
          \begin{itemize}
            \item One difficulty is usual in the fields of formal languages:
                  The algorithm is complicated and relies on
                  modules implementing complicated theoretical results, which, to the best
                  of our knowledge, have never been implemented.
            \item Another difficulty is that a certain design decision in the
                  implementation of the standard \texttt{javac} compiler is likely to make
                  it choke on the \Java code generated by the algorithm.
          \end{itemize}
  \end{enumerate}
\end{quote}

Other concrete contributions made by this work include
\begin{itemize}
  \item the understanding that the definition of fluent APIs is analogous to
        the definition of a formal language.
  \item a lower bound (deterministic pushdown automata)
        on the theoretical ‟computational complexity” of the \Java type system.
  \item an algorithm for producing a fluent API for deterministic context-free
        languages.
  \item a collection of generic programming techniques, developed towards this
        algorithm.
  \item a demonstration that the runtime of Oracle's \texttt{javac} compiler
        may be exponential in the program size.
\end{itemize}

\subsection{Related Work}

It has long been known
that \CC templates are Turing complete in the following precise sense:

\begin{Proposition}
  \label{theorem:Gutterman}
  For every Turing machine,~$m$, there exists a \CC program,~$Cₘ$ such that
  compilation of~$Cₘ$ terminates if and only if
  Turing-machine~$m$ halts.
  Furthermore, program~$Cₘ$ can be effectively generated
  from~$m$~\cite{Gutterman:2003}.
\end{Proposition}

Intuitively, this is due to the fact that templates in \CC
feature both recursive invocation and conditionals (in the form of
‟\emph{template specialization}”).

In the same fashion, it should be mundane to make the judgment that \Java's
generics are not Turing-complete since they offer no conditionals. Still, even
though there are time complexity results regarding type systems in functional
languages, we failed to find similar claims for \Java.

Specialization, conditionals, \kk{typedef}s and other features of \CC
templates, gave rise to many advancements in template/generic/generative
programming in the language~\cite{Austern:1998,Musser:Stepanov:1989,
Backhouse:Jansson:1999, Dehnert:Stepanov:2000}, including e.g., applications in
numeric libraries~\cite{Veldhuizen:95,Vandevoorde:Josuttis:02}, symbolic
derivation~\cite{Gil:Gutterman:98} and a full blown template
library~\cite{Abrahams:Gurtovoy:04}.

Garcia et al.~\cite{Garcia:Jarvi:Lumsdaine:Siek:Willcock:03} compared the
expressive power of generics in half a dozen major programming languages. In
several ways, the \Java approach~\cite{Bracha:Odersky:Stoutamire:Wadler:98} did
not rank as well as others.

Not surprisingly, work on meta-programming using \Java generics, research
concentrating on other means for enriching the language, most importantly
annotations~\cite{Papi:08}.

The work on SugarJ~\cite{Erdweg:2011} is only one of many other attempts to
achieve the embedded DSL effect of fluent APIs by language extensions.

Suggestions for semi-automatic generation can be found in the work of
Bodden~\cite{Bodden:14} and on numerous locations in the web. None of these
materialized into an algorithm or analysis of complexity. However, there is a
software artifact (fluflu\urlref{https://github.com/verhas/fluflu}) that
automatically generates a fluent API that obeys the transitions of a given
finite automaton.

The thesis propounded by this research is that API design, and especially
fluent API design
can and should be made in terms of language design. Software missionaries
and preachers such as Fowler~\cite{Fowler:2005} have long claimed that API
design resembles the design of a \textbf Domain \textbf Specific \textbf
Language (henceforth \emph{DSL}, see,
e.g.,~\cite{VanDeursen:Klint:2000,Hudak:1997,Fowler:2010} for review
articles).
In the words of Fowler ‟The difference between API design and DSL design is
then rather small”~\cite{Fowler:2005}

Another objective of this research is to let the unification of the notions of
DSL and (fluent) API design become tighter in this sense: With \Self, the
design of a fluent API framework, is solely in terms of the grammar for the DSL
that defines this fluent API\@. This grammar specification is then
automatically translated to an implementation of the fluent API that this DSL
defines. This translation generates the intricate type hierarchy
and methods of types in it in such a way
that only sequence of calls that conform
to the specification would
compile correctly (concretely, type-check).
