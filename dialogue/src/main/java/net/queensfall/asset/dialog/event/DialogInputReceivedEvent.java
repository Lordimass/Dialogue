package net.queensfall.asset.dialog.event;

import net.queensfall.asset.dialog.HyspeechDialogAsset;

public record DialogInputReceivedEvent(
        DialogEventContext context,
        HyspeechDialogAsset asset,
        String input
) implements DialogEvent {}
