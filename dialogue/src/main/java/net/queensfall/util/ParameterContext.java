package net.queensfall.util;

import java.util.HashMap;
import java.util.Map;

public final class ParameterContext {

    private final Map<Class<?>, Object> contexts = new HashMap<>();

    public <C> void put(Class<C> type, C instance) {
        contexts.put(type, instance);
    }

    public <C> C get(Class<C> type) {
        return type.cast(contexts.get(type));
    }

    public boolean has(Class<?> type) {
        return contexts.containsKey(type);
    }
}