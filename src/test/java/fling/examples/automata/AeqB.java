package fling.examples.automata;

import static fling.automata.Alphabet.ε;
import static fling.automata.DPDA.dpda;
import static fling.examples.automata.AeqB.Q.*;
import static fling.examples.automata.AeqB.Γ.*;
import static fling.examples.automata.AeqB.Σ.*;

import fling.adapters.*;
import fling.automata.DPDA;
import fling.compilers.api.ReliableAPICompiler;
import fling.internal.grammar.Grammar;
import fling.internal.grammar.sententials.*;
import fling.namers.NaiveNamer;

public class AeqB {
  enum Q implements Named {
    q0, q1
  }

  enum Σ implements Terminal {
    a, b
  }

  enum Γ implements Named {
    E, A, B
  }

  public static final DPDA<Named, Verb, Named> dpda = Grammar.cast(dpda(Q.class, Σ.class, Γ.class) //
      .q0(q0) //
      .F(q0) //
      .γ0(E) //
      .δ(q0, a, E, q1, E, A) //
      .δ(q0, b, E, q1, E, B) //
      .δ(q1, ε(), E, q0, E) //
      .δ(q1, a, A, q1, A, A) //
      .δ(q1, a, B, q1) //
      .δ(q1, b, A, q1) //
      .δ(q1, b, B, q1, B, B) //
      .go());
  public static final String JavaFluentAPI = new JavaAPIAdapter("fling.examples.generated", "AeqB", "$",
      new NaiveNamer("fling.examples.generated", "AeqB")) //
          .printFluentAPI(new ReliableAPICompiler(dpda).compileFluentAPI());
  public static final String CppFluentAPI = new CppAPIAdapter("$", new NaiveNamer("AeqB")) //
      .printFluentAPI(new ReliableAPICompiler(dpda).compileFluentAPI());

  public static void main(final String[] args) {
    System.out.println(CppFluentAPI);
  }
}