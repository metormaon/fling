package org.spartan.fajita.api.examples.balancedParenthesis;

import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.NT.BALANCED;
import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.Term.lp;
import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.Term.rp;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.examples.balancedParenthesis.states.AutoGeneratedBalancedParenthesisStates.Q0;

public class BalancedParenthesis {
  public static void expressionBuilder() {
    new Q0().lp().rp().lp().rp().lp().lp().rp().rp().$();
    new Q0().lp().lp().lp().rp().rp().rp().lp().rp().lp().rp().$();
  }

  static enum Term implements Terminal {
    lp, rp, build;
  }

  static enum NT implements NonTerminal {
    BALANCED;
  }

  public static BNF buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(BALANCED) //
        .derive(BALANCED).to(lp).and(BALANCED).and(rp) //
        /*        */.or(lp).and(BALANCED).and(rp).and(BALANCED) //
        /*        */.or(lp).and(rp).and(BALANCED)//
        /*        */.or(lp).and(rp) //
        .finish();
    return bnf;
  }
}
