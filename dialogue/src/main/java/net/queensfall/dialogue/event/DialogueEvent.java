package net.queensfall.dialogue.event;

public sealed interface DialogueEvent
        permits NextDialogueEvent, DialogueInputReceivedEvent, ChoiceSelectedEvent {

    DialogueEventContext context();
}

