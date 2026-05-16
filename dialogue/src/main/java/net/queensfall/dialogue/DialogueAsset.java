package net.queensfall.dialogue;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import net.queensfall.macro.MacroAsset;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class DialogueAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, DialogueAsset>> {

    public static final EnumCodec<DialogueType> DIALOG_TYPE_ENUM_CODEC = new EnumCodec<>(DialogueType.class);

    public static final AssetBuilderCodec<String, DialogueAsset> CODEC =
            AssetBuilderCodec
                    .builder(
                            DialogueAsset.class,
                            DialogueAsset::new,
                            Codec.STRING,
                            (asset, s) -> asset.id = s,
                            asset -> asset.id,
                            (asset, data) -> asset.extraData = data,
                            asset -> asset.extraData
                    )
                    .append(
                            new KeyedCodec<>("Type", DIALOG_TYPE_ENUM_CODEC),
                            (obj, val) -> obj.type = val,
                            obj -> obj.type
                    )
                    .add()
                    .append(
                            new KeyedCodec<>("Entries", new ArrayCodec<>(DialogueEntry.CODEC, DialogueEntry[]::new)),
                            (asset, entries) -> {
                                asset.entries = entries;
                            },
                            asset -> asset.entries
                    )
                    .documentation("Content of the dialogue.\n\nThis will eventually be replaced with multiline components.")
                    .add()
                    .append(
                            new KeyedCodec<>("Next", Codec.STRING),
                            (asset, s) -> asset.next = s,
                            asset -> asset.next
                    )
                    .documentation("The next dialogue that should open after continuing.\n\nThis will eventually be replaced with multiline components.")
                    .add()
                    .append(new KeyedCodec<>("Typewriter Effect", Codec.BOOLEAN),
                            (obj, val) -> obj.typewriterEffect = val,
                            obj -> obj.typewriterEffect
                    )
                    .documentation("Should the dialogue be written over time like a typewriter?")
                    .add()
                    .append(
                            new KeyedCodec<>("DialogueMod Macro", MacroAsset.CODEC),
                            (obj, val) -> obj.macro = val,
                            obj -> obj.macro
                    )
                    .add()
                    .build();

    private static AssetStore<String, DialogueAsset, DefaultAssetMap<String, DialogueAsset>> ASSET_STORE;
    public AssetExtraInfo.Data extraData;
    public DialogueType type = DialogueType.DIALOGUE_1;
    public MacroAsset macro;
    public String id;
    public DialogueEntry[] entries;
    public String next;

    public boolean typewriterEffect = false;

    public DialogueAsset(String id, DialogueEntry[] entries) {
        this.id = id;
        this.entries = entries;
    }

    protected DialogueAsset() {
    }

    public static AssetStore<String, DialogueAsset, DefaultAssetMap<String, DialogueAsset>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(DialogueAsset.class);
        }
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, DialogueAsset> getAssetMap() {
        return DialogueAsset.getAssetStore().getAssetMap();
    }

    public boolean isTypewriterEffectEnabled() {
        return this.typewriterEffect;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public DialogueType getType() {
        return this.type;
    }

    public DialogueEntry[] getEntries() {
        return this.entries;
    }

    public String getNext() {
        return this.next;
    }

    public MacroAsset getMacro() {
        return this.macro;
    }

    @Nonnull
    public String toString() {
        return "DialogueAsset{id='" + this.id + "', entries='" + Arrays.toString(this.entries) + "'}";
    }

}
