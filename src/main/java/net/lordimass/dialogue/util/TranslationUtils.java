package net.lordimass.dialogue.util;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import net.lordimass.dialogue.DialogueMod;
import net.lordimass.dialogue.parameter.ParameterContext;

import java.util.Objects;

public final class TranslationUtils {
    private TranslationUtils() {}

    public static Message translate(String key, PlayerRef playerRef) {
        ParameterContext ctx = new ParameterContext();
        ctx.put(PlayerRef.class, playerRef);
        ctx.put(DialogueMod.class, DialogueMod.get());

        Message message = Message.translation(key);
        message = Message.translation(DialogueMod.get().process(message.getAnsiMessage(), ctx));

        return message;
    }

    public static String translateWithHYUIML(String key, PlayerRef playerRef) {
        return Objects.requireNonNull(translate(key, playerRef).getMessageId())
            .replaceAll("<i>(.*)</i>", "<span data-hyui-italic=true>$1</span>")
            .replaceAll("<b>(.*)</b>", "<span data-hyui-bold=true>$1</span>")
            .replaceAll("<color is=(.*)>(.*)</color>", "<span data-hyui-color=$1>$2</span>");
    }
}
