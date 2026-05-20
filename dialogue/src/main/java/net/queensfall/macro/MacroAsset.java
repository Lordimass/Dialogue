package net.queensfall.macro;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import lombok.Getter;
import net.queensfall.codec.DialogueType;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class MacroAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, MacroAsset>> {

    public static final AssetBuilderCodec<String, MacroAsset> CODEC =
            AssetBuilderCodec
                    .builder(
                            MacroAsset.class,
                            MacroAsset::new,
                            Codec.STRING,
                            (asset, s) -> asset.id = s,
                            asset -> asset.id,
                            (asset, data) -> asset.extraData = data,
                            asset -> asset.extraData
                    )
                    .append(
                            new KeyedCodec<>("Commands", new ArrayCodec<>(ArrayCodec.STRING, String[]::new)),
                            (asset, commands) -> {
                                asset.commands = commands;
                            },
                            asset -> asset.commands
                    )
                    .documentation("Commands to execute in order. Use {username} to target the player who executed the dialogue.")
                    .add()
                    .build();
    private static AssetStore<String, MacroAsset, DefaultAssetMap<String, MacroAsset>> ASSET_STORE;
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache(new AssetKeyValidator(MacroAsset::getAssetStore));
    public AssetExtraInfo.Data extraData;
    @Getter
    public DialogueType type;
    public String id;
    @Getter
    public String[] commands;
    @Getter
    public String next;

    public MacroAsset(String id, String[] commands) {
        this.id = id;
        this.commands = commands;
    }

    protected MacroAsset() {
    }

    public static AssetStore<String, MacroAsset, DefaultAssetMap<String, MacroAsset>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(MacroAsset.class);
        }
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, MacroAsset> getAssetMap() {
        return MacroAsset.getAssetStore().getAssetMap();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Nonnull
    public String toString() {
        return "DialogueMacro{id='" + this.id + "', commands='" + Arrays.toString(this.commands) + "'}";
    }

}
