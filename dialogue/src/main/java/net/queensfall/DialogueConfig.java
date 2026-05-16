package net.queensfall;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.Getter;

@Getter
public class DialogueConfig {
    public static final BuilderCodec<DialogueConfig> CODEC = BuilderCodec.builder(DialogueConfig.class, DialogueConfig::new)
            .append(new KeyedCodec<>("DebugMode", Codec.BOOLEAN),
                    (config, val) -> config.debugMode = val,
                    config -> config.debugMode)
            .add()
            .build();
    private boolean debugMode = false;

}
