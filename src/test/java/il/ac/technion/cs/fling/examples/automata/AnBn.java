package il.ac.technion.cs.fling.examples.automata;

import static il.ac.technion.cs.fling.DPDA.dpda;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static il.ac.technion.cs.fling.examples.automata.AnBn.Q.*;
import static il.ac.technion.cs.fling.examples.automata.AnBn.Γ.*;
import static il.ac.technion.cs.fling.examples.automata.AnBn.Σ.*;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.adapters.*;
import il.ac.technion.cs.fling.compilers.api.ReliableAPICompiler;
import il.ac.technion.cs.fling.internal.grammar.Grammar;
import il.ac.technion.cs.fling.internal.grammar.sententials.Verb;
import il.ac.technion.cs.fling.namers.NaiveNamer;

public class AnBn {
  enum Q implements Named {
    q0, q1, q2
  }

  enum Σ implements Terminal {
    a, b
  }

  enum Γ implements Named {
    E, X
  }

  public static final DPDA<Named, Verb, Named> dpda = Grammar.cast(dpda(Q.class, Σ.class, Γ.class) //
      .q0(q0) //
      .F(q2) //
      .γ0(E) //
      .δ(q0, a, E, q0, E, X) //
      .δ(q0, a, X, q0, X, X) //
      .δ(q0, b, X, q1) //
      .δ(q1, b, X, q1) //
      .δ(q1, ε(), E, q2) //
      .go());
  public static final String JavaFluentAPI = new JavaAPIAdapter("il.ac.technion.cs.fling.examples.generated", "AnBn", "$",
      new NaiveNamer("il.ac.technion.cs.fling.examples.generated", "AnBn")) //
          .printFluentAPI(new ReliableAPICompiler(dpda).compileFluentAPI());
  public static final String CppFluentAPI = new CppAPIAdapter("$", new NaiveNamer("AnBn")) //
      .printFluentAPI(new ReliableAPICompiler(dpda).compileFluentAPI());
}
