package net.lordimass.dialogue.component;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import lombok.Setter;
import net.lordimass.dialogue.DialogueMod;
import net.lordimass.dialogue.DialogueRuntime;
import net.lordimass.dialogue.codec.DialogueAsset;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;

public class NPCDialogueComponent implements Component<EntityStore> {
    @Nonnull
    public static final BuilderCodec<NPCDialogueComponent> CODEC =
        BuilderCodec.builder(NPCDialogueComponent.class, NPCDialogueComponent::new)
            .append(
                new KeyedCodec<>("CurrentDialogue", DialogueAsset.CODEC),
                (comp, dialogue) -> comp.currentDialogue = dialogue,
                comp -> comp.currentDialogue
            )
            .documentation("The currently open dialogue")
            .add()
            .append(
                new KeyedCodec<>("Player", Player.CODEC),
                (comp, player) -> comp.player = player,
                comp -> comp.player
            )
            .documentation("The player currently engaged in interaction with this NPC")
            .add()
            .build();

    @Setter
    @Getter
    private DialogueAsset currentDialogue;
    @Getter
    @Setter
    private Player player;

    public NPCDialogueComponent() {}

    public NPCDialogueComponent(DialogueAsset currentDialogue, Player player) {
        this.currentDialogue = currentDialogue;
        this.player = player;
    }

    public static void update(@Nullable Ref<EntityStore> npcRef, DialogueAsset dialogue, PlayerRef playerRef) {
        if (npcRef == null) return;
        NPCDialogueComponent comp = npcRef.getStore().ensureAndGetComponent(
            npcRef, NPCDialogueComponent.getComponentType()
        );
        comp.setCurrentDialogue(dialogue);
        Ref<EntityStore> playerEntityRef = Objects.requireNonNull(playerRef.getReference());
        comp.setPlayer(playerEntityRef.getStore().getComponent(playerEntityRef, Player.getComponentType()));
    }

    public static void clear(@Nullable Ref<EntityStore> npcRef) {
        if (npcRef == null) return;
        try {
            npcRef.getStore().removeComponentIfExists(npcRef, NPCDialogueComponent.getComponentType());
        } catch (IllegalStateException e) {
            // Genuinely not got a clue why it throws this error with "Invalid entity reference"
            // but it doesn't seem to affect anything...
        }

    }

    public static NPCDialogueComponent get(@Nullable Ref<EntityStore> ref, ComponentAccessor<EntityStore> accessor) {
        if (ref == null) return null;
        return accessor.getComponent(ref, getComponentType());
    }

    @Override
    public @Nullable Component<EntityStore> clone() {
        return new NPCDialogueComponent(currentDialogue, player);
    }

    public static ComponentType<EntityStore, NPCDialogueComponent> getComponentType() {
        return DialogueMod.getDialogueComponentType();
    }
}
