package net.lordimass.dialogue.util;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import net.lordimass.dialogue.DialogueMod;
import net.lordimass.dialogue.DialogueRuntime;
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
            .replace("</color>", "</span>");
    }

    public static SubstringAndLastChar substringFromTokens(List<String> tokens, int end) {
        StringBuilder sb = new StringBuilder();
        char lastChar = 0;
        int count = end;
        int closingTagsNeeded = 0;
        Queue<String> queue = new ArrayBlockingQueue<>(tokens.size());
        queue.addAll(tokens);
        while (!queue.isEmpty() && count > 0) {
            String token = queue.poll();
            if (token.charAt(0) == '<') closingTagsNeeded += token.charAt(1) == '/' ? -1 : 1;
            else {
                count -= 1;
                lastChar = token.charAt(0);
            }

            sb.append(token);
        }
        while (!queue.isEmpty() && closingTagsNeeded != 0) {
            String token = queue.poll();
            if (token.charAt(0) == '<') {
                closingTagsNeeded += token.charAt(1) == '/' ? -1 : 1;
                sb.append(token);
            }
        }
        if (closingTagsNeeded != 0) {
            throw new RuntimeException("Missing " + closingTagsNeeded + " closing tags for string consisting of the following tokens:\n" + tokens);
        }
        return new SubstringAndLastChar(sb.toString(), lastChar);
    }

    public static List<String> tokenize(String string) {
        ArrayList<String> tokens =  new ArrayList<>(string.length());

        Queue<Character> queue = new ArrayBlockingQueue<>(string.length());
        queue.addAll(new ArrayList<>(string.chars().mapToObj(c -> (char) c).collect(Collectors.toList())));
        while (!queue.isEmpty()) {
            Character c = queue.poll();
            if (c == null) continue;

            StringBuilder token = new StringBuilder().append(c);
            if (c.equals('<')) {
                while (!c.equals('>')) {
                    c = queue.poll();
                    if (c == null) {
                        throw new RuntimeException("Angle bracket not closed in format string!");
                    }
                    token.append(c);
                }
            }

            Character peek = queue.peek();
            if (c.equals('\\') && peek != null && peek.equals('n')) {
                token.append(queue.poll());
            }

            tokens.add(token.toString());
        }

        return tokens;
    }

    public record SubstringAndLastChar(String string, char lastChar) {}
}
