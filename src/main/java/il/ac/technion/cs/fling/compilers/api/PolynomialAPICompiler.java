package il.ac.technion.cs.fling.compilers.api;

import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static il.ac.technion.cs.fling.internal.compiler.api.nodes.PolymorphicTypeNode.*;
import static il.ac.technion.cs.fling.internal.util.As.*;
import static java.util.stream.Collectors.toList;

import java.util.*;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.DPDA.δ;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler;
import il.ac.technion.cs.fling.internal.compiler.api.nodes.*;
import il.ac.technion.cs.fling.internal.grammar.sententials.*;

/**
 * {@link APICompiler} generating polynomial number of API types.
 * Supported method chains may terminated only when legal, yet illegal method
 * calls do not raise compilation errors, but return bottom type.
 *
 * @author Ori Roth
 */
public class PolynomialAPICompiler extends APICompiler {
  private PolynomialAPICompiler(DPDA<Named, Verb, Named> dpda) {
    super(dpda);
  }
  @Override protected List<AbstractMethodNode<TypeName, MethodDeclaration>> compileStartMethods() {
    List<AbstractMethodNode<TypeName, MethodDeclaration>> $ = new ArrayList<>();
    if (dpda.F.contains(dpda.q0))
      $.add(new AbstractMethodNode.Start<>(new MethodDeclaration(Constants.$$), //
          PolymorphicTypeNode.top()));
    for (Verb σ : dpda.Σ) {
      δ<Named, Verb, Named> δ = dpda.δ(dpda.q0, σ, dpda.γ0.top());
      if (δ == null)
        continue;
      AbstractMethodNode.Start<TypeName, MethodDeclaration> startMethod = //
          new AbstractMethodNode.Start<>(new MethodDeclaration(σ), //
              consolidate(δ.q$, dpda.γ0.pop().push(δ.getΑ()), true));
      if (!startMethod.returnType.isBot())
        $.add(startMethod);
    }
    return $;
  }
  @Override protected List<InterfaceNode<TypeName, MethodDeclaration, InterfaceDeclaration>> compileInterfaces() {
    return list(fixedInterfaces(), types.values());
  }
  @SuppressWarnings("unused") @Override protected ConcreteImplementationNode<TypeName, MethodDeclaration> complieConcreteImplementation() {
    return new ConcreteImplementationNode<>(dpda.Σ() //
        .filter(σ -> Constants.$$ != σ) //
        .map(σ -> new AbstractMethodNode.Chained<TypeName, MethodDeclaration>(new MethodDeclaration(σ))) //
        .collect(toList()));
  }
  @SuppressWarnings("static-method") private List<InterfaceNode<TypeName, MethodDeclaration, InterfaceDeclaration>> fixedInterfaces() {
    return Arrays.asList(InterfaceNode.top(), InterfaceNode.bot());
  }
  /**
   * Get type name given a state and stack symbols to push. If this type is not
   * present, it is created.
   *
   * @param q current state
   * @param α current stack symbols to be pushed
   * @return type name
   */
  private TypeName encodedName(final Named q, final Word<Named> α) {
    final TypeName $ = new TypeName(q, α, null);
    if (types.containsKey($))
      return $;
    types.put($, null); // Pending computation.
    types.put($, encodedBody(q, α));
    return $;
  }
  private InterfaceNode<TypeName, MethodDeclaration, InterfaceDeclaration> encodedBody(final Named q, final Word<Named> α) {
    List<AbstractMethodNode<TypeName, MethodDeclaration>> $ = new ArrayList<>();
    $.addAll(dpda.Σ().map(σ -> //
    new AbstractMethodNode.Intermediate<>(new MethodDeclaration(σ), next(q, α, σ))).collect(toList()));
    if (dpda.isAccepting(q))
      $.add(new AbstractMethodNode.Termination<>());
    return new InterfaceNode<>(new InterfaceDeclaration(q, α, null, word(dpda.Q), dpda.isAccepting(q)), //
        Collections.unmodifiableList($));
  }
  /**
   * Computes the type representing the state of the automaton after consuming
   * an input letter.
   *
   * @param q current state
   * @param α all known information about the top of the stack
   * @param σ current input letter
   * @return next state type
   */
  private PolymorphicTypeNode<TypeName> next(final Named q, final Word<Named> α, final Verb σ) {
    final δ<Named, Verb, Named> δ = dpda.δδ(q, σ, α.top());
    return δ == null ? PolymorphicTypeNode.bot() : common(δ, α.pop(), false);
  }
  private PolymorphicTypeNode<TypeName> consolidate(final Named q, final Word<Named> α, boolean isInitialType) {
    final δ<Named, Verb, Named> δ = dpda.δδ(q, ε(), α.top());
    return δ == null ? new PolymorphicTypeNode<>(encodedName(q, α), getTypeArguments(isInitialType))
        : common(δ, α.pop(), isInitialType);
  }
  private PolymorphicTypeNode<TypeName> common(final δ<Named, Verb, Named> δ, final Word<Named> α, boolean isInitialType) {
    if (α.isEmpty()) {
      if (δ.getΑ().isEmpty())
        return getTypeArgument(δ, isInitialType);
      return new PolymorphicTypeNode<>(encodedName(δ.q$, δ.getΑ()), getTypeArguments(isInitialType));
    }
    if (δ.getΑ().isEmpty())
      return consolidate(δ.q$, α, isInitialType);
    return new PolymorphicTypeNode<>(encodedName(δ.q$, δ.getΑ()), //
        dpda.Q().map(q -> consolidate(q, α, isInitialType)).collect(toList()));
  }
  private PolymorphicTypeNode<TypeName> getTypeArgument(final δ<Named, Verb, Named> δ, boolean isInitialType) {
    return !isInitialType ? typeVariables.get(δ.q$) : dpda.isAccepting(δ.q$) ? top() : bot();
  }
  private List<PolymorphicTypeNode<TypeName>> getTypeArguments(boolean isInitialType) {
    return !isInitialType ? list(typeVariables.values())
        : dpda.Q().map(q$ -> dpda.isAccepting(q$) ? PolymorphicTypeNode.<TypeName> top() : PolymorphicTypeNode.<TypeName> bot())
            .collect(toList());
  }
}
