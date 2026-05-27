package net.lordimass.dialogue.sensor.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import lombok.Getter;
import net.lordimass.dialogue.sensor.SensorDialogue;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;

public class BuilderSensorDialogue extends BuilderSensorBase {
    @Getter
    private String blockId;

    @Override
    public @Nullable String getShortDescription() {
        return "Checks whether the currently active dialogue's BlockIdentifier matches the given one";
    }

    @Override
    public @Nullable String getLongDescription() {
        return getShortDescription();
    }

    @Override
    public @Nullable Sensor build(BuilderSupport builderSupport) {
        return new SensorDialogue(this);
    }

    @Override
    public @Nullable BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
        this.getString(
            data,
            "BlockIdentifier",
            blockId -> this.blockId = blockId,
            null,
            null,
            BuilderDescriptorState.Stable,
            "The BlockIdentifier of the dialogue block to check for.",
            "The BlockIdentifier of the dialogue block to check for. The default value for the ID of a dialogue block is the ID of the asset (i.e. the JSON file name)."
        );
        return this;
    }
}
