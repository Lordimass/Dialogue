package net.lordimass.dialogue.player;

import au.ellie.hyui.builders.*;
import au.ellie.hyui.html.TemplateProcessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import lombok.Setter;
import net.lordimass.dialogue.codec.DialogueAsset;
import net.lordimass.dialogue.codec.DialogueEntry;
import net.lordimass.dialogue.component.NPCDialogueComponent;
import net.lordimass.dialogue.system.TypewriterDialogueSystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static net.lordimass.dialogue.util.TranslationUtils.translateWithHYUIML;

public class DialoguePageManager {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final int MAX_CHOICES = 4;

    private final PlayerRef playerRef;
    private final Ref<EntityStore> npcRef;
    private PageBuilder builder;
    @Getter
    private HyUIPage hyUIPage;
    @Getter
    private String completeString;
    @Getter
    @Setter
    private float typewriterDt;
    @Getter
    private DialogueAsset dialogue;

    public DialoguePageManager(@Nonnull PlayerRef playerRef, @Nullable Ref<EntityStore> npcRef,
                               DialogueAsset dialogue
                        ) {
        this.playerRef = playerRef;
        this.npcRef = npcRef;
        openDialogue(dialogue);
    }

    private void openDialogue(DialogueAsset dialogue) {
        this.dialogue = dialogue;
        if (dialogue == null) {if (hyUIPage != null) close();  return;}
        completeString = "";

        builder = PageBuilder
            .pageForPlayer(playerRef)
            .loadHtml("Pages/Dialogue.html", new TemplateProcessor())
            .enableRuntimeTemplateUpdates(true)
            .onDismiss(this::closeCallback)
            .withLifetime(CustomPageLifetime.CanDismiss);
        hyUIPage = builder.open(Objects.requireNonNull(playerRef.getReference()).getStore());
        assert hyUIPage != null;

        switch (this.dialogue.getType()) {
            case Dialogue -> populateDialogue();
            case Choice -> populateChoices();
            default -> buildNEXTButton();
        }
        hyUIPage.updatePage(true);

        NPCDialogueComponent.update(npcRef, dialogue, playerRef);

        if (dialogue.isTypewriterEffect()) {
            TypewriterDialogueSystem.tickingPageManagers.add(this);
            typewriterDt = 0;
        }
    }

    private void populateDialogue() {
        StringBuilder entries = new StringBuilder();
        for (DialogueEntry entry : dialogue.entries) {
            entries
                .append(translateWithHYUIML(entry.getContent(), playerRef))
                .append("\n");
        }
        entries.delete(entries.length()-1, entries.length());
        completeString = entries.toString();
        String inProgressString = dialogue.isTypewriterEffect() ? "" : completeString;

        builder.getTemplateProcessor()
            .setVariable("title", translateWithHYUIML(dialogue.getTitle(), playerRef))
            .setVariable("content", inProgressString);
        buildNEXTButton();
    }

    private void populateChoices() {
        TemplateProcessor template = builder.getTemplateProcessor();
        if (dialogue.entries.length >  MAX_CHOICES) {
            LOGGER.atWarning().log(
                "Dialogue choice page only supports up to 4 entries. "
                    + dialogue.id
                    + " has "
                    + dialogue.entries.length);
        }
        for (int i = 0; i < Math.min(dialogue.entries.length, MAX_CHOICES); i++) {
            DialogueEntry entry = dialogue.entries[i];
            String content = translateWithHYUIML(entry.getContent(), playerRef);
            template
                .setVariable("choice"+i, content)
                .setVariable("choice"+i+"Display", "block");
            builder.addEventListener("choice"+i, CustomUIEventBindingType.Activating, _ -> {
                if (entry.getNext() == null) {close();return;}
                openDialogue(entry.getNext());
            });
        }
    }

    private void buildNEXTButton() {
        boolean isNextExists = dialogue.getNext() != null;
        builder.getTemplateProcessor()
            .setVariable("nextButtonDisplay", "block")
            .setVariable("nextButtonText", isNextExists ? "NEXT" : "CLOSE");
        hyUIPage.updatePage(false);
        builder.addEventListener("next-button", CustomUIEventBindingType.Activating, (_, ctx) -> {
            if (isNextExists) openDialogue(dialogue.getNext());
            else close();
        });
    }

    private void closeCallback(HyUIPage hyUIPage, Boolean aBoolean) {
        NPCDialogueComponent.clear(npcRef);
        int index = TypewriterDialogueSystem.tickingPageManagers.indexOf(this);
        if (index >= 0) {
            TypewriterDialogueSystem.tickingPageManagers.remove(index);
        }
    }

    public void close() {
        hyUIPage.close();
    }
}
