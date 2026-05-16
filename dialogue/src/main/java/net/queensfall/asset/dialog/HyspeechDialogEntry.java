package net.queensfall.asset.dialog;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import net.queensfall.asset.macro.HyspeechMacroAsset;

import javax.annotation.Nonnull;

public class HyspeechDialogEntry {
    public static final BuilderCodec<HyspeechDialogEntry> CODEC =
            BuilderCodec
                    .builder(HyspeechDialogEntry.class, HyspeechDialogEntry::new)
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
                            new KeyedCodec<>("DialogueMod Macro", HyspeechMacroAsset.CODEC),
                            (obj, val) -> obj.macro = val,
                            obj -> obj.macro
                    )
                    .add()
                    .build();

    public String content;
    public String next;
    public HyspeechMacroAsset macro;

    public HyspeechDialogEntry(String content, String next) {
        this.content = content;
        this.next = next;
    }

    protected HyspeechDialogEntry() {
    }

    public String getContent() {
        return this.content;
    }

    public String getNext() {
        return this.next;
    }

    public HyspeechMacroAsset getMacro() {
        return this.macro;
    }

    @Nonnull
    public String toString() {
        return "HyspeechDialogEntry{next=" + this.next + ", content=" + this.content + "}";
    }
}