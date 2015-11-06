package org.spartan.fajita.api.generators.typeArguments;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;

public final class InheritedState implements Comparable<InheritedState> {
  public final int depth;
  public final NonTerminal lhs;
  public final Symbol lookahead;

  public InheritedState(final int n, final NonTerminal lhs, final Symbol l) {
    depth = n;
    this.lhs = lhs;
    lookahead = l;
  }
  @Override public boolean equals(final Object obj) {
    if (obj == null || obj.getClass() != InheritedState.class)
      return false;
    if (this == obj)
      return true;
    return compareTo((InheritedState) obj) == 0;
  }
  @Override public int hashCode() {
    int $ = Integer.hashCode(depth);
    if (lhs != null)
      $ += lhs.hashCode();
    if (lhs != null)
      $ += lookahead.hashCode();
    return $;
  }
  @Override public int compareTo(final InheritedState o) {
    int depthComparison = depth - o.depth;
    if (depthComparison != 0)
      return depthComparison;
    int ntComparison = lhs.name().compareTo(o.lhs.name());
    if (ntComparison != 0)
      return ntComparison;
    int lookaheadComparison = lookahead.name().compareTo(o.lookahead.name());
    return lookaheadComparison;
  }
  @Override public String toString() {
    return lhs.name() + '_' + depth + '_' + lookahead;
  }
}