package org.spartan.fajita.api.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.parser.JActionTable.Accept;
import org.spartan.fajita.api.parser.JActionTable.Action;
import org.spartan.fajita.api.parser.JActionTable.Jump;
import org.spartan.fajita.api.parser.JActionTable.Shift;

public class JLRParser {
  public final BNF bnf;
  private final List<JState> states;
  private int labelsCount;
  private final JActionTable actionTable;
  private final Set<NonTerminal> nullableSymbols;
  private final Map<Symbol, Set<Verb>> baseFirstSets;

  public JLRParser(final BNF bnf) {
    this.bnf = bnf;
    labelsCount = 0;
    nullableSymbols = calculateNullableSymbols();
    baseFirstSets = calculateSymbolFirstSet();
    states = generateStatesSet();
    actionTable = new JActionTable(getStates());
    fillParsingTable();
  }
  private Set<NonTerminal> calculateNullableSymbols() {
    Set<NonTerminal> nullables = new HashSet<>();
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule rule : bnf.getRules())
        if (rule.getChildren().stream().allMatch(child -> nullables.contains(child) || child.equals(SpecialSymbols.epsilon)))
          moreChanges = nullables.add(rule.lhs);
    } while (moreChanges);
    return nullables;
  }
  private Map<Symbol, Set<Verb>> calculateSymbolFirstSet() {
    Map<Symbol, Set<Verb>> $ = new HashMap<>();
    for (NonTerminal nt : bnf.getNonTerminals())
      $.put(nt, new LinkedHashSet<>());
    for (Verb term : bnf.getVerbs())
      $.put(term, new LinkedHashSet<>(Arrays.asList(term)));
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule dRule : bnf.getRules())
        for (Symbol symbol : dRule.getChildren()) {
          moreChanges |= $.get(dRule.lhs).addAll($.getOrDefault(symbol, new HashSet<>()));
          if (!isNullable(symbol))
            break;
        }
    } while (moreChanges);
    return $;
  }
  public boolean isNullable(final Symbol... expression) {
    return Arrays.asList(expression).stream()
        .allMatch(symbol -> nullableSymbols.contains(symbol) || symbol == SpecialSymbols.epsilon);
  }
  private List<Verb> firstSetOf(final Symbol... expression) {
    List<Verb> $ = new ArrayList<>();
    for (Symbol symbol : expression) {
      $.addAll(baseFirstSets.get(symbol));
      if (!isNullable(symbol))
        break;
    }
    return $;
  }
  private void fillParsingTable() {
    for (JState state : getStates()) {
      final Set<JItem> items = state.getItems();
      if (items.stream().anyMatch(i -> i.readyToReduce() && i.lookahead.equals(SpecialSymbols.$)))
        addAcceptAction(state);
      for(JItem item : items){
        if (item.readyToReduce() && !(item.lookahead.equals(SpecialSymbols.$)))
          addJumpAction(state,item.lookahead, item.label);
        for ( Verb v : firstSetOf(ruleSuffix(item, item.dotIndex))){
          addShiftAction(state, item.lookahead,jumpSet(state, v),state.goTo(v));
        }
      }
    }
  }
  private void addAcceptAction(final JState state) {
    actionTable.set(state, SpecialSymbols.$, new Accept());
  }
  private void addShiftAction(final JState state, final Verb lookahead, Map<Integer, JState> jumpSet, JState nextState) {
    actionTable.set(state, lookahead, new Shift(jumpSet,nextState));
  }
  private void addJumpAction(final JState state, final Verb lookahead,final int label) {
    actionTable.set(state, lookahead, new Jump(jumpSet(state, lookahead),label));
  }
  public Action actionTable(final JState state, final Verb lookahead) {
    return actionTable.get(state.index, lookahead);
  }
  private JState generateInitialState() {
    Set<JItem> initialItems = bnf.getRulesOf(SpecialSymbols.augmentedStartSymbol) //
        .stream().map(r -> new JItem(r, SpecialSymbols.$, labelsCount++).asKernel()) //
        .collect(Collectors.toSet());
    Set<JItem> closure = calculateClosure(initialItems);
    return new JState(closure, bnf, 0);
  }
  private Set<JItem> calculateClosure(final Set<JItem> initialItems) {
    Queue<JItem> todo = new LinkedList<>(initialItems);
    HashSet<JItem> $ = new HashSet<>();
    do {
      JItem item = todo.remove();
      boolean exists = $.add(item);
      if (!exists || item.readyToReduce() || !item.rule.getChildren().get(item.dotIndex).isNonTerminal())
        continue;
      NonTerminal nt = (NonTerminal) item.rule.getChildren().get(item.dotIndex);
      final Symbol[] strAfterNT = ruleSuffix(item,item.dotIndex + 1);
      for (DerivationRule dRule : bnf.getRulesOf(nt)) {
        if (dRule.getChildren().size() == 0) // epsilon rule
          continue;
        if (isNullable(strAfterNT))
          todo.add(new JItem(dRule, item.lookahead, item.label));
        for (Verb t : firstSetOf(strAfterNT)) {
          todo.add(new JItem(dRule, t, labelsCount++));
        }
      }
      if (isNullable(nt))
        todo.add(item.advance());
    } while (!todo.isEmpty());
    return $;
  }
  private static Symbol[] ruleSuffix(JItem item,int index) {
    return Arrays.copyOfRange(item.rule.getChildren().toArray(new Symbol[] {}), index,
        item.rule.getChildren().size());
  }
  private List<JState> generateStatesSet() {
    JState initialState = generateInitialState();
    List<JState> $ = new ArrayList<>(Arrays.asList(initialState));
    Stack<JState> todo = new Stack<>();
    todo.push(initialState);
    while (!todo.isEmpty()) {
      JState state = todo.pop();
      for (Symbol lookahead : legalSymbols()) {
        if (!state.isLegalTransition(lookahead))
          continue;
        JState nextState = generateNextState(state, lookahead, $.size());
        int existingIndex = $.indexOf(nextState);
        if (existingIndex == -1) { // a non existing new state
          todo.add(nextState);
          $.add(nextState);
        } else // an already existing new state
          nextState = $.get(existingIndex);
        state.addGotoTransition(lookahead, nextState);
      }
    }
    return $;
  }
  private JState generateNextState(final JState state, final Symbol lookahead, int newIndex) {
    if (lookahead == SpecialSymbols.$)
      if (state.getItems().stream().anyMatch(i -> i.readyToReduce() && i.rule.lhs.equals(SpecialSymbols.augmentedStartSymbol)))
        return new AcceptState(bnf, newIndex);
    Set<JItem> initialItems = state.getItems().stream().//
        filter(item -> item.isLegalTransition(lookahead)) //
        .map(item -> item.advance().asKernel()) //
        .collect(Collectors.toSet());
    Set<JItem> closure = calculateClosure(initialItems);
    return new JState(closure, bnf, newIndex);
  }
  @SuppressWarnings("boxing") private static Map<Integer, JState> jumpSet(JState s, Verb v) {
    HashMap<Integer, JState> $ = new HashMap<>();
    List<JItem> nonkernel = s.getItems().stream().filter(i -> !i.kernel).collect(Collectors.toList());
    for (JItem i : nonkernel) {
      if (i.readyToReduce() || !i.rule.getChildren().get(i.dotIndex).equals(v))
        continue;
      $.put(i.label, s.goTo(i.rule.lhs).goTo(i.lookahead));
    }
    return $;
  }
  private Set<Symbol> legalSymbols() {
    Set<Symbol> notAllowed = new HashSet<>();
    Set<Symbol> symbols = new HashSet<>();
    symbols.addAll(bnf.getNonTerminals());
    symbols.addAll(bnf.getVerbs());
    symbols.removeAll(notAllowed);
    return symbols;
  }
  @Override public String toString() {
    String $ = "States:" + System.lineSeparator();
    for (JState state : states)
      $ += state.extendedToString() + System.lineSeparator();
    $ += actionTable.toString();
    return $;
  }
  public List<JState> getStates() {
    return states;
  }
}
