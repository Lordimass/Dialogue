package net.queensfall.player;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.UUID;

public class DialoguePlayerConfig {

    public static final BuilderCodec<DialoguePlayerConfig> CODEC =
            BuilderCodec
                    .builder(
                            DialoguePlayerConfig.class,
                            DialoguePlayerConfig::new
                    )
                    .append(new KeyedCodec<>("UUID", Codec.STRING),
                            (config, val) -> config.setUuid(UUID.fromString(val)),
                            config -> config.playerUuid.toString())
                    .add()
                    .build();

    public UUID playerUuid;

    public void setUuid(UUID uuid) {
        this.playerUuid = uuid;
    }
}
