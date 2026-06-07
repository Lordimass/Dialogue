package net.lordimass.dialogue.codec;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import lombok.Getter;
import lombok.Setter;
import net.lordimass.dialogue.DialogueMod;

import javax.annotation.Nullable;

public class CharacterAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, CharacterAsset>> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static final BuilderCodec<CharacterAsset> CODEC =
        BuilderCodec.builder(CharacterAsset.class, CharacterAsset::new)
            .append(
                new KeyedCodec<>("Name", Codec.STRING),
                CharacterAsset::setName, CharacterAsset::getName
            )
            .documentation("The name of the character as it should display in the title of the dialogue box.")
            .add()
            .append(new KeyedCodec<>("Voice", Codec.STRING),
                CharacterAsset::setVoice, obj -> obj.voice
            )
            .documentation("The ID of the voice to use for this Dialogue. If undefined, it will " +
                "choose a voice based on the title of the dialogue, so dialogues with the same " +
                "title will always have the same voice. Set to the empty string to disable voice." +
                "This only works if TypewriterEffect has not been disabled.")
            .add()
            .append(
                new KeyedCodec<>("Profile", Codec.STRING),
                CharacterAsset::setProfile, CharacterAsset::getProfile
            )
            .documentation("The image to use as the 'profile' image of the character. Will " +
                "display beside their dialogue box when they are speaking. This should be a UI " +
                "image asset in Common/UI/Custom/**/*.")
            .add()
            .build();

    public static final AssetBuilderCodec<String, CharacterAsset> ASSET_BUILDER_CODEC =
        AssetBuilderCodec.wrap(
            CharacterAsset.CODEC,
            Codec.STRING,
            (asset, s) -> asset.id = s,
            asset -> asset.id,
            (asset, data) -> asset.extraData = data,
            asset -> asset.extraData
        );
    private static AssetStore<String, CharacterAsset, DefaultAssetMap<String, CharacterAsset>> ASSET_STORE;
    private AssetExtraInfo.Data extraData;

    @Getter private String id;
    @Getter @Setter private String name;
    @Setter private String voice;
    @Getter @Setter private String profile;

    protected CharacterAsset() {};

    public static AssetStore<String, CharacterAsset, DefaultAssetMap<String, CharacterAsset>> getAssetStore() {
        if (ASSET_STORE == null) ASSET_STORE = AssetRegistry.getAssetStore(CharacterAsset.class);
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, DialogueAsset> getAssetMap() {
        return DialogueAsset.getAssetStore().getAssetMap();
    }

    @Nullable
    public static CharacterAsset getAsset(String key) {
        CharacterAsset asset = AssetRegistry
            .getAssetStore(CharacterAsset.class)
            .getAssetMap()
            .getAsset(key);
        if (asset == null) LOGGER.atSevere().log("Character asset '"+key+"' could not be found");
        return asset;
    }

    /**
     * Get the voice to use for this dialogue.
     * <br><br>
     * If voice was set to the empty string in the JSON asset we return null, signifying that voice
     * functionality should be disabled.
     * <br><br>
     * If voice was not provided, we'll deterministically choose from one of the built-in voices
     * based on <code>this.title</code>
     */
    @Nullable
    public String getVoice() {
        if (voice != null) return voice.isEmpty() ? null : voice;
        if (this.name == null) return DialogueMod.BUILTIN_VOICE_IDS[0];
        int value = 0;
        for (char c : this.name.toCharArray()) {
            value += Character.getNumericValue(c);
        }
        voice = DialogueMod.BUILTIN_VOICE_IDS[value % DialogueMod.BUILTIN_VOICE_IDS.length];
        return voice;
    }
}
