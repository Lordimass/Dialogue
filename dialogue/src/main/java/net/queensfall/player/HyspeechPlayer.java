package net.queensfall.player;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.Config;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

public class HyspeechPlayer {
    private PlayerRef player;
    @Setter
    @Getter
    private Config<HyspeechPlayerConfig> config;

    public HyspeechPlayer(PlayerRef player) {
        this.player = player;

        config = new Config<>(
                new File("config/hyspeech/player_data/").toPath(),
                player.getUuid().toString(),
                HyspeechPlayerConfig.CODEC
        );

        getConfig().load().thenAccept((config) -> {
            if (config.playerUuid == null)
                config.setUuid(getPlayerRef().getUuid());
        }).exceptionally((throwable -> {
            throwable.printStackTrace();
            return null;
        }));

        config.save();
    }

    protected HyspeechPlayer() {
    }

    public PlayerRef getPlayerRef() {
        return this.player;
    }

}
