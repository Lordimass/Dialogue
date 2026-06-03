package net.lordimass.dialogue.parameter.replacementParameter;

import net.lordimass.dialogue.parameter.ParameterContext;
import net.lordimass.dialogue.parameter.ParameterProcessor;

public final class ReplacementParameterProcessor<C> extends ParameterProcessor<C> {
    private final ReplacementParameterResolver<C> resolver;

    public ReplacementParameterProcessor(String key, Class<C> contextType, ReplacementParameterResolver<C> resolver) {
        super(key, contextType, resolver);
        this.resolver = resolver;
    }

    public String key() {
        return key;
    }

    public boolean supports(ParameterContext ctx) {
        return ctx.has(contextType);
    }

    public String resolve(ParameterContext ctx) {
        return resolver.resolve(ctx.get(contextType));
    }
}