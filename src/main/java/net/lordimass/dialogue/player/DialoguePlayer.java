package net.lordimass.dialogue.player;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.Config;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

public class DialoguePlayer {
    private PlayerRef player;
    @Setter
    @Getter
    private Config<DialoguePlayerConfig> config;

    public DialoguePlayer(PlayerRef player) {
        this.player = player;

        config = new Config<>(
                new File("config/dialogue/player_data/").toPath(),
                player.getUuid().toString(),
                DialoguePlayerConfig.CODEC
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

    protected DialoguePlayer() {
    }

    public PlayerRef getPlayerRef() {
        return this.player;
    }

}
