package net.queensfall;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import lombok.Getter;
import net.queensfall.action.builder.BuilderActionBeginDialogue;
import net.queensfall.codec.DialogueAsset;
import net.queensfall.component.NPCDialogueComponent;
import net.queensfall.parameter.ParameterContext;
import net.queensfall.parameter.ParameterProcessor;
import net.queensfall.parameter.ParameterResolver;
import net.queensfall.player.DialoguePlayer;
import net.queensfall.player.DialoguePlayerConfig;
import net.queensfall.player.commands.DialogueCommand;
import net.queensfall.sensor.builder.BuilderSensorDialogue;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DialogueMod extends JavaPlugin {

    public static final Map<PlayerRef, DialoguePlayer> dialoguePlayerMap = new ConcurrentHashMap<>();
    private static DialogueMod INSTANCE;
    private final Map<String, ParameterProcessor<?>> processors = new ConcurrentHashMap<>();

    @Getter
    private static ComponentType<EntityStore, NPCDialogueComponent> dialogueComponentType;

    public DialogueMod(JavaPluginInit init) {
        super(init);
        INSTANCE = this;
    }

    public static DialogueMod get() {
        return DialogueMod.INSTANCE;
    }

    public <C> void registerParameter(
        String key,
        Class<C> contextType,
        ParameterResolver<C> resolver
    ) {
        processors.put(key, new ParameterProcessor<>(key, contextType, resolver));
    }

    public String process(String message, ParameterContext ctx) {
        for (ParameterProcessor<?> processor : processors.values()) {
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
        DialogueMod.get().registerParameter("{username}", PlayerRef.class, PlayerRef::getUsername);
        DialogueMod.get().registerParameter("{uuid}", PlayerRef.class, p -> p.getUuid().toString());
        DialogueMod.get().registerParameter("{lang}", PlayerRef.class, PlayerRef::getLanguage);

        dialogueComponentType = this.getEntityStoreRegistry().registerComponent(NPCDialogueComponent.class, NPCDialogueComponent::new);

        this.getCommandRegistry().registerCommand(new DialogueCommand());

        NPCPlugin.get().registerCoreComponentType("Dialogue", BuilderSensorDialogue::new);
        NPCPlugin.get().registerCoreComponentType("BeginDialogue", BuilderActionBeginDialogue::new);

        registerAssetTypes();
        registerEvents();
    }

    public void registerAssetTypes() {
        HytaleAssetStore.Builder<String, DialogueAsset, DefaultAssetMap<String, DialogueAsset>> dialogAssetBuilder =
            HytaleAssetStore.builder(
                DialogueAsset.class,
                new DefaultAssetMap<>()
            );

        this.getAssetRegistry().register(
            dialogAssetBuilder
                .setPath("Dialogue")
                .setCodec(DialogueAsset.ASSET_BUILDER_CODEC)
                .setKeyFunction(DialogueAsset::getId)
                .loadsAfter(Interaction.class)
                .build()
        );
    }

    public void registerEvents() {
        this.getEventRegistry().register(
            PlayerConnectEvent.class,
            playerConnectEvent ->
                dialoguePlayerMap.putIfAbsent(
                    playerConnectEvent.getPlayerRef(),
                    new DialoguePlayer(playerConnectEvent.getPlayerRef())
                )
        );

        this.getEventRegistry().register(
            PlayerDisconnectEvent.class,
            playerDisconnectEvent -> {
                DialoguePlayer player = dialoguePlayerMap.get(playerDisconnectEvent.getPlayerRef());

                com.hypixel.hytale.server.core.util.Config<DialoguePlayerConfig> cfg = new com.hypixel.hytale.server.core.util.Config<>(
                    new File("config/dialogue/player_data/").toPath(),
                    playerDisconnectEvent.getPlayerRef().getUsername(),
                    DialoguePlayerConfig.CODEC
                );

                cfg.load()
                    .thenAccept((_cfg) -> _cfg.setUuid(
                        player.getConfig().get().playerUuid)
                    )
                    .thenAccept(
                        (_) -> cfg.save().thenAccept(
                                (_) -> dialoguePlayerMap.remove(
                                    playerDisconnectEvent.getPlayerRef()
                                )
                            )
                );
            }
        );
    }
}
