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
import lombok.Setter;
import net.lordimass.dialogue.player.DialoguePageManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogueAnimationSystem extends DelayedEntitySystem<EntityStore> {
    public static final float FPS = 30.0F;
    public static final List<Animation<?>> ANIMATIONS = new ArrayList<>();

    public DialogueAnimationSystem() {
        super(1.0F / FPS);
    }

    @Override
    public void tick(
        float dt,
        int index,
        @NonNull ArchetypeChunk<EntityStore> archetypeChunk,
        @NonNull Store<EntityStore> store,
        @NonNull CommandBuffer<EntityStore> commandBuffer
    ) {
        ANIMATIONS.forEach(anim -> {
            anim.update(dt);
            anim.pageManager.getHyUIPage().getTemplateProcessor().setVariables(anim.anchorTemplateMapping);
            anim.pageManager.getHyUIPage().updatePage(false);
        });
    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    public static void addAnimation(
        String elementId,
        UIElementBuilder<?> elementType,
        SimpleAnchor endAnchor,
        float durationSeconds
    ) {
        
    }

    public static class Animation<E extends UIElementBuilder<E>> {
        public final String elementId;
        public final Class<E> elementType;
        private final AnchorTemplateKeys anchorKeys;
        public final SimpleAnchor startAnchor;
        public final SimpleAnchor endAnchor;
        public final float durationSeconds;
        @Getter protected float animProgressSeconds = 0.0F;
        @Getter Map<String, String> anchorTemplateMapping;
        DialoguePageManager pageManager;

        public Animation(
            String elementId,
            Class<E> elementType,
            SimpleAnchor startAnchor,
            SimpleAnchor endAnchor,
            float durationSeconds,
            AnchorTemplateKeys anchorKeys,
            DialoguePageManager pageManager
        ) {
            this.elementId = elementId;
            this.elementType = elementType;
            this.startAnchor = startAnchor;
            this.endAnchor = endAnchor;
            this.durationSeconds = durationSeconds;
            this.anchorKeys = anchorKeys;
            this.pageManager = pageManager;
        }

        public void update(float dt) {
            animProgressSeconds = Math.min(animProgressSeconds + dt, durationSeconds);
            anchorTemplateMapping = new HashMap<>();
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
        }

        private Integer lerp(Integer start, Integer end) {
            if (start == null || end == null) return null;
            return (int) (start + animProgressSeconds*((end - start)/ durationSeconds));
        }
    }

    public static class AnchorTemplateKeys {
        @Nullable @Setter String left;
        @Nullable @Setter String right;
        @Nullable @Setter String top;
        @Nullable @Setter String bottom;
        @Nullable @Setter String height;
        @Nullable @Setter String full;
        @Nullable @Setter String horizontal;
        @Nullable @Setter String vertical;
        @Nullable @Setter String width;
        @Nullable @Setter String minWidth;
        @Nullable @Setter String maxWidth;

        public AnchorTemplateKeys() {}
    }

    public static class SimpleAnchor {
        @Nullable @Setter Integer left;
        @Nullable @Setter Integer right;
        @Nullable @Setter Integer top;
        @Nullable @Setter Integer bottom;
        @Nullable @Setter Integer height;
        @Nullable @Setter Integer full;
        @Nullable @Setter Integer horizontal;
        @Nullable @Setter Integer vertical;
        @Nullable @Setter Integer width;
        @Nullable @Setter Integer minWidth;
        @Nullable @Setter Integer maxWidth;

        public SimpleAnchor() {}
    }

}
