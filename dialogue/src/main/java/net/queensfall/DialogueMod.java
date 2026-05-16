package net.queensfall;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.npc.NPCPlugin;
import lombok.Getter;
import net.queensfall.dialogue.DialogueAsset;
import net.queensfall.dialogue.action.builder.BuilderActionBeginDialogue;
import net.queensfall.dialogue.event.DialogueEventBus;
import net.queensfall.dialogue.event.DialogueInputReceivedEvent;
import net.queensfall.macro.MacroAsset;
import net.queensfall.demo.DemoClass;
import net.queensfall.player.DialoguePlayer;
import net.queensfall.player.DialoguePlayerConfig;
import net.queensfall.player.commands.DialogueCommand;
import net.queensfall.util.ParameterContext;
import net.queensfall.util.ParameterContextContributor;
import net.queensfall.util.ParameterProcessor;
import net.queensfall.util.ParameterResolver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DialogueMod extends JavaPlugin {

    public static final Map<PlayerRef, DialoguePlayer> dialoguePlayerMap = new ConcurrentHashMap<>();
    private static DialogueMod INSTANCE;
    private final Map<String, ParameterProcessor<?>> processors = new ConcurrentHashMap<>();
    @Getter
    private final com.hypixel.hytale.server.core.util.Config<Config> config;

    private final List<ParameterContextContributor> contributors = new ArrayList<>();

    private final DialogueEventBus dialogEvents = new DialogueEventBus();

    public DialogueEventBus dialogEvents() {
        return dialogEvents;
    }

    public DialogueMod(JavaPluginInit init) {
        super(init);
        INSTANCE = this;
        config = withConfig(Config.CODEC);
    }

    public static DialogueMod get() {
        return DialogueMod.INSTANCE;
    }

    public void registerContextContributor(ParameterContextContributor contributor) {
        contributors.add(contributor);
    }

    public void populateContext(ParameterContext ctx) {
        for (ParameterContextContributor contributor : contributors) {
            contributor.contribute(ctx);
        }
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

        /*
         * Start of demo-code. For use by developers. This is an example on how to use their own custom data.
         */

        DialogueMod.get().registerParameter(
            "{mydata}",
            DemoClass.class,
            DemoClass::getCustomData
        );

        DialogueMod.get().registerContextContributor(ctx -> {
            ctx.put(DemoClass.class, new DemoClass());
        });

        DialogueMod.get().dialogEvents().register(
            "IntroDialogue01",
            DialogueInputReceivedEvent.class,
            event -> {
                PlayerRef player = event.context().player();
                System.out.println("[" + player.getUsername() + "] " + event.input());
            }
        );

        /*
         * End of demo-code.
         */

        this.getCommandRegistry().registerCommand(new DialogueCommand());

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

        HytaleAssetStore.Builder<String, MacroAsset, DefaultAssetMap<String, MacroAsset>> macroAssetBuilder =
            HytaleAssetStore.builder(
                MacroAsset.class,
                new DefaultAssetMap<>()
            );

        this.getAssetRegistry().register(
            macroAssetBuilder
                .setPath("DialogueMacro")
                .setCodec(MacroAsset.CODEC)
                .setKeyFunction(MacroAsset::getId)
                .loadsAfter(Interaction.class)
                .build()
        );

        this.getAssetRegistry().register(
            dialogAssetBuilder
                .setPath("Dialogue")
                .setCodec(DialogueAsset.CODEC)
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

                cfg.load().thenAccept((_cfg) -> {
                    _cfg.setUuid(player.getConfig().get().playerUuid);
                }).thenAccept((_) -> {
                    cfg.save().thenAccept((_) -> {
                        dialoguePlayerMap.remove(playerDisconnectEvent.getPlayerRef());
                    });
                });
            }
        );
    }

    @Override
    public void start() {
        getConfig().save();
    }

    @Override
    public void shutdown() {
        getConfig().save();
    }
}
