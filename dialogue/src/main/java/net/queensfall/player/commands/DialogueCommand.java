package net.queensfall.player.commands;

import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class DialogueCommand extends AbstractPlayerCommand {
    public DialogueCommand() {
        super("dialogue", "Allows control of dialogue.");

        addSubCommand(new SetupCommand());
        addSubCommand(new BeginCommand());
        addSubCommand(new ConfigCommand());
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        commandContext.sender().sendMessage(Message.raw("Usage: /dialogue <arg>"));

//        PageBuilder.pageForPlayer(playerRef).loadHtml("Pages/Dialogue.html").open(store);
        PageBuilder.pageForPlayer(playerRef).fromHtml("""
            <style>
                .dialog-box {
                    anchor-width: 1000;
                    anchor-height: 250;
                    anchor-bottom: 50;
                    background-color: rgba(26, 26, 46, 0.95);
                }
            
                .title {
                   text-align: center;
                   align: center;
                   vertical-align: center;
                   horizontal-align: center;
                   padding: 0 19;
                   font-size: 15;
                   color: #b4c8c9
                   font-family: "Secondary"
                   font-weight: bold
                   letter-spacing: 0
                }
            </style>
            
            <div class="container dialog-box">
                <div class="title">
                    INSERT TITLE HERE
                </div>
                <div class="container-contents">
                    Hello, World!
                </div>
            </div>
            
            """).open(store);
    }
}
