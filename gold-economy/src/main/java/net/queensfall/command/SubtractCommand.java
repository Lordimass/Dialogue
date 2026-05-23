package net.queensfall.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.queensfall.component.MoneyComponent;
import org.jspecify.annotations.NonNull;

public class SubtractCommand extends AbstractPlayerCommand {
    private final OptionalArg<PlayerRef> playerArg;
    private final RequiredArg<Integer> goldArg;
    private final RequiredArg<Integer> silverArg;
    private final RequiredArg<Integer> copperArg;

    public SubtractCommand() {
        super("subtract", "Subtracts money from the given player.");

        playerArg = withOptionalArg("player", "Name of the player to subtract money from.", ArgTypes.PLAYER_REF);
        goldArg = withRequiredArg("gold", "The amount of gold to subtract.", ArgTypes.INTEGER);
        silverArg = withRequiredArg("silver", "The amount of silver to subtract.", ArgTypes.INTEGER);
        copperArg = withRequiredArg("copper", "The amount of copper to subtract.", ArgTypes.INTEGER);
    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        MoneyComponent moneyComponent = EconomyCommand
            .getTargetPlayerMoney(playerArg, playerRef, commandContext);
        moneyComponent.subtract(
            goldArg.get(commandContext),
            silverArg.get(commandContext),
            copperArg.get(commandContext)
        );
        commandContext.sender().sendMessage(Message.raw("Player balance is now: " + moneyComponent));
    }
}