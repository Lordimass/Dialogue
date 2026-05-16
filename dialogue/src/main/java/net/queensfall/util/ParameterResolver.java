package net.queensfall.util;

@FunctionalInterface
public interface ParameterResolver<C> {
    String resolve(C context);
}
