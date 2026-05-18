package net.queensfall.player.ui.page;

import au.ellie.hyui.builders.HyUIPage;
import au.ellie.hyui.builders.LabelBuilder;
import au.ellie.hyui.builders.PageBuilder;
import au.ellie.hyui.html.TemplateProcessor;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import net.queensfall.DialogueMod;
import net.queensfall.dialogue.DialogueAsset;
import net.queensfall.dialogue.DialogueEntry;
import net.queensfall.util.ParameterContext;

import javax.annotation.Nonnull;

public class DialoguePageManager {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final AssetStore<String, DialogueAsset, DefaultAssetMap<String, DialogueAsset>>
        STORE = AssetRegistry.getAssetStore(DialogueAsset.class);

    private final PlayerRef playerRef;
    private final Store<EntityStore> store;
    private final PageBuilder builder;
    private HyUIPage hyUIPage;
    @Getter
    private DialogueAsset dialogueAsset;

    public DialoguePageManager(@Nonnull PlayerRef playerRef,
                               @Nonnull Store<EntityStore> store,
                               @Nonnull String dialogId
                        ) {
        this.playerRef = playerRef;
        this.store = store;

        TemplateProcessor template = new TemplateProcessor();
        builder = PageBuilder
            .pageForPlayer(playerRef)
            .loadHtml("Pages/Dialogue.html", template)
            .enableRuntimeTemplateUpdates(true)
            .withLifetime(CustomPageLifetime.CanDismiss);
        hyUIPage = builder.open(store);
        openDialogue(dialogId);
    }

    private void openDialogue(String dialogName) {
        dialogueAsset = STORE.getAssetMap().getAsset(dialogName);
        if (dialogueAsset == null) {
            LOGGER.atSevere().log("DialogueAsset '"+ dialogName+"' could not be found");
            hyUIPage.close();
            return;
        }

        for (DialogueEntry entry : dialogueAsset.entries) {
            // TODO: Handle colour and other rich tags in .lang file by showing a message instead of just the string
            // TODO: Handle multi-lines

            builder.getTemplateProcessor()
                .setVariable("content", translate(entry.content))
                .setVariable("title", translate("dialogue." + dialogueAsset.getId() + ".name"));
            hyUIPage.updatePage(false);
        }
    }

    private Message translate(String key) {
        ParameterContext ctx = new ParameterContext();
        ctx.put(PlayerRef.class, playerRef);
        ctx.put(DialogueMod.class, DialogueMod.get());
        DialogueMod.get().populateContext(ctx);

        Message message = Message.translation(key);
        message = Message.translation(DialogueMod.get().process(message.getAnsiMessage(), ctx));
        return message;
    }
}
