package net.lordimass.dialogue;

import com.creditor.Creditor;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.npc.NPCPlugin;
import net.lordimass.dialogue.action.builder.BuilderActionBeginDialogue;
import net.lordimass.dialogue.codec.DialogueAsset;
import net.lordimass.dialogue.component.NPCDialogueComponent;
import net.lordimass.dialogue.player.DialoguePlayer;
import net.lordimass.dialogue.player.DialoguePlayerConfig;
import net.lordimass.dialogue.player.commands.DialogueCommand;
import net.lordimass.dialogue.sensor.builder.BuilderSensorDialogue;
import net.lordimass.dialogue.system.DialogueTickingSystem;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.lordimass.dialogue.DialogueMod.registerParameter;

/**
 * Class to hold methods and functionality which must be run on plugin boot. Separated from
 * <code>DialogueMod</code> to allow this mod to be used as a Java library and packaged as part
 * of other mods.
 * <br><br>
 * Call <code>DialogueRuntime.setup</code> and <code>DialogueRuntime.start</code> from inside
 * your plugin's <code>setup</code> and <code>start</code> methods respectively to initialise the
 * mod if it's being used as a library.
 */
public class DialogueRuntime {
    private static final Map<PlayerRef, DialoguePlayer> dialoguePlayerMap = new ConcurrentHashMap<>();

    private DialogueRuntime() {}

    // Used to prevent mod from being initialised multiple times if more than one mod is installed which uses Dialogue as a library.
    private static boolean isSetupDone = false;
    private static boolean isStartDone = false;

    public static void setup(JavaPlugin host) {
        if (isSetupDone) return;
        registerParameter("{username}", PlayerRef.class, PlayerRef::getUsername);
        registerParameter("{uuid}", PlayerRef.class, p -> p.getUuid().toString());
        registerParameter("{lang}", PlayerRef.class, PlayerRef::getLanguage);

        DialogueMod.dialogueComponentType = host.getEntityStoreRegistry().registerComponent(NPCDialogueComponent.class, NPCDialogueComponent::new);

        host.getCommandRegistry().registerCommand(new DialogueCommand());

        NPCPlugin.get().registerCoreComponentType("Dialogue", BuilderSensorDialogue::new);
        NPCPlugin.get().registerCoreComponentType("BeginDialogue", BuilderActionBeginDialogue::new);

        registerAssetTypes(host);
        registerEvents(host);
        Creditor.setup(host);
        isSetupDone = true;
    }

    public static void start(JavaPlugin host) {
        if (isStartDone) return;
        host.getEntityStoreRegistry().registerSystem(new DialogueTickingSystem());
        Creditor.start(host);
        isStartDone = true;
    }

    private static void registerAssetTypes(JavaPlugin host) {
        HytaleAssetStore.Builder<String, DialogueAsset, DefaultAssetMap<String, DialogueAsset>> dialogAssetBuilder =
            HytaleAssetStore.builder(
                DialogueAsset.class,
                new DefaultAssetMap<>()
            );

        host.getAssetRegistry().register(
            dialogAssetBuilder
                .setPath("Dialogue")
                .setCodec(DialogueAsset.ASSET_BUILDER_CODEC)
                .setKeyFunction(DialogueAsset::getId)
                .loadsAfter(Interaction.class)
                .build()
        );
    }

    private static void registerEvents(JavaPlugin host) {
        host.getEventRegistry().register(
            PlayerConnectEvent.class,
            playerConnectEvent ->
                dialoguePlayerMap.putIfAbsent(
                    playerConnectEvent.getPlayerRef(),
                    new DialoguePlayer(playerConnectEvent.getPlayerRef())
                )
        );

        host.getEventRegistry().register(
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
