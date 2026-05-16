package net.queensfall.asset.dialog;

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
import net.queensfall.asset.macro.HyspeechMacroAsset;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class HyspeechDialogAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, HyspeechDialogAsset>> {

    public static final EnumCodec<HyspeechDialogType> DIALOG_TYPE_ENUM_CODEC = new EnumCodec<>(HyspeechDialogType.class);

    public static final AssetBuilderCodec<String, HyspeechDialogAsset> CODEC =
            AssetBuilderCodec
                    .builder(
                            HyspeechDialogAsset.class,
                            HyspeechDialogAsset::new,
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
                            new KeyedCodec<>("Entries", new ArrayCodec<>(HyspeechDialogEntry.CODEC, HyspeechDialogEntry[]::new)),
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
                            new KeyedCodec<>("DialogueMod Macro", HyspeechMacroAsset.CODEC),
                            (obj, val) -> obj.macro = val,
                            obj -> obj.macro
                    )
                    .add()
                    .build();

    private static AssetStore<String, HyspeechDialogAsset, DefaultAssetMap<String, HyspeechDialogAsset>> ASSET_STORE;
    public AssetExtraInfo.Data extraData;
    public HyspeechDialogType type = HyspeechDialogType.DIALOG_1;
    public HyspeechMacroAsset macro;
    public String id;
    public HyspeechDialogEntry[] entries;
    public String next;

    public boolean typewriterEffect = false;

    public HyspeechDialogAsset(String id, HyspeechDialogEntry[] entries) {
        this.id = id;
        this.entries = entries;
    }

    protected HyspeechDialogAsset() {
    }

    public static AssetStore<String, HyspeechDialogAsset, DefaultAssetMap<String, HyspeechDialogAsset>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(HyspeechDialogAsset.class);
        }
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, HyspeechDialogAsset> getAssetMap() {
        return HyspeechDialogAsset.getAssetStore().getAssetMap();
    }

    public boolean isTypewriterEffectEnabled() {
        return this.typewriterEffect;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public HyspeechDialogType getType() {
        return this.type;
    }

    public HyspeechDialogEntry[] getEntries() {
        return this.entries;
    }

    public String getNext() {
        return this.next;
    }

    public HyspeechMacroAsset getMacro() {
        return this.macro;
    }

    @Nonnull
    public String toString() {
        return "HyspeechDialogAsset{id='" + this.id + "', entries='" + Arrays.toString(this.entries) + "'}";
    }

}
