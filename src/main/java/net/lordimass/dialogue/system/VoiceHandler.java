package net.lordimass.dialogue.system;

import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import net.lordimass.dialogue.codec.DialogueAsset;

public class VoiceHandler {
    DialogueAsset dialogue;
    PlayerRef playerRef;

    public VoiceHandler(DialogueAsset dialogue, PlayerRef playerRef) {
        this.dialogue = dialogue;
        this.playerRef = playerRef;
    }

    public void play(char c) {
        int soundIndex = SoundEvent.getAssetMap().getIndex(
            dialogue.getVoice() +
                "_Voice_" +
                Character.toUpperCase(c)
        );
        if (soundIndex == Integer.MIN_VALUE) {
            soundIndex = SoundEvent.getAssetMap()
                .getIndex("Voice_" + Character.toUpperCase(c));
        }


        SoundUtil.playSoundEvent2dToPlayer(playerRef, soundIndex, SoundCategory.SFX);
    }

    public static void play(String soundEventId, PlayerRef playerRef) {
        int id = SoundEvent.getAssetMap().getIndex(soundEventId);
        SoundUtil.playSoundEvent2dToPlayer(playerRef, id, SoundCategory.SFX);
    }
}
