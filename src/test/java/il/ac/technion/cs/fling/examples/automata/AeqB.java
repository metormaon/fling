package il.ac.technion.cs.fling.examples.automata;

import static il.ac.technion.cs.fling.DPDA.dpda;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static il.ac.technion.cs.fling.examples.automata.AeqB.Q.*;
import static il.ac.technion.cs.fling.examples.automata.AeqB.Γ.*;
import static il.ac.technion.cs.fling.examples.automata.AeqB.Σ.*;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.adapters.*;
import il.ac.technion.cs.fling.compilers.api.ReliableAPICompiler;
import il.ac.technion.cs.fling.internal.grammar.Grammar;
import il.ac.technion.cs.fling.internal.grammar.sententials.Verb;
import il.ac.technion.cs.fling.namers.NaiveNamer;

/**
 * AeqB = {w in {a, b}* | #a in w = #b in w}.
 * 
 * @author Ori Roth
 */
public class AeqB {
  /**
   * Set of DPDA states.
   */
  enum Q implements Named {
    q0, q1
  }

  /**
   * Set of DPDA input letters.
   */
  enum Σ implements Terminal {
    a, b
  }

  /**
   * Set of DPDA stack symbols.
   */
  enum Γ implements Named {
    E, A, B
  }

  /**
   * DPDA accepting AeqB.
   */
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
  public static final String JavaFluentAPI = new JavaAPIAdapter("il.ac.technion.cs.fling.examples.generated", "AeqB", "$",
      new NaiveNamer("il.ac.technion.cs.fling.examples.generated", "AeqB")) //
          .printFluentAPI(new ReliableAPICompiler(dpda).compileFluentAPI());
  /**
   * C++ fluent API supporting method chains of the form
   * <code>a()->b().a().b()...$();</code>
   */
  public static final String CppFluentAPI = new CppAPIAdapter("$", new NaiveNamer("AeqB")) //
      .printFluentAPI(new ReliableAPICompiler(dpda).compileFluentAPI());
  /**
   * SML fluent API
   */
  public static final String SMLFluentAPI = new SMLAPIAdapter("zzz", new NaiveNamer("AeqB")) //
      .printFluentAPI(new ReliableAPICompiler(dpda).compileFluentAPI());

  /**
   * Print C++ program to standard output.
   */
  public static void main(final String[] args) {
//    System.out.println(CppFluentAPI);
    System.out.println(SMLFluentAPI);
  }
}
