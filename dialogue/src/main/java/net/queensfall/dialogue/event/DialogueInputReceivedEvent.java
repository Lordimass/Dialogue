package net.queensfall.dialogue.event;

import net.queensfall.dialogue.DialogueAsset;

public record DialogueInputReceivedEvent(
        DialogueEventContext context,
        DialogueAsset asset,
        String input
) implements DialogueEvent {}
