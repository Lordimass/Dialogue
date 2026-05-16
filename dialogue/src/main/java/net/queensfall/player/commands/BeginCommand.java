package net.queensfall.player.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.queensfall.player.ui.page.DialoguePage;

import javax.annotation.Nonnull;

public class BeginCommand extends AbstractPlayerCommand {

    private final RequiredArg<PlayerRef> playerArg;
    private final RequiredArg<String> dialogArg;

    public BeginCommand() {
        super("begin", "Command for developer use only!");
        playerArg = withRequiredArg("player", "Username of the player who should open dialogue.", ArgTypes.PLAYER_REF);
        dialogArg = withRequiredArg("dialogue", "The dialogue to open.", ArgTypes.STRING);
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String label = commandContext.get(dialogArg);

        PlayerRef playerRef1 = playerArg.get(commandContext);
        Ref<EntityStore> ref1 = playerRef1.getReference();
        assert ref1 != null;
        Store<EntityStore> store1 = ref1.getStore();

        Player playerComponent = store.getComponent(ref1, Player.getComponentType());

        if (playerComponent == null) {
            return;
        }

        playerComponent.getPageManager().openCustomPage(ref1, store1,
                new DialoguePage(ref1, store1, playerRef1, label));
    }
}
