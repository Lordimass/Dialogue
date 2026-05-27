package net.lordimass.dialogue.parameter;

@FunctionalInterface
public interface ParameterResolver<C> {
    String resolve(C context);
}
