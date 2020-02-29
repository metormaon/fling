package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.Symbol.oneOrMore;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.V.*;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.SubFigure.*;

public class SubFigure implements FluentLanguageAPI<Σ, V> {
  public enum Σ implements Terminal {
    load, row, column, seal
  }

  public enum V implements Variable {
    Figure, Orientation
  }

  @Override public Class<Σ> Σ() {
    return Σ.class;
  }
  @Override public Class<V> V() {
    return V.class;
  }
  @Override public il.ac.technion.cs.fling.BNF BNF() {
    return bnf(). //
        start(Figure). //
        derive(Figure).to(load.with(String.class)). //
        derive(Figure).to(Orientation, oneOrMore(Figure), seal). //
        derive(Orientation).to(row).or(column). //
        build();
  }
}