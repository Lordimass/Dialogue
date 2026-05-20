package net.queensfall.parameter;

@FunctionalInterface
public interface ParameterResolver<C> {
    String resolve(C context);
}
