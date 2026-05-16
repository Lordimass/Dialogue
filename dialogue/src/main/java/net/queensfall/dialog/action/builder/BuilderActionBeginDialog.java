package net.queensfall.dialog.action.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.InstructionType;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import net.queensfall.dialog.action.ActionBeginDialog;
import net.queensfall.dialog.validator.DialogExistsValidator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class BuilderActionBeginDialog extends BuilderActionBase {
    protected final AssetHolder dialogId = new AssetHolder();

    @Nullable
    @Override
    public String getShortDescription() {
        return "Begin the dialog for the current player";
    }

    @Nullable
    @Override
    public String getLongDescription() {
        return getShortDescription();
    }

    @Nullable
    @Override
    public Action build(@Nonnull BuilderSupport builderSupport) {
        return new ActionBeginDialog(this, builderSupport);
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderActionBeginDialog readConfig(@Nonnull JsonElement data) {
        this.requireAsset(data, "Dialog", this.dialogId, DialogExistsValidator.required(), BuilderDescriptorState.Stable, "The dialog to begin", null);
        this.requireInstructionType(EnumSet.of(InstructionType.Interaction));
        return this;
    }

    public String getDialogId(@Nonnull BuilderSupport support) {
        return this.dialogId.get(support.getExecutionContext());
    }
}
