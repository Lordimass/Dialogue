package net.lordimass.dialogue.system;

import au.ellie.hyui.builders.HyUIPage;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import lombok.Setter;
import net.lordimass.dialogue.player.DialoguePageManager;
import net.lordimass.dialogue.util.TokenString;
import net.lordimass.dialogue.util.TranslationUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.lordimass.dialogue.util.TokenString.SOUND_TAG_REGEX;

/**
 * Handles the typewriter effect for any dialogues added to its <code>tickingPageManagers</code>
 * static list.
 */
public class DialogueTickingSystem extends DelayedEntitySystem<EntityStore> {
    private static final float CHARS_PER_SECOND = 16.0F;
    private static final Set<Character> DELAY_CHARACTERS = Stream.of('.', ',', ';', '!', '?')
        .collect(Collectors.toUnmodifiableSet());
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
            TokenString tokenString = info.getTokenString();
            String nextToken = tokenString.next();
            if (nextToken == null) return;

            updateGui(pageManager, tokenString.getInProgressString());
            // charAt 0 is usually the only character in the token at this stage.
            String soundEvent = nextToken.matches(SOUND_TAG_REGEX) ? nextToken.replaceAll(SOUND_TAG_REGEX, "$1") : null;
            if (DELAY_CHARACTERS.contains(nextToken.charAt(0)) || soundEvent != null) info.delayNext = true;

            playSounds(pageManager, nextToken, soundEvent);

            if (!tokenString.isComplete()) {
                newTickingPageManagers.add(pageManager);
            }
        });
        tickingPageManagers = new ArrayList<>(newTickingPageManagers);
    }

    private static void updateGui(DialoguePageManager pageManager, String content) {
        HyUIPage hyUIPage = pageManager.getHyUIPage();
        hyUIPage.getTemplateProcessor()
            .setVariable("content", content);
        hyUIPage.updatePage(false);
    }

    private static void playSounds(DialoguePageManager pageManager, String token, @Nullable String soundEvent) {
        if (soundEvent != null) {
            VoiceHandler.play(soundEvent, pageManager.getPlayerRef());
        } else {
            pageManager.getTypewriterEffectInfo().getVoiceHandler().play(token.charAt(0));
        }
    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    public static class TypewriterEffectInfo {
        @Getter
        TokenString tokenString;
        @Getter
        private final VoiceHandler voiceHandler;
        protected boolean delayNext;

        public TypewriterEffectInfo(@Nonnull String completeDialogueString, @Nonnull DialoguePageManager pageManager) {
            this.tokenString = new TokenString(completeDialogueString);
            this.voiceHandler = new VoiceHandler(pageManager.getDialogue(), pageManager.getPlayerRef());
        }
    }
}
