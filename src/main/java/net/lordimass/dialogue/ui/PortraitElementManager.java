package net.lordimass.dialogue.ui;

import au.ellie.hyui.builders.ImageBuilder;
import au.ellie.hyui.builders.PageBuilder;
import lombok.Getter;
import lombok.Setter;
import net.lordimass.dialogue.codec.CharacterAsset;
import net.lordimass.dialogue.system.DialogueAnimationSystem;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PortraitElementManager {
    private static final int SPEAKER_BOTTOM_ANCHOR = -50;
    private static final int LISTENER_BOTTOM_ANCHOR = -150;
    private static final int OFF_SCREEN_BOTTOM_ANCHOR = -400;
    private static final SimpleAnchor.AnchorTemplateKeys ANCHOR_TEMPLATE_KEYS_LEFT = new SimpleAnchor.AnchorTemplateKeys();
    private static final SimpleAnchor.AnchorTemplateKeys ANCHOR_TEMPLATE_KEYS_RIGHT = new SimpleAnchor.AnchorTemplateKeys();

    private PageBuilder pageBuilder;
    private DialoguePageManager pageManager;
    @Nullable private CharacterAsset oldLeftCharacter;
    @Nullable @Getter private CharacterAsset leftCharacter;
    @Nullable private CharacterAsset oldRightCharacter;
    @Nullable @Getter private CharacterAsset rightCharacter;
    private PortraitSide oldSpeakingDirection = PortraitSide.LEFT;
    @Getter private PortraitSide speakingDirection = PortraitSide.LEFT;

    public PortraitElementManager() {
        // TODO: This sets the value every time a new portrait element manager is made. Really these should just be entirely static
        ANCHOR_TEMPLATE_KEYS_LEFT.setBottom("portraitLeftBottomAnchor");
        ANCHOR_TEMPLATE_KEYS_RIGHT.setBottom("portraitRightBottomAnchor");
    }

    public void updatePortraits(PageBuilder pageBuilder, DialoguePageManager pageManager) {
        this.pageBuilder = pageBuilder;
        this.pageManager = pageManager;

        CharacterAsset character1 = pageManager.getDialogue().getCharacter();
        CharacterAsset character2 = pageManager.getDialogue().getCharacter2();

        updatePortraitVars(character1, character2);
        updateSpeakingDirection(character1, character2);
        setupAnimations(PortraitSide.LEFT);
        setupAnimations(PortraitSide.RIGHT);

        generateNewVariablesMap(pageBuilder);
    }

    private void setupAnimations(PortraitSide side) {
        CharacterAsset character = side == PortraitSide.LEFT ? leftCharacter : rightCharacter;
        CharacterAsset oldCharacter = side == PortraitSide.LEFT ? oldLeftCharacter : oldRightCharacter;
        boolean speaking = side == PortraitSide.LEFT
            ? speakingDirection == PortraitSide.LEFT
            : speakingDirection == PortraitSide.RIGHT;
        boolean oldSpeaking = side == PortraitSide.LEFT
            ? oldSpeakingDirection == PortraitSide.LEFT
            : oldSpeakingDirection == PortraitSide.RIGHT;
        SimpleAnchor start = new SimpleAnchor();
        SimpleAnchor end = new SimpleAnchor();

        boolean isSwap = oldCharacter != null && character != null;
        if (oldCharacter == null && character == null) return;
        if (isSwap && speaking == oldSpeaking) {
            // Add an animation that does nothing. No idea why but this fixes a bug when the
            // character states are the same from page to page.
            start.bottom = speaking ? SPEAKER_BOTTOM_ANCHOR : LISTENER_BOTTOM_ANCHOR;
            end.bottom = start.bottom;
            DialogueAnimationSystem.addAnimation(new DialogueAnimationSystem.Animation<>(
                side.elementId, ImageBuilder.class,
                start, end, 0.25F,
                side.anchorTemplateKeys,
                pageManager, null
            ));
        }
        else if (isSwap && !oldCharacter.equals(character)) {
            // Swap out for new character
            start.bottom = speaking ? SPEAKER_BOTTOM_ANCHOR : LISTENER_BOTTOM_ANCHOR;
            end.bottom = OFF_SCREEN_BOTTOM_ANCHOR;
            DialogueAnimationSystem.addAnimation(new DialogueAnimationSystem.Animation<>(
                side.elementId, ImageBuilder.class,
                start, end, 0.25F,
                side.anchorTemplateKeys,
                pageManager, () -> {
                    start.bottom = OFF_SCREEN_BOTTOM_ANCHOR;
                    end.bottom = speaking ? SPEAKER_BOTTOM_ANCHOR : LISTENER_BOTTOM_ANCHOR;
                    DialogueAnimationSystem.addAnimation(new DialogueAnimationSystem.Animation<>(
                            side.elementId, ImageBuilder.class, start, end,
                            0.25F, side.anchorTemplateKeys, pageManager, null
                        ));
                })
            );
        } else if (isSwap) {
            // Just move to new position without bobbing off-screen
            start.bottom = oldSpeaking ? SPEAKER_BOTTOM_ANCHOR : LISTENER_BOTTOM_ANCHOR;
            end.bottom = speaking ? SPEAKER_BOTTOM_ANCHOR : LISTENER_BOTTOM_ANCHOR;
            DialogueAnimationSystem.addAnimation(new DialogueAnimationSystem.Animation<>(
                side.elementId, ImageBuilder.class,
                start, end,
                0.25F,
                side.anchorTemplateKeys,
                pageManager, null
            ));
        } else {
            // Appearing and disappearing animation.
            start.bottom = oldCharacter == null
                ? OFF_SCREEN_BOTTOM_ANCHOR
                : oldSpeaking ? SPEAKER_BOTTOM_ANCHOR : LISTENER_BOTTOM_ANCHOR;
            end.bottom = oldCharacter == null
                ? speaking ? SPEAKER_BOTTOM_ANCHOR : LISTENER_BOTTOM_ANCHOR
                : OFF_SCREEN_BOTTOM_ANCHOR;
            DialogueAnimationSystem.addAnimation(new DialogueAnimationSystem.Animation<>(
                side.elementId, ImageBuilder.class,
                start, end,
                0.5F,
                side.anchorTemplateKeys,
                pageManager, null
            ));
        }
    }

    private void setCharacter(@Nullable CharacterAsset character, PortraitSide side) {
        if (side == PortraitSide.LEFT) {
            this.oldLeftCharacter = this.leftCharacter;
            this.leftCharacter = character;
        } else {
            this.oldRightCharacter = this.rightCharacter;
            this.rightCharacter = character;
        }
    }

    private void setSpeakingDirection(PortraitSide direction) {
        this.oldSpeakingDirection = this.speakingDirection;
        this.speakingDirection = direction;
    }

    private void generateNewVariablesMap(PageBuilder pageBuilder) {
        String leftPortrait = leftCharacter != null
            ? speakingDirection == PortraitSide.LEFT ? leftCharacter.getPortrait() : leftCharacter.getInactivePortrait()
            : null;
        String rightPortrait = rightCharacter != null
            ? speakingDirection == PortraitSide.RIGHT ? rightCharacter.getPortrait() : rightCharacter.getInactivePortrait()
            : null;
        Map<String, String> newVariables = new HashMap<>();
        newVariables.put("portraitLeftSrc", "Common/Portraits/"+ leftPortrait);
        newVariables.put("portraitLeftDisplay", leftPortrait == null ? "none" : "block");
        newVariables.put("portraitRightSrc", "Common/Portraits/"+ rightPortrait);
        newVariables.put("portraitRightDisplay", rightPortrait == null ? "none" : "block");

        pageBuilder.getTemplateProcessor().setVariables(newVariables);
    }

    private void updatePortraitVars(CharacterAsset character1, CharacterAsset character2) {
        boolean leftMatches1 = CharacterAsset.equals(leftCharacter, character1);
        boolean leftMatches2 = CharacterAsset.equals(leftCharacter, character2);
        boolean rightMatches1 = CharacterAsset.equals(rightCharacter, character1);
        boolean rightMatches2 = CharacterAsset.equals(rightCharacter, character2);
        boolean matchesCurrent = (leftMatches1 && rightMatches2) || (leftMatches2 && rightMatches1);
        if (!matchesCurrent) {
            if (leftMatches1 && leftCharacter != null) {
                oldLeftCharacter = leftCharacter;
                setCharacter(character2, PortraitSide.RIGHT);
            } else if (leftMatches2 && leftCharacter != null) {
                oldLeftCharacter = leftCharacter;
                setCharacter(character1, PortraitSide.RIGHT);
            } else if (character1 != null) {
                setCharacter(character1, PortraitSide.LEFT);
                setCharacter(character2, PortraitSide.RIGHT);
            } else if (character2 != null) {
                setCharacter(character2, PortraitSide.LEFT);
                setCharacter(null, PortraitSide.RIGHT);
            } else {
                setCharacter(null, PortraitSide.LEFT);
                setCharacter(null, PortraitSide.RIGHT);
            }
        } else {
            oldRightCharacter = rightCharacter;
            oldLeftCharacter = leftCharacter;
        }
    }

    private void updateSpeakingDirection(CharacterAsset character1, CharacterAsset character2) {
        CharacterAsset nonNullPortrait = character1 != null ? character1 : character2;
        if (nonNullPortrait == null) return;
        if (leftCharacter != null && CharacterAsset.equals(leftCharacter, nonNullPortrait)) {
            setSpeakingDirection(PortraitSide.LEFT);
        } else if (rightCharacter != null && CharacterAsset.equals(rightCharacter, nonNullPortrait)) {
            setSpeakingDirection(PortraitSide.RIGHT);
        }
    }

    public enum PortraitSide {
        LEFT("portraitLeft", "portraitLeftSrc", "portraitLeftDisplay", ANCHOR_TEMPLATE_KEYS_LEFT),
        RIGHT("portraitRight", "portraitRightSrc", "portraitRightDisplay", ANCHOR_TEMPLATE_KEYS_RIGHT);

        public final String elementId;
        public final String templateSrcKey;
        public final String templateDisplayKey;
        public final SimpleAnchor.AnchorTemplateKeys anchorTemplateKeys;
        PortraitSide(String elementId, String templateSrcKey, String templateDisplayKey, SimpleAnchor.AnchorTemplateKeys anchorTemplateKeys) {
            this.elementId = elementId;
            this.templateSrcKey = templateSrcKey;
            this.templateDisplayKey = templateDisplayKey;
            this.anchorTemplateKeys = anchorTemplateKeys;
        }
    }
}
