package com.anotherspectrum.sententia.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.anotherspectrum.anotherlibrary.menu.MenuManager;
import com.anotherspectrum.anotherlibrary.utils.StringUtil;
import com.anotherspectrum.sententia.friend.FriendFile;
import com.anotherspectrum.sententia.friend.FriendListType;
import com.anotherspectrum.sententia.friend.FriendMenu;
import com.anotherspectrum.sententia.util.CommandHelp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * 친구 명령어가 포함된 클래스입니다.
 */
@CommandAlias("menu")
public class ShareMenuCommand extends BaseCommand {

    @Default
    public void friendCommand(@NotNull CommandSender sender) {
        Player player = (Player) sender;
        Player target = Bukkit.getPlayer("")
        MenuManager menuManager = new MenuManager(1, "TEST", "test_1", false);
        menuManager.open(player);

        menuManagerT.open(player);
    }
}

