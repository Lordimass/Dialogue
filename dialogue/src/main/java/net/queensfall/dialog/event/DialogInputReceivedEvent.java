package net.queensfall.dialog.event;

import net.queensfall.dialog.DialogAsset;

public record DialogInputReceivedEvent(
        DialogEventContext context,
        DialogAsset asset,
        String input
) implements DialogEvent {}
