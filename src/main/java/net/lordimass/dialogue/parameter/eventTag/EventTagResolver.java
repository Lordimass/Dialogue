package net.lordimass.dialogue.parameter.eventTag;

import net.lordimass.dialogue.parameter.ParameterResolver;

import java.util.Map;

@FunctionalInterface
public interface EventTagResolver<C> extends ParameterResolver<C> {
    boolean resolve(C context, Map<String, String> params);
}
