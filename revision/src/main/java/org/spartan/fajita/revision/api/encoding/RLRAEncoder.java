package org.spartan.fajita.revision.api.encoding;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.export.FluentAPIRecorder;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.Constants;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.types.NestedType;
import org.spartan.fajita.revision.symbols.types.ParameterType;

public class RLRAEncoder {
  public final String topClassName;
  public final String topClass;
  final NonTerminal startSymbol;
  final BNF bnf;
  public final String packagePath;
  final String topClassPath;
  final Namer namer;
  final List<String> apiTypes;
  final List<String> staticMethods;
  final Class<? extends Grammar> provider;

  public RLRAEncoder(Fajita fajita, NonTerminal start) {
    topClassName = fajita.apiName;
    packagePath = fajita.packagePath;
    topClassPath = packagePath + "." + topClassName;
    startSymbol = start;
    provider = fajita.provider;
    bnf = fajita.bnf();
    namer = new Namer();
    apiTypes = new ArrayList<>();
    staticMethods = new ArrayList<>();
    computeMembers();
    StringBuilder $ = new StringBuilder();
    $.append("package ").append(packagePath).append(";@").append(SuppressWarnings.class.getCanonicalName()) //
        .append("(\"all\")").append(" public class ").append(topClassName).append("{");
    for (String s : staticMethods)
      $.append(s);
    for (String s : apiTypes)
      $.append(s);
    $.append("}");
    topClass = $.toString();
  }
  // TODO Roth: code duplication in constructors
  public RLRAEncoder(Fajita fajita, Symbol nested) {
    assert nested.isNonTerminal() || nested.isExtendible();
    topClassName = nested.name();
    packagePath = fajita.packagePath;
    topClassPath = packagePath + "." + topClassName;
    startSymbol = nested.head().asNonTerminal();
    provider = fajita.provider;
    bnf = fajita.bnf().getSubBNF(startSymbol);
    namer = new Namer();
    apiTypes = new ArrayList<>();
    staticMethods = new ArrayList<>();
    computeMembers();
    StringBuilder $ = new StringBuilder();
    $.append("package ").append(packagePath).append(";@").append(SuppressWarnings.class.getCanonicalName()) //
        .append("(\"all\")").append(" public class ").append(topClassName).append("{");
    for (String s : staticMethods)
      $.append(s);
    for (String s : apiTypes)
      $.append(s);
    $.append("}");
    topClass = $.toString();
  }
  private void computeMembers() {
    new MembersComputer().compute();
  }

  class Namer {
    private final Map<Verb, String> verbNames = new LinkedHashMap<>();
    private final Map<Terminal, Integer> terminalCounts = new LinkedHashMap<>();

    public String name(Verb v) {
      if (Constants.$.equals(v))
        return "$";
      if (verbNames.containsKey(v))
        return verbNames.get(v);
      int x;
      terminalCounts.put(v.terminal,
          Integer.valueOf(x = terminalCounts.getOrDefault(v.terminal, Integer.valueOf(0)).intValue() + 1));
      String $ = v.terminal.name() + x;
      verbNames.put(v, $);
      return $;
    }
    private String name(Symbol s) {
      return s.isVerb() ? name(s.asVerb()) : s.asNonTerminal().name();
    }
    private String names(Collection<? extends Symbol> ss) {
      return String.join("", ss.stream().map(s -> name(s)).collect(toList()));
    }
    public String name(J j) {
      assert j != JJAMMED && j != JUNKNOWN && j.address.size() <= 1 && !j.isEmpty();
      StringBuilder $ = new StringBuilder();
      if (!j.address.isEmpty())
        $.append(name(j.address.peek()));
      $.append("¢").append(names(j.toPush)).append("_");
      Set<Verb> blj = j.address.baseLegalJumps();
      blj.remove(Constants.$);
      if (j.asJSM().peekLegalJumps().contains(Constants.$))
        blj.add(Constants.$);
      return $.append(names(blj)).toString();
    }
  }

