package net.queensfall;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.npc.NPCPlugin;
import lombok.Getter;
import net.queensfall.asset.dialog.HyspeechDialogAsset;
import net.queensfall.asset.dialog.action.builder.BuilderActionBeginDialog;
import net.queensfall.asset.dialog.event.DialogEventBus;
import net.queensfall.asset.dialog.event.DialogInputReceivedEvent;
import net.queensfall.asset.macro.HyspeechMacroAsset;
import net.queensfall.demo.DemoClass;
import net.queensfall.player.HyspeechPlayer;
import net.queensfall.player.HyspeechPlayerConfig;
import net.queensfall.player.commands.HyspeechCommand;
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

    public static final Map<PlayerRef, HyspeechPlayer> hyspeechPlayerMap = new ConcurrentHashMap<>();
    private static DialogueMod INSTANCE;
    private final Map<String, ParameterProcessor<?>> processors = new ConcurrentHashMap<>();
    @Getter
    private final Config<HyspeechConfig> config;

    private final List<ParameterContextContributor> contributors = new ArrayList<>();

    private final DialogEventBus dialogEvents = new DialogEventBus();

    public DialogEventBus dialogEvents() {
        return dialogEvents;
    }

    public DialogueMod(JavaPluginInit init) {
        super(init);
        INSTANCE = this;
        config = withConfig(HyspeechConfig.CODEC);
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
            "IntroDialog01",
            DialogInputReceivedEvent.class,
            event -> {
                PlayerRef player = event.context().player();
                System.out.println("[" + player.getUsername() + "] " + event.input());
            }
        );

        /*
         * End of demo-code.
         */

        this.getCommandRegistry().registerCommand(new HyspeechCommand());

        NPCPlugin.get().registerCoreComponentType("HyspeechBeginDialog", BuilderActionBeginDialog::new);

        registerAssetTypes();
        registerEvents();
    }

    public void registerAssetTypes() {
        HytaleAssetStore.Builder<String, HyspeechDialogAsset, DefaultAssetMap<String, HyspeechDialogAsset>> dialogAssetBuilder =
            HytaleAssetStore.builder(
                HyspeechDialogAsset.class,
                new DefaultAssetMap<>()
            );

        HytaleAssetStore.Builder<String, HyspeechMacroAsset, DefaultAssetMap<String, HyspeechMacroAsset>> macroAssetBuilder =
            HytaleAssetStore.builder(
                HyspeechMacroAsset.class,
                new DefaultAssetMap<>()
            );

        this.getAssetRegistry().register(
            macroAssetBuilder
                .setPath("HyspeechMacro")
                .setCodec(HyspeechMacroAsset.CODEC)
                .setKeyFunction(HyspeechMacroAsset::getId)
                .loadsAfter(Interaction.class)
                .build()
        );

        this.getAssetRegistry().register(
            dialogAssetBuilder
                .setPath("HyspeechDialog")
                .setCodec(HyspeechDialogAsset.CODEC)
                .setKeyFunction(HyspeechDialogAsset::getId)
                .loadsAfter(Interaction.class)
                .build()
        );
    }

    public void registerEvents() {
        this.getEventRegistry().register(
            PlayerConnectEvent.class,
            playerConnectEvent ->
                hyspeechPlayerMap.putIfAbsent(
                    playerConnectEvent.getPlayerRef(),
                    new HyspeechPlayer(playerConnectEvent.getPlayerRef())
                )
        );

        this.getEventRegistry().register(
            PlayerDisconnectEvent.class,
            playerDisconnectEvent -> {
                HyspeechPlayer player = hyspeechPlayerMap.get(playerDisconnectEvent.getPlayerRef());

                Config<HyspeechPlayerConfig> cfg = new Config<>(
                    new File("config/hyspeech/player_data/").toPath(),
                    playerDisconnectEvent.getPlayerRef().getUsername(),
                    HyspeechPlayerConfig.CODEC
                );

                cfg.load().thenAccept((_cfg) -> {
                    _cfg.setUuid(player.getConfig().get().playerUuid);
                }).thenAccept((_) -> {
                    cfg.save().thenAccept((_) -> {
                        hyspeechPlayerMap.remove(playerDisconnectEvent.getPlayerRef());
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
