package net.lordimass.dialogue.parameter.replacementParameter;

import net.lordimass.dialogue.parameter.ParameterResolver;

@FunctionalInterface
public interface ReplacementParameterResolver<C> extends ParameterResolver<C> {
    String resolve(C context);
}
