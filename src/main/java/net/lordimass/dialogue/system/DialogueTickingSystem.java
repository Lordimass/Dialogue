package net.lordimass.dialogue.system;

import au.ellie.hyui.builders.HyUIPage;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import lombok.Setter;
import net.lordimass.dialogue.player.DialoguePageManager;
import net.lordimass.dialogue.util.TranslationUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the typewriter effect for any dialogues added to its <code>tickingPageManagers</code>
 * static list.
 */
public class DialogueTickingSystem extends DelayedEntitySystem<EntityStore> {
    private static final float CHARS_PER_SECOND = 16.0F;
    public static ArrayList<DialoguePageManager> tickingPageManagers = new ArrayList<>();

    public DialogueTickingSystem() {
        super(1.0F / CHARS_PER_SECOND);
    }

    @Override
    public void tick(
        float dt,
        int index,
        @NonNull ArchetypeChunk<EntityStore> archetypeChunk,
        @NonNull Store<EntityStore> store,
        @NonNull CommandBuffer<EntityStore> commandBuffer
    ) {
        ArrayList<DialoguePageManager> newTickingPageManagers = new ArrayList<>();
        tickingPageManagers.forEach((pageManager) -> {
            TypewriterEffectInfo info = pageManager.getTypewriterEffectInfo();
            int charCount = calculateCharCount(info, dt);
            TranslationUtils.SubstringAndLastChar substringAndLastChar = TranslationUtils.substringFromTokens(info.getDialogueTokens(), charCount);
            HyUIPage hyUIPage = pageManager.getHyUIPage();
            hyUIPage.getTemplateProcessor()
                .setVariable("content", substringAndLastChar.string());
            hyUIPage.updatePage(false);

            info.getVoiceHandler().play(substringAndLastChar.lastChar());

            if (!(substringAndLastChar.string().length() == info.getCompleteDialogueString().length())) {
                newTickingPageManagers.add(pageManager);
            }
        });
        tickingPageManagers = (ArrayList<DialoguePageManager>) newTickingPageManagers.clone();
    }

    private static int calculateCharCount(TypewriterEffectInfo info, float dt) {
        float totalDt = info.getTypewriterDt() + dt;
        info.setTypewriterDt(totalDt);
        return (int) (totalDt * CHARS_PER_SECOND);
    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    public static class TypewriterEffectInfo {
        @Getter
        @Nonnull
        private final List<String> dialogueTokens;
        @Getter
        @Nonnull
        private final String completeDialogueString;
        @Getter
        @Setter
        private float typewriterDt;
        @Getter
        private final VoiceHandler voiceHandler;

        public TypewriterEffectInfo(@Nonnull String completeDialogueString, @Nonnull DialoguePageManager pageManager) {
            this.dialogueTokens = TranslationUtils.tokenize(completeDialogueString);
            this.completeDialogueString = completeDialogueString;
            this.voiceHandler = new VoiceHandler(pageManager.getDialogue(), pageManager.getPlayerRef());
        }

        public void reset() {
            this.dialogueTokens.clear();
            this.typewriterDt = 0;
        }
    }
}
