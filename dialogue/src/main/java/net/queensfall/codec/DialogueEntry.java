package net.queensfall.codec;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import lombok.Getter;
import net.queensfall.macro.MacroAsset;

import javax.annotation.Nonnull;

@Getter
public class DialogueEntry {
    public static final BuilderCodec<DialogueEntry> CODEC =
            BuilderCodec
                    .builder(DialogueEntry.class, DialogueEntry::new)
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

    public DialogueEntry(String content, String next) {
        this.content = content;
        this.next = next;
    }

    protected DialogueEntry() {
    }

    @Nonnull
    public String toString() {
        return "DialogueEntry{next=" + this.next + ", content=" + this.content + "}";
    }
}