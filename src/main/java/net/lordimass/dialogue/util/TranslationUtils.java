package net.lordimass.dialogue.util;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import net.lordimass.dialogue.DialogueMod;
import net.lordimass.dialogue.parameter.ParameterContext;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

public final class TranslationUtils {

    private TranslationUtils() {}

    public static Message translate(String key, PlayerRef playerRef) {
        ParameterContext ctx = new ParameterContext();
        ctx.put(PlayerRef.class, playerRef);
        ctx.put(DialogueMod.class, DialogueMod.get());

        Message message = Message.translation(key);
        message = Message.translation(DialogueMod.process(message.getAnsiMessage(), ctx));

        return message;
    }

    public static String translateWithHYUIML(String key, PlayerRef playerRef) {
        return Objects.requireNonNull(translate(key, playerRef).getMessageId())
            .replace("<i>", "<span data-hyui-italic=true>")
            .replace("</i>", "</span>")
            .replace("<b>", "<span data-hyui-bold=true>")
            .replace("</b>", "</span>")
            .replaceAll("<color is=(.*?)>", "<span data-hyui-color=$1>")
            .replace("</color>", "</span>")
            .replace("</sound>", ""); // Closing </sound> tags are redundant.
    }
}
