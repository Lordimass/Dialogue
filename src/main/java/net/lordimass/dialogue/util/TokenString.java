package net.lordimass.dialogue.util;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

@ToString
public class TokenString {
    public final String base;
    @Getter
    private String inProgressString;
    private final List<String> tokens;
    private int pointer = -1;
    @Getter
    private boolean complete;
    public static final String SOUND_TAG_REGEX = "<sound is=\"(.*?)\">";
    private static final String[] NO_CLOSING_TAG_REQUIRED = {SOUND_TAG_REGEX};

    public static void main() {
        TokenString tokenString = new TokenString("<color is=\"#777777\">Uh</color><color is=\"#999999\">...</color>\nDo I know you?<sound is=\"Voice_Effect_Question\">");
        while (!tokenString.isComplete()) {
            System.out.println(tokenString.next() + " " + tokenString.inProgressString);
        }

    }

    public TokenString(String base) {
        this.base = base;
        this.inProgressString = "";
        tokens = tokenize(base);
    }

    public String next() {
        boolean cont = true;
        String token = null;
        while (cont) {
            pointer++;
            if (pointer >= tokens.size()) {
                complete = true;
                break;
            }
            token = tokens.get(pointer);
            if (token.charAt(0) != '<' || !isClosingTagRequired(token)) {
                cont = false;
            }
        }
        inProgressString = buildInProgressString();
        return token;
    }

    private String buildInProgressString() {
        StringBuilder sb = new StringBuilder();
        int count = pointer+1;
        int closingTagsNeeded = 0;
        Queue<String> queue = new ArrayBlockingQueue<>(tokens.size());
        queue.addAll(tokens);
        while (!queue.isEmpty() && count > 0) {
            String token = queue.poll();
            boolean closingTagRequired = isClosingTagRequired(token);
            if (token.charAt(0) == '<' && closingTagRequired) {
                closingTagsNeeded += token.charAt(1) == '/' ? -1 : 1;
            }
            count--;
            if (closingTagRequired) {
                sb.append(token);
            }
        }
        while (!queue.isEmpty() && closingTagsNeeded != 0) {
            String token = queue.poll();
            if (token.charAt(0) == '<' && isClosingTagRequired(token)) {
                closingTagsNeeded += token.charAt(1) == '/' ? -1 : 1;
                sb.append(token);
            }
        }
        if (closingTagsNeeded != 0) {
            throw new RuntimeException("Missing " + closingTagsNeeded + " closing tags for string:\n" + base);
        }
        return sb.toString();
    }

    private boolean isClosingTagRequired(String token) {
        for (String tagCheck : NO_CLOSING_TAG_REQUIRED) {
            if (token.matches(tagCheck)) {
                return false;
            }
        }
        return true;
    }

    private static List<String> tokenize(String string) {
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
}
