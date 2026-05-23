package net.queensfall;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import net.queensfall.command.EconomyCommand;
import net.queensfall.component.MoneyComponent;
import org.jspecify.annotations.NonNull;

public class GoldEconomy extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static GoldEconomy INSTANCE;

    @Getter
    private static ComponentType<EntityStore, MoneyComponent> moneyComponentType;

    public GoldEconomy(@NonNull JavaPluginInit init) {
        super(init);
        INSTANCE = this;
    }

    @Override
    public void setup() {
        moneyComponentType = this.getEntityStoreRegistry().registerComponent(MoneyComponent.class, MoneyComponent::new);

        this.getCommandRegistry().registerCommand(new EconomyCommand());
    }

    public static GoldEconomy get() {
        return INSTANCE;
    }
}
