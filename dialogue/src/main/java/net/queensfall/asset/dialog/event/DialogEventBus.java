package net.queensfall.asset.dialog.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DialogEventBus {

    private final Map<String, Map<Class<?>, List<DialogEventListener<?>>>> listeners = new HashMap<>();

    public <T extends DialogEvent> void register(
            String dialogId,
            Class<T> eventType,
            DialogEventListener<T> listener
    ) {
        listeners
                .computeIfAbsent(dialogId, k -> new HashMap<>())
                .computeIfAbsent(eventType, k -> new ArrayList<>())
                .add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends DialogEvent> void dispatch(String dialogId, T event) {
        Map<Class<?>, List<DialogEventListener<?>>> byType = listeners.get(dialogId);
        if (byType == null)
            return;

        List<DialogEventListener<?>> handlers = byType.get(event.getClass());
        if (handlers == null)
            return;

        for (DialogEventListener<?> handler : handlers) {
            ((DialogEventListener<T>) handler).onEvent(event);
        }
    }
}