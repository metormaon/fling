package fling.grammar;

import static java.util.stream.Collectors.toSet;
import static fling.util.Collections.reversed;

import java.util.ArrayList;

import static fling.sententials.Alphabet.ε;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import fling.automata.DPDA;
import fling.automata.DPDA.δ;
import fling.sententials.Constants;
import fling.sententials.DerivationRule;
import fling.sententials.SententialForm;
import fling.sententials.Symbol;
import fling.sententials.Terminal;
import fling.sententials.Variable;
import fling.sententials.Word;

public class LL1 extends Grammar {
  public final Set<Object> Q;
  public final Set<Terminal> Σ;
  public final Set<Object> Γ;
  public final Set<δ<Object, Terminal, Object>> δs;
  public final Set<Object> F;
  public Object q0;
  public Word<Object> γ0;

  public LL1(BNF bnf, Namer namer) {
    super(bnf, namer);
    Q = new LinkedHashSet<>();
    Σ = new LinkedHashSet<>();
    Γ = new LinkedHashSet<>();
    δs = new LinkedHashSet<>();
    F = new LinkedHashSet<>();
    buildLL1Automaton();
  }
  @Override public DPDA<Object, Terminal, Object> toDPDA() {
    return new DPDA<>(Q, Σ, Γ, δs, F, q0, γ0);
  }
  private void buildLL1Automaton() {
    Σ.addAll(standardizedBnf.Σ);
    Σ.remove(Constants.$);
    Q.addAll(Σ);
    Object q0$ = "q0$", q0ø = "q0ø";
    Q.add(q0$);
    Q.add(q0ø);
    Γ.addAll(Σ);
    Γ.add(Constants.$);
    Γ.addAll(standardizedBnf.V);
    assert Γ.contains(Constants.S);
    Set<String> A = standardizedBnf.V.stream().filter(this::isNullable).map(this::getAcceptingVariable).collect(toSet());
    Γ.addAll(A);
    F.add(q0$);
    q0 = q0$; // Can be either.
    γ0 = new Word<>(Constants.$, !isNullable(Constants.S) ? Constants.S : getAcceptingVariable(Constants.S));
    // Moving from q0ø to q0$ with $.
    δs.add(new δ<>(q0ø, ε(), Constants.$, q0$, new Word<>()));
    // Moving from q0ø to q0$ with accepting variables.
    for (Object v : A)
      δs.add(new δ<>(q0ø, ε(), v, q0$, new Word<>(v)));
    // Moving from q0$ to q0ø with non-accepting variables and symbols.
    for (Object v : standardizedBnf.V)
      δs.add(new δ<>(q0$, ε(), v, q0ø, new Word<>(v)));
    for (Terminal σ : standardizedBnf.Σ)
      if (!Constants.$.equals(σ))
        δs.add(new δ<>(q0$, ε(), σ, q0ø, new Word<>(σ)));
    // Consuming transitions from q0$ to qσ.
    for (DerivationRule r : standardizedBnf.R)
      for (SententialForm sf : r.rhs)
        for (Terminal σ : standardizedBnf.firsts(sf))
          if (!Constants.$.equals(σ))
            δs.add(new δ<>(q0$, σ, getAcceptingVariable(r.lhs), σ, reversed(getPossiblyAcceptingVariables(sf, true))));
    for (Variable v : standardizedBnf.V)
      for (Terminal σ : standardizedBnf.Σ)
        if (!Constants.$.equals(σ) && !standardizedBnf.firsts(v).contains(σ) && isNullable(v))
          δs.add(new δ<>(q0$, σ, getAcceptingVariable(v), σ, new Word<>()));
    // Consuming transitions from q0ø to qσ.
    for (DerivationRule r : standardizedBnf.R)
      for (SententialForm sf : r.rhs)
        for (Terminal σ : standardizedBnf.firsts(sf))
          if (!Constants.$.equals(σ))
            δs.add(new δ<>(q0ø, σ, r.lhs, σ, reversed(getPossiblyAcceptingVariables(sf, false))));
    for (Variable v : standardizedBnf.V)
      for (Terminal σ : standardizedBnf.Σ)
        if (!Constants.$.equals(σ) && !standardizedBnf.firsts(v).contains(σ) && isNullable(v))
          δs.add(new δ<>(q0ø, σ, v, σ, new Word<>()));
    // ε transitions from qσ to q0$ (can be either).
    for (Terminal σ : standardizedBnf.Σ)
      if (!Constants.$.equals(σ))
        δs.add(new δ<>(σ, ε(), σ, q0$, new Word<>()));
    // σ consuming transitions from q0ø to itself.
    for (Terminal σ : standardizedBnf.Σ)
      if (!Constants.$.equals(σ))
        δs.add(new δ<>(q0ø, σ, σ, q0ø, new Word<>()));
    // ε transitions from qσ to itself.
    for (DerivationRule r : standardizedBnf.R)
      for (SententialForm sf : r.rhs)
        for (Terminal σ : standardizedBnf.firsts(sf))
          if (!Constants.$.equals(σ)) {
            δs.add(new δ<>(σ, ε(), getAcceptingVariable(r.lhs), σ, reversed(getPossiblyAcceptingVariables(sf, true))));
            δs.add(new δ<>(σ, ε(), r.lhs, σ, reversed(getPossiblyAcceptingVariables(sf, false))));
          }
  }
  private boolean isNullable(Symbol s) {
    return standardizedBnf.isNullable(s);
  }
  private String getAcceptingVariable(Variable v) {
    return v + "$";
  }
  private Word<Object> getPossiblyAcceptingVariables(SententialForm sf, boolean isFromQ0$) {
    List<Object> $ = new ArrayList<>();
    boolean isAccepting = isFromQ0$;
    for (Symbol s : sf) {
      $.add(!s.isVariable() || !isAccepting ? s : getAcceptingVariable(s.asVariable()));
      isAccepting &= isNullable(s);
    }
    return new Word<>($);
  }

  public class Item {
    public final DerivationRule rule;
    public final int dot;
    public final Object lookahead;

    public Item(DerivationRule rule, int dot, Object lookahead) {
      this.rule = rule;
      this.dot = dot;
      this.lookahead = lookahead;
    }
  }
}
