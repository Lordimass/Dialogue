package net.queensfall.player.ui.page;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import javax.annotation.Nullable;

public class DialogPageData {
    public static final BuilderCodec<DialogPageData> CODEC =
            BuilderCodec.builder(DialogPageData.class, DialogPageData::new)
                    .append(
                            new KeyedCodec<>("DialogNext", Codec.STRING),
                            (entry, s) -> entry.dialogNext = "true".equalsIgnoreCase(s),
                            entry -> Boolean.TRUE.equals(entry.dialogNext) ? "true" : null
                    )
                    .add()
                    .append(
                            new KeyedCodec<>("InputNext", Codec.STRING),
                            (entry, s) -> entry.inputNext = "true".equalsIgnoreCase(s),
                            entry -> Boolean.TRUE.equals(entry.inputNext) ? "true" : null
                    )
                    .add()
                    .append(
                            new KeyedCodec<>("Choice0", Codec.STRING),
                            (entry, s) -> entry.firstChoice = "true".equalsIgnoreCase(s),
                            entry -> Boolean.TRUE.equals(entry.firstChoice) ? "true" : null
                    )
                    .add()
                    .append(
                            new KeyedCodec<>("Choice1", Codec.STRING),
                            (entry, s) -> entry.secondChoice = "true".equalsIgnoreCase(s),
                            entry -> Boolean.TRUE.equals(entry.secondChoice) ? "true" : null
                    )
                    .add()
                    .append(
                            new KeyedCodec<>("Choice2", Codec.STRING),
                            (entry, s) -> entry.thirdChoice = "true".equalsIgnoreCase(s),
                            entry -> Boolean.TRUE.equals(entry.thirdChoice) ? "true" : null
                    )
                    .add()
                    .append(
                            new KeyedCodec<>("Choice3", Codec.STRING),
                            (entry, s) -> entry.fourthChoice = "true".equalsIgnoreCase(s),
                            entry -> Boolean.TRUE.equals(entry.fourthChoice) ? "true" : null
                    )
                    .add()
                    .append(
                            new KeyedCodec<>("@Input", Codec.STRING),
                            (entry, s) -> entry.input = s,
                            entry -> entry.input
                    )
                    .add()
                    .build();

    @Nullable
    private Boolean dialogNext;

    @Nullable
    private Boolean inputNext;

    @Nullable
    private Boolean firstChoice;
    @Nullable
    private Boolean secondChoice;
    @Nullable
    private Boolean thirdChoice;
    @Nullable
    private Boolean fourthChoice;

    @Nullable
    private String input;

    public String input() {
        return this.input;
    }

    public Boolean dialogNext() {
        return this.dialogNext;
    }

    public Boolean inputNext() {
        return this.inputNext;
    }



    public Boolean firstChoice() {
        return this.firstChoice;
    }

    public Boolean secondChoice() {
        return this.secondChoice;
    }

    public Boolean thirdChoice() {
        return this.thirdChoice;
    }

    public Boolean fourthChoice() {
        return this.fourthChoice;
    }

    public Boolean getEntry(int i) {
        return switch (i) {
            case 0 -> firstChoice();
            case 1 -> secondChoice();
            case 2 -> thirdChoice();
            case 3 -> fourthChoice();
            default -> null;
        };
    }
}
