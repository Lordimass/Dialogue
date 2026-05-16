package net.queensfall.dialog.event;

import net.queensfall.dialog.DialogAsset;
import net.queensfall.dialog.DialogEntry;

public record ChoiceSelectedEvent(
        DialogEventContext context,
        DialogAsset asset,
        int selectedIndex,
        DialogEntry selectedEntry
) implements DialogEvent {}