  class MembersComputer {
    private final Set<String> apiTypeNames = new LinkedHashSet<>();

    public void compute() {
      if (!bnf.isSubBNF)
        compute$Type();
      computeStaticMethods();
      compute$$$Type();
    }
    private void computeStaticMethods() {
      for (Verb v : analyzer.firstSetOf(startSymbol))
        computeStaticMethod(v);
    }
    private void computeStaticMethod(Verb v) {
      Set<Verb> blj = new LinkedHashSet<>();
      blj.add(Constants.$);
      J j = RLLPConcrete.jnext(new JSM(bnf, analyzer, startSymbol, blj), v);
      computeType(j, v, x -> namer.name(x), () -> "ϱ");
      // NOTE should be applicable only for $ jumps
      Function<Verb, String> unknownSolution = !bnf.isSubBNF ? x -> {
        assert Constants.$.equals(x);
        return "$";
      } : x -> {
        assert Constants.$.equals(x);
        return "$$$";
      };
      Supplier<String> emptySolution = !bnf.isSubBNF ? () -> "$" : () -> "$$$";
      staticMethods.add(new StringBuilder("public static ") //
          .append(computeType(j, v, unknownSolution, emptySolution)) //
          .append(" ").append(v.terminal.name()).append("(").append(parametersEncoding(v.type)) //
          .append("){").append("$$$ $$$ = new $$$();$$$.recordTerminal(" //
              + v.terminal.getClass().getCanonicalName() + "." + v.terminal.name() //
              + (v.type.length == 0 ? "" : ",") + parameterNamesEncoding(v.type) + ");return $$$;}") //
          .toString());
    }
    private String computeType(J j, Verb origin, Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      assert j != JJAMMED;
      if (j == JUNKNOWN)
        return unknownSolution.apply(origin);
      if (j.isEmpty())
        return emptySolution.get();
      J jpeek = j.toPush.isEmpty() ? j.trim1() : j.trim();
      String $ = namer.name(jpeek);
      JSM jsmTemplate = j.toPush.isEmpty() ? j.address.pop() : j.address;
      if (apiTypeNames.contains($))
        return $ + computeTemplates(jsmTemplate, unknownSolution, emptySolution);
      apiTypeNames.add($);
      JSM jsm = jpeek.asJSM();
      Set<Verb> lj = jpeek.address.baseLegalJumps();
      StringBuilder t = new StringBuilder("public interface ").append($);
      StringBuilder template = new StringBuilder("<ϱ");
      List<String> templates = new ArrayList<>();
      for (Verb v : lj)
        // NOTE an optimization for $ jump
        if (!Constants.$.equals(v))
          templates.add(namer.name(v));
      if (!templates.isEmpty()) {
        template.append(",");
        template.append(String.join(",", templates));
      }
      t.append(template.append(">"));
      if (bnf.isSubBNF && jsm.peekLegalJumps().contains(Constants.$))
        t.append(" extends ").append(ASTNode.class.getCanonicalName());
      t.append("{");
      // NOTE further filtering is done in {@link RLLPEncoder#computeMethod}
      for (Verb v : bnf.verbs)
        // NOTE $ method is built below
        if (!Constants.$.equals(v))
          t.append(computeMethod(jsm, v, unknownSolution, emptySolution));
      if (!bnf.isSubBNF && jsm.peekLegalJumps().contains(Constants.$))
        t.append(packagePath + "." + astTopClass + "." + startSymbol.name()).append(" $();");
      apiTypes.add(t.append("}").toString());
      return $ + computeTemplates(jsmTemplate, unknownSolution, emptySolution);
    }
    private String computeTemplates(JSM jsm, Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      assert JAMMED != jsm && UNKNOWN != jsm;
      StringBuilder $ = new StringBuilder("<");
      List<String> templates = new ArrayList<>();
      if (jsm.isEmpty()) {
        $.append(emptySolution.get());
        for (Verb v : jsm.baseLegalJumps())
          if (!Constants.$.equals(v))
            templates.add(unknownSolution.apply(v));
        if (!templates.isEmpty())
          $.append(",").append(String.join(",", templates));
        return $.append(">").toString();
      }
      // NOTE can send null as origin verb as sent J cannot be JUNKNOWN
      $.append(computeType(J.of(jsm), null, unknownSolution, emptySolution));
      for (Verb v : jsm.trim().baseLegalJumps())
        if (!Constants.$.equals(v))
          templates.add(computeType(jsm.jjumpFirstAvailable(v), v, unknownSolution, emptySolution));
      if (!templates.isEmpty())
        $.append(",").append(String.join(",", templates));
      return $.append(">").toString();
    }
    private String computeMethod(JSM jsm, Verb v, Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      if (jsm == JAMMED)
        return "";
      if (jsm == UNKNOWN)
        return "public " + namer.name(v) + " " + v.terminal.name() + "(" + parametersEncoding(v.type) + ");";
      if (jsm.isEmpty())
        return "public ϱ " + v.terminal.name() + "(" + parametersEncoding(v.type) + ");";
      Symbol top = jsm.peek();
      J jnext = RLLPConcrete.jnext(jsm, v);
      if (JJAMMED == jnext)
        return "";
      return top.isVerb() && !top.asVerb().equals(v) ? "" //
          : "public " + computeType(jnext, v, unknownSolution, emptySolution) //
              + " " + v.terminal.name() + "(" + parametersEncoding(v.type) + ");";
    }
    private void compute$Type() {
      apiTypes.add(new StringBuilder("public interface ${") //
          .append(packagePath.toLowerCase() + "." + astTopClass + "." + startSymbol.name()).append(" $();}").toString());
      apiTypeNames.add("$");
    }
    private void compute$$$Type() {
      List<String> superInterfaces = new ArrayList<>(apiTypeNames);
      superInterfaces.add(ASTNode.class.getCanonicalName());
      StringBuilder $ = new StringBuilder("private static class $$$ extends ") //
          .append(FluentAPIRecorder.class.getCanonicalName()).append(" implements ") //
          .append(String.join(",", superInterfaces)).append("{").append(String.join("", //
              bnf.verbs.stream().filter(v -> v != Constants.$) //
                  .map(v -> "public $$$ " + v.terminal.name() + "(" //
                      + parametersEncoding(v.type) + "){recordTerminal(" //
                      + v.terminal.getClass().getCanonicalName() //
                      + "." + v.terminal.name() + (v.type.length == 0 ? "" : ",") //
                      + parameterNamesEncoding(v.type) + ");return this;}")
                  .collect(toList())));
      if (!bnf.isSubBNF)
        $.append("public ").append(packagePath + "." + astTopClass + "." + startSymbol.name()) //
            .append(" $(){return ast(" + packagePath + "." + astTopClass + ".class.getSimpleName());}");
      $.append("$$$(){super(new " + provider.getCanonicalName() + "().bnf().ebnf()");
      if (bnf.isSubBNF)
        $.append(".makeSubBNF(").append(startSymbol.getClass().getCanonicalName() + "." + startSymbol.name()).append(")");
      $.append(",\"" + packagePath + "\");}");
      apiTypes.add($.append("}").toString());
    }
    private String parametersEncoding(ParameterType[] type) {
      List<String> $ = new ArrayList<>();
      for (int i = 0; i < type.length; ++i)
        $.add((type[i] instanceof NestedType ? ASTNode.class.getCanonicalName() : type[i].toParameterString()) + " arg" + (i + 1));
      return String.join(",", $);
    }
    private String parameterNamesEncoding(ParameterType[] type) {
      List<String> $ = new ArrayList<>();
      for (int i = 0; i < type.length; ++i)
        $.add("arg" + (i + 1));
      return String.join(",", $);
    }
  }
}
