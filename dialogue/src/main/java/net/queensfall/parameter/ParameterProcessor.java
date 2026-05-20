package net.queensfall.parameter;

public final class ParameterProcessor<C> {

    private final String key;
    private final Class<C> contextType;
    private final ParameterResolver<C> resolver;

    public ParameterProcessor(String key, Class<C> contextType, ParameterResolver<C> resolver) {
        this.key = key;
        this.contextType = contextType;
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