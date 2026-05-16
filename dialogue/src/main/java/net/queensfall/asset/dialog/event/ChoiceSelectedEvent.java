package net.queensfall.asset.dialog.event;

import net.queensfall.asset.dialog.HyspeechDialogAsset;
import net.queensfall.asset.dialog.HyspeechDialogEntry;

public record ChoiceSelectedEvent(
        DialogEventContext context,
        HyspeechDialogAsset asset,
        int selectedIndex,
        HyspeechDialogEntry selectedEntry
) implements DialogEvent {}
