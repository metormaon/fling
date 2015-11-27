package org.spartan.fajita.api.examples.balancedParenthesis;

import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.NT.BALANCED;
import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.Term.lp;
import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.Term.rp;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.examples.balancedParenthesis.states.AutoGeneratedBalancedParenthesisStates;
import org.spartan.fajita.api.examples.balancedParenthesis.states.AutoGeneratedBalancedParenthesisStates.Q0;
import org.spartan.fajita.api.examples.balancedParenthesis.states.AutoGeneratedBalancedParenthesisStates.Q11;
import org.spartan.fajita.api.examples.balancedParenthesis.states.AutoGeneratedBalancedParenthesisStates.Q2;
import org.spartan.fajita.api.examples.balancedParenthesis.states.AutoGeneratedBalancedParenthesisStates.Q3;
import org.spartan.fajita.api.examples.balancedParenthesis.states.AutoGeneratedBalancedParenthesisStates.Q4;
import org.spartan.fajita.api.examples.balancedParenthesis.states.AutoGeneratedBalancedParenthesisStates.Q5;
import org.spartan.fajita.api.examples.balancedParenthesis.states.AutoGeneratedBalancedParenthesisStates.Q6;
import org.spartan.fajita.api.examples.balancedParenthesis.states.AutoGeneratedBalancedParenthesisStates.Q7;
import org.spartan.fajita.api.examples.balancedParenthesis.states.AutoGeneratedBalancedParenthesisStates.Q8;

public class BalancedParenthesis {
  public static void expressionBuilder() {
    Q0 q0 = new AutoGeneratedBalancedParenthesisStates.Q0();
//    q0.lp().rp().lp().rp().lp().lp().rp().rp().$();
    final Q2<Q0> lp1 = q0.lp();
    Q5<Q2<Q0>, Q11<Q4<Q2<Q0>>>> lp2 = lp1.lp();
    Q5<Q5<Q2<Q0>, ?>, Q8<Q7<Q5<Q2<Q0>, ?>, ?>, Q11<Q4<Q2<Q0>>>>> lp3 = lp2.lp();
    Q6<Q5<Q5<Q2<Q0>, ?>, ?>, Q8<Q7<Q5<Q2<Q0>, ?>, ?>, Q11<Q4<Q2<Q0>>>>> rp1 = lp3.rp();
    Q8<Q7<Q5<Q2<Q0>, ?>, ?>, Q11<Q4<Q2<Q0>>>> rp2 = rp1.rp();
    final Q11<Q4<Q2<Q0>>> rp3 = rp2.rp();
    final Q2<Q11<Q4<Q2<Q0>>>> lp4 = rp3.lp();
    Q3<Q2<Q11<Q4<Q2<Q0>>>>> rp4 = lp4.rp();
    Q2<Q3<Q2<Q11<Q4<Q2<Q0>>>>>> lp5 = rp4.lp();
    Q3<Q2<Q3<Q2<Q11<Q4<Q2<Q0>>>>>>> rp5 = lp5.rp();
    rp5.$();
  }

  static enum Term implements Terminal {
    lp, rp, build;
    @Override public Type type() {
      return Type.VOID;
    }
  }

  static enum NT implements NonTerminal {
    BALANCED;
  }

  public static BNF buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("BalancedParenthesis") //
        .setStartSymbols(BALANCED) //
        .endConfig() //
        .derive(BALANCED).to(lp).and(BALANCED).and(rp) //
        /*        */.or().to(lp).and(BALANCED).and(rp).and(BALANCED) //
        /*        */.or().to(lp).and(rp).and(BALANCED)//
        /*        */.or().to(lp).and(rp) //
        .finish();
    return bnf;
  }
}
