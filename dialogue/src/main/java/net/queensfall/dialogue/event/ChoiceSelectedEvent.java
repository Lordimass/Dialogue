package net.queensfall.dialogue.event;

import net.queensfall.dialogue.DialogueAsset;
import net.queensfall.dialogue.DialogueEntry;

public record ChoiceSelectedEvent(
        DialogueEventContext context,
        DialogueAsset asset,
        int selectedIndex,
        DialogueEntry selectedEntry
) implements DialogueEvent {}
