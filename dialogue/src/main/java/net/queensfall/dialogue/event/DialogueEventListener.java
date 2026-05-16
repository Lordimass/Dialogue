package net.queensfall.dialogue.event;

@FunctionalInterface
public interface DialogueEventListener<T extends DialogueEvent> {
    void onEvent(T event);
}