package net.queensfall.dialog.event;

public sealed interface DialogEvent
        permits NextDialogEvent, DialogInputReceivedEvent, ChoiceSelectedEvent {

    DialogEventContext context();
}

