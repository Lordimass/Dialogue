package net.queensfall.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.queensfall.component.MoneyComponent;
import org.jspecify.annotations.NonNull;

public class GetCommand extends AbstractPlayerCommand {
    private final OptionalArg<PlayerRef> playerArg;

    public GetCommand() {
        super("get", "Shows the amount of money the given player has.");

        playerArg = withOptionalArg("player", "Name of the player to fetch the amount of money for.", ArgTypes.PLAYER_REF);
    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        MoneyComponent moneyComponent = EconomyCommand
            .getTargetPlayerMoney(playerArg, playerRef, commandContext);
        commandContext.sender().sendMessage(Message.raw(moneyComponent.toString()));
    }
}
