package net.lordimass.dialogue.player;

import au.ellie.hyui.builders.HyUIAnchor;
import au.ellie.hyui.builders.ImageBuilder;
import au.ellie.hyui.builders.PageBuilder;
import au.ellie.hyui.builders.UIElementBuilder;
import lombok.Getter;
import lombok.Setter;
import net.lordimass.dialogue.codec.CharacterAsset;
import net.lordimass.dialogue.system.DialogueAnimationSystem;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PortraitElementManager {
    private static final String SPEAKER_BOTTOM_ANCHOR = "-50";
    private static final String LISTENER_BOTTOM_ANCHOR = "-150";
    private final DialogueAnimationSystem.AnchorTemplateKeys ANCHOR_TEMPLATE_KEYS_LEFT = new DialogueAnimationSystem.AnchorTemplateKeys();
    private final DialogueAnimationSystem.AnchorTemplateKeys ANCHOR_TEMPLATE_KEYS_RIGHT = new DialogueAnimationSystem.AnchorTemplateKeys();

    @Setter @Getter private CharacterAsset leftCharacter;
    @Setter @Getter private CharacterAsset rightCharacter;
    @Setter @Getter private SpeakingDirection speakingDirection = SpeakingDirection.LEFT;

    public PortraitElementManager() {
        ANCHOR_TEMPLATE_KEYS_LEFT.setBottom("portraitLeftBottomAnchor");
        ANCHOR_TEMPLATE_KEYS_RIGHT.setBottom("portraitRightBottomAnchor");
    }

    public void updatePortraits(PageBuilder pageBuilder, DialoguePageManager pageManager) {
        CharacterAsset character1 = pageManager.getDialogue().getCharacter();
        CharacterAsset character2 = pageManager.getDialogue().getCharacter2();

        updatePortraitVars(character1, character2);
        updateSpeakingDirection(character1, character2);

        Map<String, String> newVariables = generateNewVariablesMap();
        pageBuilder.getTemplateProcessor().setVariables(newVariables);

        DialogueAnimationSystem.SimpleAnchor start = new DialogueAnimationSystem.SimpleAnchor();
        start.setBottom(-50);
        DialogueAnimationSystem.SimpleAnchor end = new DialogueAnimationSystem.SimpleAnchor();
        end.setBottom(-150);
        DialogueAnimationSystem.ANIMATIONS.add(new DialogueAnimationSystem.Animation<>(
            "portraitLeft", ImageBuilder.class,
            start, end,
            3,
            ANCHOR_TEMPLATE_KEYS_LEFT,
            pageManager
        ));
    }

    private @NonNull Map<String, String> generateNewVariablesMap() {
        String leftPortrait = leftCharacter != null
            ? speakingDirection == SpeakingDirection.LEFT ? leftCharacter.getPortrait() : leftCharacter.getInactivePortrait()
            : null;
        String rightPortrait = rightCharacter != null
            ? speakingDirection == SpeakingDirection.RIGHT ? rightCharacter.getPortrait() : rightCharacter.getInactivePortrait()
            : null;
        Map<String, String> newVariables = new HashMap<>();
        newVariables.put("portraitLeftSrc", "Common/Portraits/"+ leftPortrait);
        newVariables.put("portraitLeftDisplay", leftPortrait == null ? "none" : "block");
        newVariables.put(
            "portraitLeftBottomAnchor",
            speakingDirection == SpeakingDirection.LEFT ? SPEAKER_BOTTOM_ANCHOR : LISTENER_BOTTOM_ANCHOR
        );
        newVariables.put("portraitRightSrc", "Common/Portraits/"+ rightPortrait);
        newVariables.put("portraitRightDisplay", rightPortrait == null ? "none" : "block");
        newVariables.put(
            "portraitRightBottomAnchor",
            speakingDirection == SpeakingDirection.RIGHT ? SPEAKER_BOTTOM_ANCHOR : LISTENER_BOTTOM_ANCHOR
        );

        return newVariables;
    }

    private void updatePortraitVars(CharacterAsset character1, CharacterAsset character2) {
        boolean leftMatches1 = CharacterAsset.equals(leftCharacter, character1);
        boolean leftMatches2 = CharacterAsset.equals(leftCharacter, character2);
        boolean rightMatches1 = CharacterAsset.equals(rightCharacter, character1);
        boolean rightMatches2 = CharacterAsset.equals(rightCharacter, character2);
        boolean matchesCurrent = (leftMatches1 && rightMatches2) || (leftMatches2 && rightMatches1);
        if (!matchesCurrent) {
            if (leftMatches1 && leftCharacter != null) {
                setRightCharacter(character2);
            } else if (leftMatches2 && leftCharacter != null) {
                setRightCharacter(character1);
            } else if (character1 != null) {
                setLeftCharacter(character1);
                setRightCharacter(character2);
            } else if (character2 != null) {
                setLeftCharacter(character2);
                setRightCharacter(null);
            } else {
                setLeftCharacter(null);
                setRightCharacter(null);
            }
        }
    }

    private void updateSpeakingDirection(CharacterAsset character1, CharacterAsset character2) {
        CharacterAsset nonNullPortrait = character1 != null ? character1 : character2;
        if (nonNullPortrait == null) return;
        if (leftCharacter != null && CharacterAsset.equals(leftCharacter, nonNullPortrait)) {
            setSpeakingDirection(SpeakingDirection.LEFT);
        } else if (rightCharacter != null && CharacterAsset.equals(rightCharacter, nonNullPortrait)) {
            setSpeakingDirection(SpeakingDirection.RIGHT);
        }
    }

    public enum SpeakingDirection {
        LEFT, RIGHT
    }
}
