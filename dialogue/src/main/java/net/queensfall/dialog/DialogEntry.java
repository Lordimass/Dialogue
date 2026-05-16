package net.queensfall.dialog;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import net.queensfall.macro.MacroAsset;

import javax.annotation.Nonnull;

public class DialogEntry {
    public static final BuilderCodec<DialogEntry> CODEC =
            BuilderCodec
                    .builder(DialogEntry.class, DialogEntry::new)
                    .append(
                            new KeyedCodec<>("Content", Codec.STRING),
                            (obj, val) -> obj.content = val,
                            obj -> obj.content
                    )
                    .addValidator(Validators.nonNull())
                    .add()
                    .append(
                            new KeyedCodec<>("Next", Codec.STRING),
                            (obj, val) -> obj.next = val,
                            obj -> obj.next
                    )
                    .add()
                    .append(
                            new KeyedCodec<>("DialogueMod Macro", MacroAsset.CODEC),
                            (obj, val) -> obj.macro = val,
                            obj -> obj.macro
                    )
                    .add()
                    .build();

    public String content;
    public String next;
    public MacroAsset macro;

    public DialogEntry(String content, String next) {
        this.content = content;
        this.next = next;
    }

    protected DialogEntry() {
    }

    public String getContent() {
        return this.content;
    }

    public String getNext() {
        return this.next;
    }

    public MacroAsset getMacro() {
        return this.macro;
    }

    @Nonnull
    public String toString() {
        return "DialogEntry{next=" + this.next + ", content=" + this.content + "}";
    }
}