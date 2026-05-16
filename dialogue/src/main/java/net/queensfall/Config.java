package net.queensfall;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.Getter;

@Getter
public class Config {
    public static final BuilderCodec<Config> CODEC = BuilderCodec.builder(Config.class, Config::new)
            .append(new KeyedCodec<>("DebugMode", Codec.BOOLEAN),
                    (config, val) -> config.debugMode = val,
                    config -> config.debugMode)
            .add()
            .build();
    private boolean debugMode = false;

}
