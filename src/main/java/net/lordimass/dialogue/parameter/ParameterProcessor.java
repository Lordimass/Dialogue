package net.lordimass.dialogue.parameter;

public abstract class ParameterProcessor<C> {
    protected final String key;
    protected final Class<C> contextType;
    protected final ParameterResolver<C> resolver;

    public ParameterProcessor(String key, Class<C> contextType, ParameterResolver<C> resolver) {
        this.key = key;
        this.contextType = contextType;
        this.resolver = resolver;
    }

    abstract public boolean supports(ParameterContext ctx);
    abstract public String resolve(ParameterContext ctx);
}
