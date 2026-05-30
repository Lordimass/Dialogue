package net.lordimass.dialogue.codec;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.logger.HytaleLogger;
import lombok.Getter;
import lombok.ToString;
import net.lordimass.dialogue.DialogueMod;
import org.bson.BsonValue;
import org.jspecify.annotations.NonNull;
import javax.annotation.Nullable;

@ToString
public class DialogueAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, DialogueAsset>> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static final EnumCodec<DialogueType> DIALOG_TYPE_ENUM_CODEC = new EnumCodec<>(DialogueType.class);

    public static final BuilderCodec<DialogueAsset> CODEC =
        BuilderCodec.builder(DialogueAsset.class, DialogueAsset::new)
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
            .documentation("Content of the dialogue.")
            .add()
            .append(
                new KeyedCodec<>("Title", Codec.STRING),
                (asset, s) -> asset.title = s,
                asset -> asset.title
            )
            .documentation("Title of the dialogue. Usually the name of the person speaking.")
            .add()
            .append(
                new KeyedCodec<>("NextId", Codec.STRING),
                (asset, s) -> asset.nextId = s,
                asset -> asset.nextId
            )
            .documentation("The asset ID of the next dialogue that should open after continuing.")
            .add()
            .append(
                new KeyedCodec<>("Next", new LazyCodec()),
                (asset, s) -> asset.next = s,
                asset -> asset.next
            )
            .documentation("The next dialogue to open after this one. Use `NextId` instead to " +
                "reference a separate dialogue asset instead of inlining it here.")
            .add()
            .append(new KeyedCodec<>("TypewriterEffect", Codec.BOOLEAN),
                (obj, val) -> obj.typewriterEffect = val,
                obj -> obj.typewriterEffect
            )
            .documentation("Should the dialogue be written over time like a typewriter?")
            .add()
            .append(new KeyedCodec<>("BlockIdentifier", Codec.STRING),
                (obj, val) -> obj.blockId = val,
                obj -> obj.blockId
            )
            .documentation("A unique identifier for this specific dialogue block. If left " +
                "undefined, it will default to the ID of the asset (i.e. the JSON file name).")
            .add()
            .append(new KeyedCodec<>("Voice", Codec.STRING),
                (obj, val) -> obj.voice = val,
                obj -> obj.voice
            )
            .documentation("The ID of the voice to use for this Dialogue. If undefined, it will " +
                "choose a voice based on the title of the dialogue, so dialogues with the same " +
                "title will always have the same voice. Set to the empty string to disable voice." +
                "This only works if TypewriterEffect has not been disabled.")
            .add()
            .build();

    public static final AssetBuilderCodec<String, DialogueAsset> ASSET_BUILDER_CODEC =
        AssetBuilderCodec.wrap(
            DialogueAsset.CODEC,
            Codec.STRING,
            (asset, s) -> asset.id = s,
            asset -> asset.id,
            (asset, data) -> asset.extraData = data,
            asset -> asset.extraData
        );

    private static AssetStore<String, DialogueAsset, DefaultAssetMap<String, DialogueAsset>> ASSET_STORE;
    private AssetExtraInfo.Data extraData;
    @Getter
    private DialogueType type = DialogueType.Dialogue;
    @Getter
    public String id;
    @Getter
    public DialogueEntry[] entries;
    private String nextId;
    private DialogueAsset next;
    @Getter
    @Nullable
    private String title;
    private String blockId;
    @Getter
    private boolean typewriterEffect = true;
    private String voice;

    protected DialogueAsset() {
    }

    public String getBlockId() {
        return blockId == null ? id : blockId;
    }

    public static AssetStore<String, DialogueAsset, DefaultAssetMap<String, DialogueAsset>> getAssetStore() {
        if (ASSET_STORE == null) ASSET_STORE = AssetRegistry.getAssetStore(DialogueAsset.class);
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, DialogueAsset> getAssetMap() {
        return DialogueAsset.getAssetStore().getAssetMap();
    }

    @Nullable
    public static DialogueAsset getAsset(String key) {
        DialogueAsset asset = AssetRegistry
            .getAssetStore(DialogueAsset.class)
            .getAssetMap()
            .getAsset(key);
        if (asset == null) LOGGER.atSevere().log("DialogueAsset '"+key+"' could not be found");
        return asset;
    }

    @Nullable
    public DialogueAsset getNext() {
        if (this.next != null) {
            return this.next;
        } else if (this.nextId != null) {
            return getAsset(this.nextId);
        }
        return null;
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
        if (this.title == null) return DialogueMod.BUILTIN_VOICE_IDS[0];
        int value = 0;
        for (char c : this.title.toCharArray()) {
            value += Character.getNumericValue(c);
        }
        voice = DialogueMod.BUILTIN_VOICE_IDS[value % DialogueMod.BUILTIN_VOICE_IDS.length];
        return voice;
    }

    /** Lazy DialogueAsset.CODEC work around so that the codec can be self-referential. */
    public static class LazyCodec implements Codec<DialogueAsset> {
        public @NonNull Schema toSchema(@NonNull SchemaContext schemaContext) {return DialogueAsset.CODEC.toSchema(schemaContext);}
        public @Nullable DialogueAsset decode(BsonValue bsonValue, ExtraInfo extraInfo) {return DialogueAsset.CODEC.decode(bsonValue, extraInfo);}
        public BsonValue encode(DialogueAsset dialogueAsset, ExtraInfo extraInfo) {return DialogueAsset.CODEC.encode(dialogueAsset, extraInfo);}
    }

}
