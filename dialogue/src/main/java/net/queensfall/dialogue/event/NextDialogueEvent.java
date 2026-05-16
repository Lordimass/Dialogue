package net.queensfall.dialogue.event;

import net.queensfall.dialogue.DialogueAsset;

public record NextDialogueEvent(DialogueEventContext context, DialogueAsset asset)
        implements DialogueEvent {}
