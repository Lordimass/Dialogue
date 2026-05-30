package net.lordimass.dialogue;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import net.lordimass.dialogue.component.NPCDialogueComponent;
import net.lordimass.dialogue.parameter.ParameterContext;
import net.lordimass.dialogue.parameter.ParameterProcessor;
import net.lordimass.dialogue.parameter.ParameterResolver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DialogueMod extends JavaPlugin {
    private static DialogueMod INSTANCE;
    public static final String[] BUILTIN_VOICE_IDS = new String[]{"F1", "F2", "F3", "F4", "M1", "M2", "M3", "M4"};
    protected static final Map<String, ParameterProcessor<?>> processors = new ConcurrentHashMap<>();

    @Getter
    protected static ComponentType<EntityStore, NPCDialogueComponent> dialogueComponentType;

    public DialogueMod(JavaPluginInit init) {
        super(init);
        INSTANCE = this;
    }

    public static DialogueMod get() {
        return DialogueMod.INSTANCE;
    }

    public static <C> void registerParameter(
        String key,
        Class<C> contextType,
        ParameterResolver<C> resolver
    ) {
        processors.put(key, new ParameterProcessor<>(key, contextType, resolver));
    }

    public static String process(String message, ParameterContext ctx) {
        for (ParameterProcessor<?> processor : DialogueMod.processors.values()) {
            if (processor.supports(ctx)) {
                message = message.replace(
                    processor.key(),
                    processor.resolve(ctx)
                );
            }
        }
        return message;
    }

    @Override
    public void setup() {
        DialogueRuntime.setup(this);
    }

    @Override
    public void start() {
        DialogueRuntime.start(this);
    }
}
