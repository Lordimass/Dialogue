package net.lordimass.dialogue.parameter.eventTag;

import net.lordimass.dialogue.parameter.ParameterContext;

public class EventTagParameterContext extends ParameterContext {
    public final String token;
    public EventTagParameterContext(String token) {
        this.token = token;
    }

    public static EventTagParameterContext parameterContextAs(ParameterContext ctx) {
        if (ctx instanceof EventTagParameterContext) {
            return (EventTagParameterContext) ctx;
        }
        throw new IllegalArgumentException("Context must be an instance of EventTagParameterContext!");
    }
}
