package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.BNFBootstrap.NT.*;
import static org.spartan.fajita.api.examples.BNFBootstrap.Term.*;

import java.io.IOException;

import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.BNF;
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
        .derive(BNF)//
        /**/.to(Header).and(Body).and(Footer) //
        .derive(Header)//
        /**/.to(Variables).and(Terminals) //
        /**/.or(Terminals).and(Variables) //
        .derive(Variables)//
        /**/.to(with, NonTerminal.class)//
        .derive(Terminals)//
        /**/.to(with, Terminal.class)//
        .derive(Body)//
        /**/.to(Start).and(Rule).and(Rules) //
        .derive(Start)//
        /**/.to(start, NonTerminal.class)//
        .derive(Rules)//
        /**/.to(Rule).and(Rules) //
        /**/.orNone() //
        .derive(Rule)//
        /**/.to(derive, NonTerminal.class).and(Conjunctions) //
        .derive(Conjunctions)//
        /**/.to(First_Conjunction).and(Extra_Conjunctions) //
        .derive(First_Conjunction)//
        /**/.to(to, NonTerminal.class).and(Symbol_Sequence) //
        /**/.or(to, Terminal.class, Class[].class).and(Symbol_Sequence) //
        /**/.or(toNone) //
        .derive(Extra_Conjunctions)//
        /**/.to(Extra_Conjunctions).and(Extra_Conjunction) //
        /**/.orNone() //
        .derive(Extra_Conjunction)//
        /**/.to(or, NonTerminal.class).and(Symbol_Sequence) //
        /**/.or(or, Terminal.class, Class[].class).and(Symbol_Sequence)//
        /**/.or(orNone)//
        .derive(Symbol_Sequence)//
        /**/.to(and, NonTerminal.class) //
        /**/.or(and, Terminal.class, Class[].class) //
        /**/.orNone() //
        .derive(Footer)//
        /**/.to(go)//
        .go();
    return b;
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(buildBNF());
  }
}