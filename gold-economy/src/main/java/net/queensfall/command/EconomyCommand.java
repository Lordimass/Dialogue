package net.queensfall.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.queensfall.component.MoneyComponent;
import org.jspecify.annotations.NonNull;

import java.awt.*;

public class EconomyCommand extends AbstractPlayerCommand {
    public EconomyCommand() {
        super("economy", "Container command for GoldEconomy commands");

        addSubCommand(new GetCommand());
        addSubCommand(new AddCommand());
        addSubCommand(new SubtractCommand());
    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        commandContext.sender().sendMessage(Message.raw("Missing required arguments.").color(Color.RED));
    }

    public static MoneyComponent getTargetPlayerMoney(OptionalArg<PlayerRef> argPlayerRef, PlayerRef otherPlayerRef, CommandContext commandContext) {
        PlayerRef targetPlayerRef = argPlayerRef.get(commandContext);
        if (targetPlayerRef == null) {
            targetPlayerRef = otherPlayerRef;
        }
        Ref<EntityStore> targetPlayerEntityRef = targetPlayerRef.getReference();
        assert targetPlayerEntityRef != null;
        Store<EntityStore> targetPlayerStore = targetPlayerEntityRef.getStore();
        return targetPlayerStore.ensureAndGetComponent(
            targetPlayerEntityRef,
            MoneyComponent.getComponentType()
        );
    }
}
