package org.spartan.fajita.api.examples.automatonCycles;

import static org.spartan.fajita.api.examples.automatonCycles.AutomatonCycles.NT.A;
import static org.spartan.fajita.api.examples.automatonCycles.AutomatonCycles.NT.B;
import static org.spartan.fajita.api.examples.automatonCycles.AutomatonCycles.NT.C;
import static org.spartan.fajita.api.examples.automatonCycles.AutomatonCycles.NT.D;
import static org.spartan.fajita.api.examples.automatonCycles.AutomatonCycles.Term.a;
import static org.spartan.fajita.api.examples.automatonCycles.AutomatonCycles.Term.b;
import static org.spartan.fajita.api.examples.automatonCycles.AutomatonCycles.Term.d;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.examples.automatonCycles.states.States;
import org.spartan.fajita.api.examples.automatonCycles.states.States.Q0;
import org.spartan.fajita.api.examples.automatonCycles.states.States.Q2;
import org.spartan.fajita.api.examples.automatonCycles.states.States.Q3;
import org.spartan.fajita.api.examples.automatonCycles.states.States.Q4;
import org.spartan.fajita.api.examples.automatonCycles.states.States.Q6;
import org.spartan.fajita.api.examples.automatonCycles.states.States.Q7;
import org.spartan.fajita.api.generators.BaseStateSpec;
import org.spartan.fajita.api.generators.typeArguments.TypeArgumentManager;
import org.spartan.fajita.api.parser.LRParser;

public class AutomatonCycles {
  public static void expressionBuilder() {
    Q0 q0 = new States.Q0();
    Q2<Q0, Q6<Q4<Q0, ?>, Q7<Q3<Q0>>>> a = q0.a();
    Q6<Q4<Q0, ?>, Q7<Q3<Q0>>> ab = a.b();
    Q7<Q3<Q0>> abd = ab.d();
    abd.$();
    Q2<Q2<Q2<Q0, ?>, ?>, Q6<Q4<Q0, ?>, Q7<Q3<Q0>>>> a3 = a.a().a();
    Q7<Q3<Q0>> d2 = a3.b().d();
    d2.$();
  }

  static enum Term implements Terminal {
    a, b, d;
    @Override public Type type() {
      return Type.VOID;
    }
  }

  static enum NT implements NonTerminal {
    A, B, C, D;
  }

  public static LRParser buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("Automaton cycles and more") //
        .setStartSymbols(D) //
        .endConfig() //
        .derive(D).to(C).and(d) //
        .derive(C).to(A).and(B) //
        .derive(A).to(a).and(A)/* */.or().to(a) //
        .derive(B).to(b) //
        .finish();
    System.out.println(bnf);
    LRParser parser = new LRParser(bnf);
    System.out.println(parser.getStates());
    System.out.println(parser);
    return parser;
  }
  public static void apiGeneration(final LRParser parser) {
    // ApiGenerator apiGenerator = new ApiGenerator(parser);
    System.out.println(new BaseStateSpec(new TypeArgumentManager(parser)).generate());
  }
}
