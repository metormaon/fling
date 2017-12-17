package org.spartan.fajita.revision.export.testing;

import java.lang.Object;
import java.lang.SuppressWarnings;
import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.export.FluentAPIRecorder;
import org.spartan.fajita.revision.symbols.Terminal;

@SuppressWarnings("all") public class FajitaTesting {
  public static Example_1<Example_1<Example_1<Example_1_rec_3a4, MalExample_1_rec_167>, MalExample_1<Example_1_rec_3a4, MalExample_1_rec_167>>, MalExample_1<Example_1<Example_1_rec_3a4, MalExample_1_rec_167>, MalExample_1<Example_1_rec_3a4, MalExample_1_rec_167>>> example(
      ASTNode arg0) {
    $$$ $$$ = new $$$();
    $$$.recordTerminal(org.spartan.fajita.revision.examples.FajitaTesting.Term.example, arg0);
    return $$$;
  }
  public static MalExample_1<Example_1<Example_1<Example_1_rec_3a4, MalExample_1_rec_167>, MalExample_1<Example_1_rec_3a4, MalExample_1_rec_167>>, MalExample_1<Example_1<Example_1_rec_3a4, MalExample_1_rec_167>, MalExample_1<Example_1_rec_3a4, MalExample_1_rec_167>>> malexample(
      ASTNode arg0) {
    $$$ $$$ = new $$$();
    $$$.recordTerminal(org.spartan.fajita.revision.examples.FajitaTesting.Term.malexample, arg0);
    return $$$;
  }

  public interface Example_1<example, malexample> extends ASTNode {
    example example(ASTNode arg0);
    malexample malexample(ASTNode arg0);
    FajitaTestingAST.Test $();
  }

  public interface MalExample_1<example, malexample> extends ASTNode {
    example example(ASTNode arg0);
    malexample malexample(ASTNode arg0);
    FajitaTestingAST.Test $();
  }

  public interface ExampleBody1_1<call> {
    call call(Terminal arg0);
  }

  public interface ExampleBody_2 {
    ExampleBody_3 with(Object... arg0);
  }

  public interface ExampleBody_3 {
    ExampleBodyNext_1<ExampleBodyNext_1<ExampleBodyNext_1_rec_8f>> then(Terminal arg0);
  }

  public interface ExampleBodyNext_1<then> {
    ExampleBodyNext_2<then> with(Object... arg0);
  }

  public interface ExampleBodyNext_2<then> {
    then then(Terminal arg0);
  }

  public interface MalExample_1_rec_167 {
    Example_1_rec_3a4 example(ASTNode arg0);
    MalExample_1_rec_167 malexample(ASTNode arg0);
    FajitaTestingAST.Test $();
  }

  public interface Example_1_rec_3a4 {
    Example_1_rec_3a4 example(ASTNode arg0);
    MalExample_1_rec_167 malexample(ASTNode arg0);
    FajitaTestingAST.Test $();
  }

  public interface ExampleBodyNext_1_rec_8f {
    ExampleBodyNext_2<ExampleBodyNext_1_rec_8f> with(Object... arg0);
  }

  private interface ParseError {
  }

  private static class $$$ extends FluentAPIRecorder implements Example_1, MalExample_1, ExampleBody1_1, ExampleBody_2,
      ExampleBody_3, ExampleBodyNext_1, ExampleBodyNext_2, MalExample_1_rec_167, Example_1_rec_3a4, ExampleBodyNext_1_rec_8f {
    $$$() {
      super(new org.spartan.fajita.revision.examples.FajitaTesting().bnf().ebnf(), "org.spartan.fajita.revision.export.testing");
    }
    public $$$ example(ASTNode arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.FajitaTesting.Term.example, arg0);
      return this;
    }
    public $$$ malexample(ASTNode arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.FajitaTesting.Term.malexample, arg0);
      return this;
    }
    public $$$ call(Terminal arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.FajitaTesting.Term.call, arg0);
      return this;
    }
    public $$$ with(Object... arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.FajitaTesting.Term.with, arg0);
      return this;
    }
    public $$$ then(Terminal arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.FajitaTesting.Term.then, arg0);
      return this;
    }
    public FajitaTestingAST.Test $() {
      return ast("FajitaTestingAST");
    }
  }
}