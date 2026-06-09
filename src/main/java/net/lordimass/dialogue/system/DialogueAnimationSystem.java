package net.lordimass.dialogue.system;

import au.ellie.hyui.builders.UIElementBuilder;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import net.lordimass.dialogue.ui.DialoguePageManager;
import net.lordimass.dialogue.ui.SimpleAnchor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class DialogueAnimationSystem extends DelayedEntitySystem<EntityStore> {
    private static final float TPS = 30.0F;
    private static List<Animation<?>> animations = new ArrayList<>();

    public DialogueAnimationSystem() {
        super(1.0F / TPS);
    }

    @Override
    public void tick(
        float dt,
        int index,
        @NonNull ArchetypeChunk<EntityStore> archetypeChunk,
        @NonNull Store<EntityStore> store,
        @NonNull CommandBuffer<EntityStore> commandBuffer
    ) {
        List<Animation<?>> newAnimations = new ArrayList<>();
        List<Animation<?>> completedAnimations = new ArrayList<>();
        animations.forEach(anim -> {
            Map<String, String> anchorTemplateMapping = anim.update(dt);
            anim.pageManager.getHyUIPage().getTemplateProcessor().setVariables(anchorTemplateMapping);
            anim.pageManager.getHyUIPage().updatePage(false);
            if (anim.animProgressSeconds != anim.durationSeconds) newAnimations.add(anim);
            else completedAnimations.add(anim);
        });
        animations = new ArrayList<>(newAnimations);
        completedAnimations.forEach(anim -> {
            if (anim.onComplete != null) anim.onComplete.run();
        });
    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    /**
     * Begin animating the movement of a UI element to a new anchor. If an animation is already
     * in progress for the given element, it will be overridden in favour of this one.
     */
    public static <E extends UIElementBuilder<E>> void addAnimation(
        Animation<E> animation
    ) {
        animations.removeIf(anim -> anim.elementId.equals(animation.elementId));
        animations.add(animation);
    }

    public static class Animation<E extends UIElementBuilder<E>> {
        public final String elementId;
        public final Class<E> elementType;
        private final SimpleAnchor.AnchorTemplateKeys anchorKeys;
        public final SimpleAnchor startAnchor;
        public final SimpleAnchor endAnchor;
        public final float durationSeconds;
        @Getter protected float animProgressSeconds = 0.0F;
        DialoguePageManager pageManager;
        @Nullable Runnable onComplete;

        public Animation(
            String elementId,
            Class<E> elementType,
            SimpleAnchor startAnchor,
            SimpleAnchor endAnchor,
            float durationSeconds,
            SimpleAnchor.AnchorTemplateKeys anchorKeys,
            DialoguePageManager pageManager,
            @Nullable Runnable onComplete
        ) {
            this.elementId = elementId;
            this.elementType = elementType;
            this.startAnchor = startAnchor;
            this.endAnchor = endAnchor;
            this.durationSeconds = durationSeconds;
            this.anchorKeys = anchorKeys;
            this.pageManager = pageManager;
            this.onComplete = onComplete;
        }

        Map<String, String> update(float dt) {
            animProgressSeconds = Math.min(animProgressSeconds + dt, durationSeconds);
            Map<String, String> anchorTemplateMapping = new HashMap<>();
            if (anchorKeys.left != null) anchorTemplateMapping.put(anchorKeys.left, ""+lerp(startAnchor.left, endAnchor.left));
            if (anchorKeys.right != null) anchorTemplateMapping.put(anchorKeys.right, ""+lerp(startAnchor.right, endAnchor.right));
            if (anchorKeys.top != null) anchorTemplateMapping.put(anchorKeys.top, ""+lerp(startAnchor.top, endAnchor.top));
            if (anchorKeys.bottom != null) anchorTemplateMapping.put(anchorKeys.bottom, ""+lerp(startAnchor.bottom, endAnchor.bottom));
            if (anchorKeys.height != null) anchorTemplateMapping.put(anchorKeys.height, ""+lerp(startAnchor.height, endAnchor.height));
            if (anchorKeys.full != null) anchorTemplateMapping.put(anchorKeys.full, ""+lerp(startAnchor.full, endAnchor.full));
            if (anchorKeys.horizontal != null) anchorTemplateMapping.put(anchorKeys.horizontal, ""+lerp(startAnchor.horizontal, endAnchor.horizontal));
            if (anchorKeys.vertical != null) anchorTemplateMapping.put(anchorKeys.vertical, ""+lerp(startAnchor.vertical, endAnchor.vertical));
            if (anchorKeys.width != null) anchorTemplateMapping.put(anchorKeys.width, ""+lerp(startAnchor.width, endAnchor.width));
            if (anchorKeys.minWidth != null) anchorTemplateMapping.put(anchorKeys.minWidth, ""+lerp(startAnchor.minWidth, endAnchor.minWidth));
            if (anchorKeys.maxWidth != null) anchorTemplateMapping.put(anchorKeys.maxWidth, ""+lerp(startAnchor.maxWidth, endAnchor.maxWidth));
            return anchorTemplateMapping;
        }

        private Integer lerp(Integer start, Integer end) {
            if (start == null || end == null) return null;
            return (int) (start + animProgressSeconds*((end - start)/ durationSeconds));
        }
    }

}
