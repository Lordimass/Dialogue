package net.lordimass.dialogue.system;

import au.ellie.hyui.builders.HyUIPage;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.lordimass.dialogue.player.DialoguePageManager;
import net.lordimass.dialogue.util.TranslationUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;

/**
 * Handles the typewriter effect for any dialogues added to its <code>tickingPageManagers</code>
 * static list.
 */
public class TypewriterDialogueSystem extends DelayedEntitySystem<EntityStore> {
    private static final float CHARS_PER_SECOND = 25.0F;
    public static ArrayList<DialoguePageManager> tickingPageManagers = new ArrayList<>();

    public TypewriterDialogueSystem() {
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
            int charCount = calculateCharCount(pageManager, dt);
            String inProgressString = TranslationUtils.tokenedSubstring(pageManager.getDialogueTokens(), charCount);
            HyUIPage hyUIPage = pageManager.getHyUIPage();
            hyUIPage.getTemplateProcessor()
                .setVariable("content", inProgressString);
            hyUIPage.updatePage(false);

            if (!(inProgressString.length() == pageManager.getCompleteDialogueString().length())) {
                newTickingPageManagers.add(pageManager);
            }
        });
        tickingPageManagers = (ArrayList<DialoguePageManager>) newTickingPageManagers.clone();
    }

    private static int calculateCharCount(DialoguePageManager pageManager, float dt) {
        float totalDt = pageManager.getTypewriterDt() + dt;
        pageManager.setTypewriterDt(totalDt);
        return (int) (totalDt * CHARS_PER_SECOND);
    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }
}
