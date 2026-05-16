package net.queensfall.dialogue.action.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.InstructionType;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import net.queensfall.dialogue.action.ActionBeginDialogue;
import net.queensfall.dialogue.validator.DialogExistsValidator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class BuilderActionBeginDialogue extends BuilderActionBase {
    protected final AssetHolder dialogueId = new AssetHolder();

    @Nullable
    @Override
    public String getShortDescription() {
        return "Begin the dialogue for the current player";
    }

    @Nullable
    @Override
    public String getLongDescription() {
        return getShortDescription();
    }

    @Nullable
    @Override
    public Action build(@Nonnull BuilderSupport builderSupport) {
        return new ActionBeginDialogue(this, builderSupport);
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderActionBeginDialogue readConfig(@Nonnull JsonElement data) {
        this.requireAsset(data, "Dialogue", this.dialogueId, DialogExistsValidator.required(), BuilderDescriptorState.Stable, "The dialogue to begin", null);
        this.requireInstructionType(EnumSet.of(InstructionType.Interaction));
        return this;
    }

    public String getDialogId(@Nonnull BuilderSupport support) {
        return this.dialogueId.get(support.getExecutionContext());
    }
}
