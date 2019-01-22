package fling.compiler;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.stream.Collectors;

import fling.compiler.ast.FluentAPINode;
import fling.compiler.ast.MethodNode;
import fling.compiler.ast.PolymorphicTypeNode;
import fling.sententials.Named;
import fling.sententials.Terminal;
import fling.sententials.Word;

public class CppAdapter<Q extends Named, Σ extends Terminal, Γ extends Named> implements PolymorphicLanguageAdapter<Q, Σ, Γ> {
  private final String startMethodName;
  private final String terminationMethodName;

  public CppAdapter(String startMethodName, String terminationMethodName) {
    this.startMethodName = startMethodName;
    this.terminationMethodName = terminationMethodName;
  }
  @Override public String printTopType() {
    return "TOP";
  }
  @Override public String printBotType() {
    return "BOT";
  }
  @Override public String printIntermediateType(Compiler<Q, Σ, Γ>.TypeName name) {
    return printTypeName(name);
  }
  @Override public String printIntermediateType(Compiler<Q, Σ, Γ>.TypeName name,
      List<PolymorphicTypeNode<Compiler<Q, Σ, Γ>.TypeName>> typeArguments) {
    return String.format("%s<%s>", //
        printTypeName(name), //
        typeArguments.stream().map(this::printType).collect(joining(",")));
  }
  @Override public String printStartMethod(PolymorphicTypeNode<Compiler<Q, Σ, Γ>.TypeName> returnType) {
    return String.format("%s* %s() {return nullptr;}", printType(returnType), startMethodName);
  }
  @Override public String printTerminationMethod() {
    return String.format("virtual void %s() const;", terminationMethodName);
  }
  @Override public String printIntermediateMethod(Compiler<Q, Σ, Γ>.MethodDeclaration declaration,
      PolymorphicTypeNode<Compiler<Q, Σ, Γ>.TypeName> returnType) {
    return String.format("virtual %s %s(%s) const;", printType(returnType), declaration.name.name(),
        String.join(",", declaration.name.parameters()));
  }
  @Override public String printTopInterface() {
    return String.format("class TOP{public:virtual void %s() const;};", terminationMethodName);
  }
  @Override public String printBotInterface() {
    return "class BOT{};";
  }
  @Override public String printInterface(Compiler<Q, Σ, Γ>.InterfaceDeclaration declaration,
      List<MethodNode<Compiler<Q, Σ, Γ>.TypeName, Compiler<Q, Σ, Γ>.MethodDeclaration>> methods) {
    return String.format("%s{public:%s};", //
        printInterfaceDeclaration(declaration), //
        methods.stream().map(m -> printMethod(m)).collect(joining()));
  }
  @Override public String printFluentAPI(
      FluentAPINode<Compiler<Q, Σ, Γ>.TypeName, Compiler<Q, Σ, Γ>.MethodDeclaration, Compiler<Q, Σ, Γ>.InterfaceDeclaration> fluentAPI) {
    return String.format("%s%s%s", //
        fluentAPI.interfaces.stream().filter(i -> !i.isTop() && !i.isBot()).map(i -> printInterfaceDeclaration(i.declaration) + ";")
            .collect(joining()), //
        fluentAPI.interfaces.stream().map(i -> printInterface(i)).collect(joining()), //
        fluentAPI.startMethods.stream().map(m -> printMethod(m)).collect(joining()));
  }
  public String printTypeName(Compiler<Q, Σ, Γ>.TypeName name) {
    return printTypeName(name.q, name.α);
  }
  public String printTypeName(Q q, Word<Γ> α) {
    return α == null ? q.name() : String.format("%s_%s", q.name(), α.stream().map(Named::name).collect(Collectors.joining()));
  }
  public String printInterfaceDeclaration(Compiler<Q, Σ, Γ>.InterfaceDeclaration declaration) {
    return declaration.typeVariables.isEmpty() ? String.format("class %s", printTypeName(declaration.q, declaration.α))
        : String.format("template<%s>class %s",
            declaration.typeVariables.stream().map(q -> "class " + q.name()).collect(Collectors.joining(",")), //
            printTypeName(declaration.q, declaration.α));
  }
}