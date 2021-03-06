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
 * {@link APICompiler} generating (possibly) exponential number of API types.
 * Supported method chains compiles only when prefix of legal word.
 *
 * @author Ori Roth
 */
public class ReliableAPICompiler extends APICompiler {
  public ReliableAPICompiler(DPDA<Named, Verb, Named> dpda) {
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
              consolidate(δ.q$, //
                  dpda.γ0.pop().push(δ.getΑ()), //
                  new LinkedHashSet<>(dpda.Q().filter(dpda::isAccepting).collect(toList())), //
                  true));
      if (!startMethod.returnType.isBot())
        $.add(startMethod);
    }
    return $;
  }
  @Override protected List<InterfaceNode<TypeName, MethodDeclaration, InterfaceDeclaration>> compileInterfaces() {
    return list( //
        fixedInterfaces(), //
        types.values().stream().filter(interfaze -> !interfaze.isBot()).collect(toList()));
  }
  @SuppressWarnings("static-method") private List<InterfaceNode<TypeName, MethodDeclaration, InterfaceDeclaration>> fixedInterfaces() {
    return Arrays.asList(InterfaceNode.top());
  }
  @SuppressWarnings("unused") @Override protected ConcreteImplementationNode<TypeName, MethodDeclaration> complieConcreteImplementation() {
    return new ConcreteImplementationNode<>(dpda.Σ() //
        .filter(σ -> Constants.$$ != σ) //
        .map(σ -> new AbstractMethodNode.Chained<TypeName, MethodDeclaration>(new MethodDeclaration(σ))) //
        .collect(toList()));
  }
  /**
   * Get type name given a state and stack symbols to push. If this type is not
   * present, it is created.
   *
   * @param q current state
   * @param α current stack symbols to be pushed
   * @return type name
   */
  private TypeName encodedName(final Named q, final Word<Named> α, Set<Named> legalJumps) {
    final TypeName $ = new TypeName(q, α, legalJumps);
    if (types.containsKey($))
      return types.get($).isBot() ? null : $;
    types.put($, shallowIsBot($) ? //
        InterfaceNode.bot() : //
        InterfaceNode.top()); // Pending computation.
    InterfaceNode<TypeName, MethodDeclaration, InterfaceDeclaration> body = encodedBody(q, α, legalJumps);
    types.put($, body == null ? InterfaceNode.bot() : body);
    return types.get($).isBot() ? null : $;
  }
  private boolean shallowIsBot(TypeName type) {
    if (dpda.isAccepting(type.q))
      return false;
    for (Verb σ : dpda.Σ) {
      δ<Named, Verb, Named> δ = dpda.δδ(type.q, σ, type.α);
      if (δ == null)
        continue;
      if (!δ.getΑ().isEmpty())
        // Consuming transition.
        return false;
      if (type.legalJumps.contains(δ.q$))
        // Legal epsilon-transition is possible.
        return false;
    }
    return true;
  }
  private InterfaceNode<TypeName, MethodDeclaration, InterfaceDeclaration> encodedBody(final Named q, final Word<Named> α,
      Set<Named> legalJumps) {
    List<AbstractMethodNode<TypeName, MethodDeclaration>> $ = new ArrayList<>();
    $.addAll(dpda.Σ().map(σ -> //
    new AbstractMethodNode.Intermediate<>(new MethodDeclaration(σ), next(q, α, legalJumps, σ))) //
        .filter(method -> !method.returnType.isBot()) //
        .collect(toList()));
    if (dpda.isAccepting(q))
      $.add(new AbstractMethodNode.Termination<>());
    return $.isEmpty() ? null
        : new InterfaceNode<>(new InterfaceDeclaration(q, α, legalJumps, word(legalJumps), dpda.isAccepting(q)), //
            Collections.unmodifiableList($));
  }
  /**
   * Computes the type representing the state of the automaton after consuming
   * an input letter.
   *
   * @param q current state
   * @param α all known information about the top of the stack
   * @param legalJumps
   * @param σ current input letter
   * @return next state type
   */
  private PolymorphicTypeNode<TypeName> next(final Named q, final Word<Named> α, Set<Named> legalJumps, final Verb σ) {
    final δ<Named, Verb, Named> δ = dpda.δδ(q, σ, α.top());
    return δ == null ? PolymorphicTypeNode.bot() : common(δ, α.pop(), legalJumps, false);
  }
  private PolymorphicTypeNode<TypeName> consolidate(final Named q, final Word<Named> α, Set<Named> legalJumps,
      boolean isInitialType) {
    final δ<Named, Verb, Named> δ = dpda.δδ(q, ε(), α.top());
    if (δ == null) {
      TypeName name = encodedName(q, α, legalJumps);
      return name == null ? bot() : //
          new PolymorphicTypeNode<>(name, getTypeArguments(legalJumps, isInitialType));
    }
    return common(δ, α.pop(), legalJumps, isInitialType);
  }
  private PolymorphicTypeNode<TypeName> common(final δ<Named, Verb, Named> δ, final Word<Named> α, Set<Named> legalJumps,
      boolean isInitialType) {
    if (α.isEmpty()) {
      if (δ.getΑ().isEmpty())
        return getTypeArgument(δ, legalJumps, isInitialType);
      TypeName name = encodedName(δ.q$, δ.getΑ(), legalJumps);
      return name == null ? bot() : //
          new PolymorphicTypeNode<>(name, getTypeArguments(legalJumps, isInitialType));
    }
    if (δ.getΑ().isEmpty())
      return consolidate(δ.q$, α, legalJumps, isInitialType);
    Map<Named, PolymorphicTypeNode<TypeName>> typeArguments = new LinkedHashMap<>();
    for (Named q : dpda.Q) {
      PolymorphicTypeNode<TypeName> argument = consolidate(q, α, legalJumps, isInitialType);
      if (!argument.isBot())
        typeArguments.put(q, argument);
    }
    TypeName name = encodedName(δ.q$, δ.getΑ(), typeArguments.keySet());
    return name == null ? bot() : //
        new PolymorphicTypeNode<>( //
            encodedName(δ.q$, δ.getΑ(), typeArguments.keySet()), //
            new ArrayList<>(typeArguments.values()));
  }
  private PolymorphicTypeNode<TypeName> getTypeArgument(final δ<Named, Verb, Named> δ, Set<Named> legalJumps,
      boolean isInitialType) {
    return !legalJumps.contains(δ.q$) ? bot() : //
        isInitialType ? top() : //
            typeVariables.get(δ.q$);
  }
  private List<PolymorphicTypeNode<TypeName>> getTypeArguments(Set<Named> legalJumps, boolean isInitialType) {
    return !isInitialType ? //
        dpda.Q() //
            .filter(legalJumps::contains) //
            .map(typeVariables::get) //
            .collect(toList()) //
        : legalJumps.stream() //
            .map(q -> PolymorphicTypeNode.<TypeName> top()) //
            .collect(toList());
  }
}
