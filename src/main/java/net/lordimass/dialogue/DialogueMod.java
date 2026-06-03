package net.lordimass.dialogue;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import net.lordimass.dialogue.component.NPCDialogueComponent;


public class DialogueMod extends JavaPlugin {
    private static DialogueMod INSTANCE;
    public static final String[] BUILTIN_VOICE_IDS = new String[]{"F1", "F2", "F3", "F4", "M1", "M2", "M3", "M4"};

    @Getter
    protected static ComponentType<EntityStore, NPCDialogueComponent> dialogueComponentType;

    public DialogueMod(JavaPluginInit init) {
        super(init);
        INSTANCE = this;
    }

    public static DialogueMod get() {
        return DialogueMod.INSTANCE;
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
