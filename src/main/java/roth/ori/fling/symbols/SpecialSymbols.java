package roth.ori.fling.symbols;

public class SpecialSymbols {
  public static final Verb $ = new Verb(new Terminal() {
    @Override public String name() {
      return "$";
    }
  }) {
    @Override public String toString() {
      return "$";
    }
  };
  // TODO Roth: check whether needed
  public static final Verb empty = new Verb(new Terminal() {
    @Override public String name() {
      return "~";
    }
  }) {
    @Override public String toString() {
      return "~";
    }
  };
  public static final Symbol augmentedStartSymbol = new Symbol() {
    @Override public String name() {
      return "augS";
    }
    @Override public String toString() {
      return name();
    }
  };
}
