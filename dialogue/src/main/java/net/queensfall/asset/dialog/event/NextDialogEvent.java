package net.queensfall.asset.dialog.event;

import net.queensfall.asset.dialog.HyspeechDialogAsset;

public record NextDialogEvent(DialogEventContext context, HyspeechDialogAsset asset)
        implements DialogEvent {}
