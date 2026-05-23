package net.queensfall.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import net.queensfall.GoldEconomy;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;

public class MoneyComponent implements Component<EntityStore> {
    @Nonnull
    public static final BuilderCodec<MoneyComponent> CODEC =
        BuilderCodec.builder(MoneyComponent.class, MoneyComponent::new)
            .append(
                new KeyedCodec<>("TotalCurrency", Codec.INTEGER),
                (comp, gold) -> comp.total = gold,
                comp -> comp.total
            )
            .documentation("The amount of currency this entity has on them")
            .add()
            .build();

    private static final int SILVER_INCREMENT = 100;
    private static final int GOLD_INCREMENT = (int) Math.pow(SILVER_INCREMENT, 2);

    private int total = 0;

    public MoneyComponent() {}

    private MoneyComponent(int total) {
        this.total = total;
    }

    public int getGold() {
        return total / GOLD_INCREMENT;
    }

    public int getSilver() {
        return total % GOLD_INCREMENT / SILVER_INCREMENT;
    }

    public int getCopper() {
        return total % SILVER_INCREMENT;
    }

    public void set(int gold, int silver, int copper) {
        this.total = gold*GOLD_INCREMENT + silver*SILVER_INCREMENT + copper;
    }

    public void add(int gold, int silver, int copper) {
        this.total += gold*GOLD_INCREMENT + silver*SILVER_INCREMENT + copper;
    }

    public void subtract(int gold, int silver, int copper) {
        add(-gold, -silver, -copper);
    }

    public static ComponentType<EntityStore, MoneyComponent> getComponentType() {
        return GoldEconomy.getMoneyComponentType();
    }

    @Override
    public @Nullable Component<EntityStore> clone() {
        return new MoneyComponent(this.total);
    }

    @Override
    public String toString() {
        return "<" + getGold() + "G; " + getSilver() + "S; " + getCopper() + "C>";
    }
}
