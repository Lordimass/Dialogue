package net.queensfall.dialog.event;

@FunctionalInterface
public interface DialogEventListener<T extends DialogEvent> {
    void onEvent(T event);
}