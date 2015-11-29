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
import org.spartan.fajita.api.examples.automatonCycles.states.AutoGeneratedAutomatonCyclesStates.Q0;

public class AutomatonCycles {
  public static void expressionBuilder() {
    new Q0().a().b().d().$();
    new Q0().a().a().a().b().d().$();
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

  public static BNF buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("AutomatonCycles") //
        .start(D) //
        .endConfig() //
        .derive(D).to(C).and(d) //
        .derive(C).to(A).and(B) //
        .derive(A).to(a).and(A) //
        /* */.or().to(a) //
        .derive(B).to(b) //
        .finish();
    return bnf;
  }
}
