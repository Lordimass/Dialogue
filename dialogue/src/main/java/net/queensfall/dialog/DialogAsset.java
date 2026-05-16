package net.queensfall.dialog;

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

public class DialogAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, DialogAsset>> {

    public static final EnumCodec<DialogType> DIALOG_TYPE_ENUM_CODEC = new EnumCodec<>(DialogType.class);

    public static final AssetBuilderCodec<String, DialogAsset> CODEC =
            AssetBuilderCodec
                    .builder(
                            DialogAsset.class,
                            DialogAsset::new,
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
                            new KeyedCodec<>("Entries", new ArrayCodec<>(DialogEntry.CODEC, DialogEntry[]::new)),
                            (asset, entries) -> {
                                asset.entries = entries;
                            },
                            asset -> asset.entries
                    )
                    .documentation("Content of the dialog.\n\nThis will eventually be replaced with multiline components.")
                    .add()
                    .append(
                            new KeyedCodec<>("Next", Codec.STRING),
                            (asset, s) -> asset.next = s,
                            asset -> asset.next
                    )
                    .documentation("The next dialog that should open after continuing.\n\nThis will eventually be replaced with multiline components.")
                    .add()
                    .append(new KeyedCodec<>("Typewriter Effect", Codec.BOOLEAN),
                            (obj, val) -> obj.typewriterEffect = val,
                            obj -> obj.typewriterEffect
                    )
                    .documentation("Should the dialog be written over time like a typewriter?")
                    .add()
                    .append(
                            new KeyedCodec<>("DialogueMod Macro", MacroAsset.CODEC),
                            (obj, val) -> obj.macro = val,
                            obj -> obj.macro
                    )
                    .add()
                    .build();

    private static AssetStore<String, DialogAsset, DefaultAssetMap<String, DialogAsset>> ASSET_STORE;
    public AssetExtraInfo.Data extraData;
    public DialogType type = DialogType.DIALOG_1;
    public MacroAsset macro;
    public String id;
    public DialogEntry[] entries;
    public String next;

    public boolean typewriterEffect = false;

    public DialogAsset(String id, DialogEntry[] entries) {
        this.id = id;
        this.entries = entries;
    }

    protected DialogAsset() {
    }

    public static AssetStore<String, DialogAsset, DefaultAssetMap<String, DialogAsset>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(DialogAsset.class);
        }
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, DialogAsset> getAssetMap() {
        return DialogAsset.getAssetStore().getAssetMap();
    }

    public boolean isTypewriterEffectEnabled() {
        return this.typewriterEffect;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public DialogType getType() {
        return this.type;
    }

    public DialogEntry[] getEntries() {
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
        return "DialogAsset{id='" + this.id + "', entries='" + Arrays.toString(this.entries) + "'}";
    }

}
