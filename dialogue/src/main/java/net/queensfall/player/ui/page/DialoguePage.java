package net.queensfall.player.ui.page;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import net.queensfall.DialogueMod;
import net.queensfall.dialogue.DialogueAsset;
import net.queensfall.dialogue.DialogueType;
import net.queensfall.dialogue.event.ChoiceSelectedEvent;
import net.queensfall.dialogue.event.DialogueEventContext;
import net.queensfall.dialogue.event.DialogueInputReceivedEvent;
import net.queensfall.dialogue.event.NextDialogueEvent;
import net.queensfall.macro.MacroAsset;
import net.queensfall.util.ParameterContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DialoguePage extends InteractiveCustomUIPage<DialoguePageData> {

    private static final AssetStore<String, DialogueAsset, DefaultAssetMap<String, DialogueAsset>>
            STORE = AssetRegistry.getAssetStore(DialogueAsset.class);
    @Nonnull
    @Getter
    private final Ref<EntityStore> ref;
    @Nonnull
    @Getter
    private final Store<EntityStore> store;
    public DialogueType currentDialogueType = DialogueType.UNSET;
    public boolean isProcessing = true;
    public String key;

    public String input = "";

    public DialoguePage(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, PlayerRef playerRef, String key) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, DialoguePageData.CODEC);
        setKey(key);

        this.ref = ref;
        this.store = store;
    }

    private DialogueEventContext createEventContext(ParameterContext params) {
        return new DialogueEventContext(
                key,
                playerRef,
                ref,
                store,
                params,
                DialogueMod.get()
        );
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> entRef, @Nonnull UICommandBuilder commands, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> entStore) {
        DialogueAsset asset = getAsset();

        if (asset == null) {
            this.close();
            return;
        }

        currentDialogueType = asset.getType();

        if (currentDialogueType.equals(DialogueType.UNSET)) {
            close();
            return;
        }

        commands.append(currentDialogueType.uiPath);

        if(currentDialogueType.isDialog() || currentDialogueType.isInput())
            if (asset.getNext() == null)
                commands.set("#NextButton.Text", "CLOSE");

        ParameterContext ctx = new ParameterContext();
        ctx.put(PlayerRef.class, playerRef);
        ctx.put(DialogueMod.class, DialogueMod.get());
        DialogueMod.get().populateContext(ctx);

        Message message = Message.translation("dialogue." + asset.getId() + ".name");
        message = Message.translation(DialogueMod.get().process(message.getAnsiMessage(), ctx));
        commands.set("#NameTitle.TextSpans", message);

        if(currentDialogueType.isInput()) {
            eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#Input", EventData.of("@Input", "#ContentGroup #Input.Value"), false);
            eventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    "#NextButton",
                    EventData.of("InputNext", "true")
            );
        } else if (currentDialogueType.isDialog()) {
            eventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    "#NextButton",
                    EventData.of("DialogNext", "true")
            );
        } else if (currentDialogueType.isChoice()) {
            if(asset.entries != null && asset.entries.length > 0) {
                for (int i = 0; i < asset.entries.length; i++) {
                    eventBuilder.addEventBinding(
                            CustomUIEventBindingType.Activating,
                            "#Content" + i,
                            EventData.of("Choice" + i, "true")
                    );
                }
            }
        }

        /*
            Entry fulfillment is not needed for input dialogue.
         */
        if(!currentDialogueType.isInput())
            if(asset.entries != null && asset.entries.length > 0) {
                for (int i = 0; i < asset.entries.length; i++) {
                    message = Message.translation(asset.entries[i].content);
                    message = Message.translation(DialogueMod.get().process(message.getAnsiMessage(), ctx));
                    commands.set("#Content" + i + ".TextSpans", message);
                }
            }

        if (currentDialogueType.equals(DialogueType.UNSET))
            this.close();

        isProcessing = false;
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> entRef, @Nonnull Store<EntityStore> entStore, @Nonnull DialoguePageData data) {
        boolean needsUpdate = false;

        DialogueAsset asset = getAsset();

        if (asset == null) {
            this.close();
            return;
        }

        if(data.input() != null) {
            this.input = data.input();

            needsUpdate = true;
        }

        if (!this.isProcessing) {
            if (Boolean.TRUE.equals(data.inputNext())) {
                ParameterContext params = new ParameterContext();
                params.put(PlayerRef.class, playerRef);
                params.put(DialogueMod.class, DialogueMod.get());
                DialogueMod.get().populateContext(params);

                DialogueEventContext ctx = createEventContext(params);
                DialogueMod.get().dialogEvents()
                        .dispatch(key, new DialogueInputReceivedEvent(ctx, asset, this.input));

                this.input = "";
                needsUpdate = true;
                isProcessing = true;
                executeMacro(asset.getMacro());
                setKey(asset.getNext());
            } else if (Boolean.TRUE.equals(data.dialogNext())) {
                ParameterContext params = new ParameterContext();
                params.put(PlayerRef.class, playerRef);
                params.put(DialogueMod.class, DialogueMod.get());
                DialogueMod.get().populateContext(params);

                DialogueEventContext ctx = createEventContext(params);
                DialogueMod.get().dialogEvents()
                        .dispatch(key, new NextDialogueEvent(ctx, asset));

                isProcessing = true;
                executeMacro(asset.getMacro());

                setKey(asset.getNext());
            }

            for (int i = 0; i < 4; i++) {
                if (Boolean.TRUE.equals(data.getEntry(i))) {
                    ParameterContext params = new ParameterContext();
                    params.put(PlayerRef.class, playerRef);
                    params.put(DialogueMod.class, DialogueMod.get());
                    DialogueMod.get().populateContext(params);

                    DialogueEventContext ctx = createEventContext(params);
                    DialogueMod.get().dialogEvents()
                            .dispatch(key, new ChoiceSelectedEvent(ctx, asset, i,  asset.entries[i]));

                    handleEntry(i, asset);
                }
            }

            if (isProcessing) {
                this.rebuild();
            }
        }

        if(needsUpdate)
            this.sendUpdate();
    }

    private void handleEntry(int index, DialogueAsset asset) {
        if (asset.entries.length <= index)
            return;

        isProcessing = true;

        MacroAsset macro = asset.entries[index].getMacro();
        executeMacro(macro);

        setKey(asset.entries[index].getNext());
    }

    private void executeMacro(@Nullable MacroAsset macro) {
        if (macro == null)
            return;

        ParameterContext ctx = new ParameterContext();
        ctx.put(PlayerRef.class, playerRef);
        ctx.put(DialogueMod.class, DialogueMod.get());

        ArrayDeque<String> commands = Arrays.stream(macro.getCommands())
                .map(cmd -> DialogueMod.get().process(cmd, ctx))
                .collect(Collectors.toCollection(ArrayDeque::new));

        HytaleServer.get()
                .getCommandManager()
                .handleCommands(ConsoleSender.INSTANCE, commands);
    }

    public DialogueAsset getAsset() {
        if (STORE == null)
            return null;

        return STORE.getAssetMap().getAsset(key);
    }

    public void setKey(String key) {
        this.key = key;
    }
}
