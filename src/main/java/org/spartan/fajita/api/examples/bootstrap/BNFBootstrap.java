package org.spartan.fajita.api.examples.bootstrap;

import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.NT.*;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.NT.Body;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.NT.Conjunctions;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.NT.Extra_Conjunction;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.NT.Extra_Conjunctions;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.NT.First_Conjunction;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.NT.Footer;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.NT.Header;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.NT.Rule;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.NT.Rules;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.NT.Start;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.NT.Symbol_Sequence;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.NT.Terminals;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.NT.Variables;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.Term.and;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.Term.derive;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.Term.go;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.Term.or;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.Term.orNone;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.Term.start;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.Term.to;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.Term.toNone;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.Term.with;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNF.ClassEllipsis;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class BNFBootstrap {
  public static void expressionBuilder() {
    // showASTs();
  }

  static enum Term implements Terminal {
    start, derive, with, //
    to, and, toNone, or, orNone, //
    go;
  }

  static enum NT implements NonTerminal {
    BNF, Header, Body, Footer, //
    Variables, Terminals, Start, //
    Rules, Rule, Conjunctions, //
    First_Conjunction, Extra_Conjunctions, //
    Extra_Conjunction, //
    Symbol_Sequence //
  }

  public static BNF buildBNF() {
    BNF b = new BNFBuilder(Term.class, NT.class) //
        .start(BNF) //
        //
        .derive(BNF)/*               */.to(Header).and(Body).and(Footer) //
        .derive(Header)/*            */.to(Variables).and(Terminals) //
        /*                             */.or(Terminals).and(Variables) //
        .derive(Variables)/*         */.to(with, NonTerminal.class)//
        .derive(Terminals)/*         */.to(with, Terminal.class)//
        .derive(Body)/*              */.to(Start).and(Rules) //
        .derive(Start)/*             */.to(start,NonTerminal.class)//
        .derive(Rules)/*             */.to(Rule).and(Rules) //
        /*                             */.or(Rule) //
        .derive(Rule)/*              */.to(derive, NonTerminal.class).and(Conjunctions) //
        .derive(Conjunctions)/*      */.to(First_Conjunction).and(Extra_Conjunctions) //
        .derive(First_Conjunction)/* */.to(to, NonTerminal.class).and(Symbol_Sequence) //
        /*                             */.or(to, Terminal.class, ClassEllipsis.class).and(Symbol_Sequence) //
        /*                             */.or(toNone) //
        .derive(Extra_Conjunctions)/**/.to(Extra_Conjunction).and(Extra_Conjunctions) //
        /*                             */.orNone() //
        .derive(Extra_Conjunction)/* */.to(or, NonTerminal.class).and(Symbol_Sequence) //
        /*                             */.or(or, Terminal.class, ClassEllipsis.class).and(Symbol_Sequence)//
        /*                             */.or(orNone)//
        .derive(Symbol_Sequence)/*   */.to(and, NonTerminal.class) //
        /*                             */.or(and, Terminal.class, ClassEllipsis.class) //
        /*                             */.orNone() //
        .derive(Footer)/*            */.to(go)//
        .finish();
    return b;
  }
}
