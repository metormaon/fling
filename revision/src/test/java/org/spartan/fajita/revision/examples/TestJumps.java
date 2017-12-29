package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.oneOrMore;
import static org.spartan.fajita.revision.api.Fajita.option;
import static org.spartan.fajita.revision.examples.TestJumps.NT.S;
import static org.spartan.fajita.revision.examples.TestJumps.Term.x;
import static org.spartan.fajita.revision.examples.TestJumps.Term.y;
import static org.spartan.fajita.revision.junk.TestJumps.x;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class TestJumps extends Grammar {
  public static enum Term implements Terminal {
    x, y
  }

  public static enum NT implements NonTerminal {
    S
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "TestJumps", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(x, oneOrMore(y, option(x)));
  }
  public static void main(String[] args) throws IOException {
    new TestJumps().generateGrammarFiles();
  }
  public static void testing() {
    x().y().y().x().y().x().$();
  }
}