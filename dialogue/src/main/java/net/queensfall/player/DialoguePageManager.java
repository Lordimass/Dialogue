package net.queensfall.player;

import au.ellie.hyui.builders.*;
import au.ellie.hyui.html.TemplateProcessor;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import net.queensfall.codec.DialogueAsset;
import net.queensfall.codec.DialogueEntry;
import javax.annotation.Nonnull;

import static net.queensfall.util.TranslationUtils.translateWithHYUIML;

public class DialoguePageManager {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final AssetStore<String, DialogueAsset, DefaultAssetMap<String, DialogueAsset>>
        STORE = AssetRegistry.getAssetStore(DialogueAsset.class);

    private static final int MAX_CHOICES = 4;

    private final PlayerRef playerRef;
    private final Store<EntityStore> store;
    private PageBuilder builder;
    private HyUIPage hyUIPage;
    @Getter
    private DialogueAsset dialogueAsset;

    public DialoguePageManager(@Nonnull PlayerRef playerRef,
                               @Nonnull Store<EntityStore> store,
                               @Nonnull String dialogId
                        ) {
        this.playerRef = playerRef;
        this.store = store;
        openDialogue(dialogId);
    }

    private void openDialogue(String dialogName) {
        builder = PageBuilder
            .pageForPlayer(playerRef)
            .loadHtml("Pages/Dialogue.html", new TemplateProcessor())
            .enableRuntimeTemplateUpdates(true)
            .withLifetime(CustomPageLifetime.CanDismiss);
        hyUIPage = builder.open(store);
        assert hyUIPage != null;

        dialogueAsset = STORE.getAssetMap().getAsset(dialogName);
        if (dialogueAsset == null) {
            LOGGER.atSevere().log("DialogueAsset '"+ dialogName+"' could not be found");
            hyUIPage.close();
            return;
        }

        switch (dialogueAsset.getType()) {
            case Dialogue -> populateDialogue();
            case Choice -> populateChoices();
            default -> buildNEXTButton();
        }

        hyUIPage.updatePage(true);
    }

    private void populateDialogue() {
        StringBuilder entries = new StringBuilder();
        for (DialogueEntry entry : dialogueAsset.entries) {
            entries
                .append(translateWithHYUIML(entry.content, playerRef))
                .append("\n");
        }
        entries.delete(entries.length()-1, entries.length());

        builder.getTemplateProcessor()
            .setVariable("title", translateWithHYUIML("dialogue." + dialogueAsset.getId() + ".name", playerRef))
            .setVariable("content", entries.toString());
        buildNEXTButton();
    }

    private void populateChoices() {
        TemplateProcessor template = builder.getTemplateProcessor();
        if (dialogueAsset.entries.length >  MAX_CHOICES) {
            LOGGER.atWarning().log(
                "Dialogue choice page only supports up to 4 entries. "
                    + dialogueAsset.id
                    + " has "
                    + dialogueAsset.entries.length);
        }
        for (int i = 0; i < Math.min(dialogueAsset.entries.length, MAX_CHOICES); i++) {
            DialogueEntry entry = dialogueAsset.entries[i];
            String content = translateWithHYUIML(entry.content, playerRef);
            template
                .setVariable("choice"+i, content)
                .setVariable("choice"+i+"Display", "block");
            builder.addEventListener("choice"+i, CustomUIEventBindingType.Activating, (_, ctx) -> {
                if (entry.next == null) {
                    ctx.getPage().ifPresent(HyUIPage::close);
                    return;
                }
                openDialogue(entry.next);
            });
        }
    }

    private void buildNEXTButton() {
        builder.getTemplateProcessor()
            .setVariable("nextButtonDisplay", "block")
            .setVariable("nextButtonText", dialogueAsset.next == null ? "CLOSE" : "NEXT");
        hyUIPage.updatePage(false);
        builder.addEventListener("next-button", CustomUIEventBindingType.Activating, (_, ctx) -> {
            if (dialogueAsset.next == null) {
                ctx.getPage().ifPresent(HyUIPage::close);
                return;
            }
            openDialogue(dialogueAsset.next);
        });
    }
}
