package net.queensfall.dialogue.validator;

import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import net.queensfall.dialogue.DialogueAsset;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public class DialogExistsValidator extends AssetValidator {
    private static final DialogExistsValidator DEFAULT_INSTANCE = new DialogExistsValidator();

    private DialogExistsValidator() {
    }

    private DialogExistsValidator(EnumSet<Config> config) {
        super(config);
    }

    public static DialogExistsValidator required() {
        return DEFAULT_INSTANCE;
    }

    @Nonnull
    public static DialogExistsValidator withConfig(EnumSet<Config> config) {
        return new DialogExistsValidator(config);
    }

    @Override
    @Nonnull
    public String getDomain() {
        return "Dialogue";
    }

    @Override
    public boolean test(String marker) {
        return DialogueAsset.getAssetMap().getAsset(marker) != null;
    }

    @Override
    @Nonnull
    public String errorMessage(String marker, String attributeName) {
        return "The Dialogue asset with the name \"" + marker + "\" does not exist for attribute \"" + attributeName + "\"";
    }

    @Override
    @Nonnull
    public String getAssetName() {
        return DialogueAsset.class.getSimpleName();
    }
}
