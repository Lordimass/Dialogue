package net.queensfall.dialogue.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DialogueEventBus {

    private final Map<String, Map<Class<?>, List<DialogueEventListener<?>>>> listeners = new HashMap<>();

    public <T extends DialogueEvent> void register(
            String dialogId,
            Class<T> eventType,
            DialogueEventListener<T> listener
    ) {
        listeners
                .computeIfAbsent(dialogId, k -> new HashMap<>())
                .computeIfAbsent(eventType, k -> new ArrayList<>())
                .add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends DialogueEvent> void dispatch(String dialogId, T event) {
        Map<Class<?>, List<DialogueEventListener<?>>> byType = listeners.get(dialogId);
        if (byType == null)
            return;

        List<DialogueEventListener<?>> handlers = byType.get(event.getClass());
        if (handlers == null)
            return;

        for (DialogueEventListener<?> handler : handlers) {
            ((DialogueEventListener<T>) handler).onEvent(event);
        }
    }
}