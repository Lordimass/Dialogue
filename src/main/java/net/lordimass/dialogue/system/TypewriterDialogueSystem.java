package net.lordimass.dialogue.system;

import au.ellie.hyui.builders.HyUIPage;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.lordimass.dialogue.player.DialoguePageManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

import static net.lordimass.dialogue.util.TranslationUtils.translateWithHYUIML;

/**
 * Handles the typewriter effect for the player's currently open dialogue.
 */
public class TypewriterDialogueSystem extends DelayedEntitySystem<EntityStore> {
    private static final float CHARS_PER_SECOND = 15.0F;
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
        tickingPageManagers.forEach((pageManager) -> {
            float totalDt = pageManager.getTypewriterDt() + dt;
            pageManager.setTypewriterDt(totalDt);
            int charCount = (int) (totalDt * CHARS_PER_SECOND);
            String inProgressString = pageManager
                .getCompleteString()
                .substring(0, Math.min(charCount, pageManager.getCompleteString().length()));
            HyUIPage hyUIPage = pageManager.getHyUIPage();
            hyUIPage.getTemplateProcessor()
                .setVariable("content", inProgressString);

            hyUIPage.updatePage(false);
        });
    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }
}
