package org.spartan.fajita.revision.junk;

import java.lang.String;
import java.lang.SuppressWarnings;
import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.export.FluentAPIRecorder;

@SuppressWarnings("all")
public class Datalog {
  public static RULE_1_n1<RULE_1<RULE_1<RULE_1_rec_3bc, RULE_1_n1_rec_1e4>, RULE_1_n1<RULE_1_rec_3bc, RULE_1_n1_rec_1e4>>, RULE_1_n1<RULE_1<RULE_1_rec_3bc, RULE_1_n1_rec_1e4>, RULE_1_n1<RULE_1_rec_3bc, RULE_1_n1_rec_1e4>>> head(ASTNode arg0) {
    $$$ $$$ = new $$$();$$$.recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.head,arg0);return $$$;}

  public static RULE_1<RULE_1<RULE_1<RULE_1_rec_3bc, RULE_1_n1_rec_1e4>, RULE_1_n1<RULE_1_rec_3bc, RULE_1_n1_rec_1e4>>, RULE_1_n1<RULE_1<RULE_1_rec_3bc, RULE_1_n1_rec_1e4>, RULE_1_n1<RULE_1_rec_3bc, RULE_1_n1_rec_1e4>>> fact(ASTNode arg0) {
    $$$ $$$ = new $$$();$$$.recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.fact,arg0);return $$$;}

  public interface RULE_1<fact, head> extends ASTNode {
    fact fact(ASTNode arg0);

    head head(ASTNode arg0);
  }

  public interface RULE_1_n1<fact, head> {
    BODY_1<fact, head> body(ASTNode arg0);
  }

  public interface BODY1_1 {
    BODY2_1 literal(ASTNode arg0);
  }

  public interface BODY2_1 {
    BODY2_1 literal(ASTNode arg0);
  }

  public interface BODY_1<fact, head> extends ASTNode {
    fact fact(ASTNode arg0);

    head head(ASTNode arg0);
  }

  public interface LITERAL_1 {
    LITERAL_2 terms(String... arg0);
  }

  public interface LITERAL_2 {
  }

  public interface RULE_1_n1_rec_1e4 {
    BODY_1<RULE_1_rec_3bc, RULE_1_n1_rec_1e4> body(ASTNode arg0);
  }

  public interface RULE_1_rec_3bc {
    RULE_1_rec_3bc fact(ASTNode arg0);

    RULE_1_n1_rec_1e4 head(ASTNode arg0);
  }

  private interface ParseError {
  }

  private static class $$$ extends FluentAPIRecorder implements RULE_1, RULE_1_n1, BODY1_1, BODY2_1, BODY_1, LITERAL_1, LITERAL_2, RULE_1_n1_rec_1e4, RULE_1_rec_3bc {
    $$$() {
      super(new org.spartan.fajita.revision.examples.Datalog().bnf().ebnf());}

    public $$$ fact(ASTNode arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.fact,arg0);return this;}

    public $$$ head(ASTNode arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.head,arg0);return this;}

    public $$$ body(ASTNode arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.body,arg0);return this;}

    public $$$ literal(ASTNode arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.literal,arg0);return this;}

    public $$$ terms(String... arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.terms,arg0);return this;}
  }
}
