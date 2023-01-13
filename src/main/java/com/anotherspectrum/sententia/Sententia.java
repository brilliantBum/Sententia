package com.anotherspectrum.sententia;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import com.anotherspectrum.anotherlibrary.AnotherLibrary;
import com.anotherspectrum.sententia.command.FriendCommand;
import com.anotherspectrum.sententia.command.ShareMenuCommand;
import com.anotherspectrum.sententia.event.EventHandler;
import com.anotherspectrum.sententia.friend.FriendFile;
import com.anotherspectrum.sententia.friend.FriendListType;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Collectors;

public final class Sententia extends JavaPlugin {

    private @Getter AnotherLibrary anotherLibrary;

    private @Getter PaperCommandManager paperCommandManager;

    @Override
    public void onEnable() {
        /* LOAD MAIN LIBRARY */
        this.anotherLibrary = new AnotherLibrary("Sententia", this);

        /* LOAD COMMAND API */
        this.paperCommandManager = new PaperCommandManager(this);
        registerCommands();

        /* LOAD EVENTS */
        new EventHandler(this);
    }

    @Override
    public void onDisable() {

    }

    private void registerCommands() {
        paperCommandManager.enableUnstableAPI("help");

        paperCommandManager.registerCommand(new FriendCommand());
        paperCommandManager.registerCommand(new ShareMenuCommand());

        CommandCompletions<BukkitCommandCompletionContext> commandCompletions = paperCommandManager.getCommandCompletions();
        commandCompletions.registerAsyncCompletion("friendreqlist", c -> {
            CommandSender sender = c.getSender();
            if (sender instanceof Player) {
                Player player = (Player) sender;
                FriendFile friendFile = new FriendFile(player);

                return friendFile.getList(FriendListType.REQUESTS)
                        .stream()
                        .map(s -> s.substring(0, s.indexOf("(")))
                        .collect(Collectors.toList());
            }
            return null;
        });
    }
}
