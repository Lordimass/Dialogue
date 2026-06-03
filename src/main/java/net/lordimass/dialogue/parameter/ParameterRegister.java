package net.lordimass.dialogue.parameter;

import lombok.Getter;
import net.lordimass.dialogue.parameter.eventTag.EventTagParameterContext;
import net.lordimass.dialogue.parameter.eventTag.EventTagProcessor;
import net.lordimass.dialogue.parameter.eventTag.EventTagResolver;
import net.lordimass.dialogue.parameter.replacementParameter.ReplacementParameterProcessor;
import net.lordimass.dialogue.parameter.replacementParameter.ReplacementParameterResolver;

import java.util.concurrent.CopyOnWriteArraySet;

public final class ParameterRegister {
    @Getter
    public static final CopyOnWriteArraySet<ReplacementParameterProcessor<?>> replacementProcessors = new CopyOnWriteArraySet<>();
    @Getter
    public static final CopyOnWriteArraySet<EventTagProcessor<?>> eventTagProcessors = new CopyOnWriteArraySet<>();

    private ParameterRegister() {}

    public static <C> void registerReplacementParameter(
        String key,
        Class<C> contextType,
        ReplacementParameterResolver<C> resolver
    ) {
        replacementProcessors.add(new ReplacementParameterProcessor<>(key, contextType, resolver));
    }

    public static <C> void registerEventTag(
        String tag,
        Class<C> contextType,
        EventTagResolver<C> resolver,
        String[] parameters
    ) {
        eventTagProcessors.add(new EventTagProcessor<>(tag, contextType, resolver, parameters));
    }

    public static String processReplacementTags(String message, ParameterContext ctx) {
        for (ReplacementParameterProcessor<?> processor : replacementProcessors) {
            if (processor.supports(ctx)) {
                message = message.replace(
                    processor.key(),
                    processor.resolve(ctx)
                );
            }
        }
        return message;
    }

    public static boolean processEventTag(EventTagParameterContext ctx) {
        for (EventTagProcessor<?> processor : eventTagProcessors) {
            if (processor.resolve(ctx) != null) {
                return true;
            };
        }
        return false;
    }
}
