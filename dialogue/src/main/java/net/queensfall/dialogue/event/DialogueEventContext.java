package net.queensfall.dialogue.event;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.queensfall.DialogueMod;
import net.queensfall.util.ParameterContext;

public record DialogueEventContext(
        String dialogId,
        PlayerRef player,
        Ref<EntityStore> entRef,
        Store<EntityStore> entStore,
        ParameterContext params,
        DialogueMod dialogue
) {}