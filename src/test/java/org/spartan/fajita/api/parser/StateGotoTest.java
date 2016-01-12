package org.spartan.fajita.api.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.parser.old.Item;
import org.spartan.fajita.api.parser.old.LRParser;
import org.spartan.fajita.api.parser.old.State;
import org.spartan.fajita.api.parser.old.ActionTable.ReduceReduceConflictException;
import org.spartan.fajita.api.parser.old.ActionTable.ShiftReduceConflictException;

public class StateGotoTest {
  private enum Term implements Terminal {
    a, b, c, d;
    @Override public Type type() {
      return Type.VOID;
    }
  }

  private enum NT implements NonTerminal {
    S, A;
  }

  @SuppressWarnings("static-method") @Test public void testEmptyNextState()
      throws ReduceReduceConflictException, ShiftReduceConflictException {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(NT.S) //
        .derive(NT.S).to(NT.A).and(Term.b) //
        .derive(NT.A).to(Term.a).and(Term.c) //
        .finish();
    LRParser parser = new LRParser(bnf);
    State initialState = parser.getStates().get(0);
    assertFalse(initialState.isLegalTransition(Term.c));
    State nextState = initialState.goTo(Term.c);
    assertEquals(nextState, null);
  }
  @SuppressWarnings("static-method") @Test public void testNextStateTerminalLookahead()
      throws ReduceReduceConflictException, ShiftReduceConflictException {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(NT.S) //
        .derive(NT.S).to(NT.A).and(Term.b) //
        .derive(NT.A).to(Term.a).and(Term.c) //
        .finish();
    LRParser parser = new LRParser(bnf);
    State initialState = parser.getStates().get(0);
    assertTrue(initialState.isLegalTransition(Term.a));
    State nextState = initialState.goTo(Term.a);
    Item A_Rule = nextState.getItems().stream().filter(r -> r.rule.lhs.equals(NT.A)).findAny().get();
    assertEquals(1, A_Rule.dotIndex);
    assertEquals(nextState.getItems(), new HashSet<>(Arrays.asList(A_Rule)));
  }
  @SuppressWarnings("static-method") @Test public void testNextStateNonTerminalLookahead()
      throws ReduceReduceConflictException, ShiftReduceConflictException {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(NT.S) //
        .derive(NT.S).to(NT.A).and(Term.b) //
        .derive(NT.A).to(Term.a).and(Term.c) //
        .finish();
    LRParser parser = new LRParser(bnf);
    State initialState = parser.getStates().get(0);
    assertTrue(initialState.isLegalTransition(NT.A));
    State nextState = initialState.goTo(NT.A);
    Item S_Rule = nextState.getItems().stream().filter(r -> r.rule.lhs.equals(NT.S)).findAny().get();
    assertEquals(1, S_Rule.dotIndex);
    assertEquals(nextState.getItems(), new HashSet<>(Arrays.asList(S_Rule)));
  }
  @SuppressWarnings("static-method") @Test public void testUsesClosureInNextState()
      throws ReduceReduceConflictException, ShiftReduceConflictException {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(NT.S) //
        .derive(NT.S).to(Term.a).and(NT.A) //
        .derive(NT.A).to(Term.b).and(Term.c) //
        .finish();
    LRParser parser = new LRParser(bnf);
    State initialState = parser.getStates().get(0);
    State nextState = initialState.goTo(Term.a);
    assertEquals(2, nextState.getItems().size());
  }
}
