package net.queensfall.asset.dialog.event;

@FunctionalInterface
public interface DialogEventListener<T extends DialogEvent> {
    void onEvent(T event);
}