package net.queensfall.dialog.event;

import net.queensfall.dialog.DialogAsset;

public record NextDialogEvent(DialogEventContext context, DialogAsset asset)
        implements DialogEvent {}
