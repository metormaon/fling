package roth.ori.fling.examples;

import static roth.ori.fling.examples.Exp.NT.S;
import static roth.ori.fling.examples.Exp.NT.X;
import static roth.ori.fling.examples.Exp.Term.a;
import static roth.ori.fling.examples.Exp.Term.b;
import static roth.ori.fling.examples.Exp.Term.c;
import static roth.ori.fling.examples.Exp.Term.d;

import java.io.IOException;

import roth.ori.fling.api.Fling;
import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;

public class Exp extends Grammar {
  public static enum Term implements Terminal {
    a, b, c, d
  }

  public static enum NT implements Symbol {
    S, X
  }

  @Override public FlingBNF bnf() {
    return Fling.build(Exp.class, Term.class, NT.class, "Exp", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(X, S, X).or(d) //
        .derive(X).to(a, X, b).or(c);
  }
  public static void main(String[] args) throws IOException {
    new Exp().generateGrammarFiles();
  }
}
