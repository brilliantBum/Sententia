package com.anotherspectrum.sententia.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.anotherspectrum.anotherlibrary.annotations.commands.Command;
import com.anotherspectrum.anotherlibrary.utils.StringUtil;
import com.anotherspectrum.sententia.friend.FriendFile;
import com.anotherspectrum.sententia.friend.FriendListType;
import com.anotherspectrum.sententia.friend.FriendMenu;
import com.anotherspectrum.sententia.util.CommandHelp;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 친구 명령어가 포함된 클래스입니다.
 */
@CommandAlias("친구|friend|friends|fri")
@Description("다른 유저와 친구를 맺을 수 있습니다.")
@CommandPermission("sententia.friend.use")
public class FriendCommand extends BaseCommand implements CommandHelp {

    private final String PREFIX = "<color:#FFDC10><b>[ FRIEND ]</b><white>";

    /**
     * '/친구' 만 입력했을 때 발동되는 메소드입니다.
     * @param sender
     */
    @Default
    public void friendCommand(@NotNull CommandSender sender) {
        Player player = checkConsole(sender);
        new FriendMenu(player).open(player);
    }

    @Subcommand("도움말|헬프|help")
    @Description("친구 시스템의 명령어 도움말을 확인할 수 있습니다.")
    public void friendCommandHelp(@NotNull CommandSender sender) {
        Player player = checkConsole(sender);
        send(player);
    }

    @Subcommand("목록|list")
    @Description("당신의 친구 목록을 확인합니다.")
    public void friendList(@NotNull CommandSender sender) {
        Player player = checkConsole(sender);
        FriendFile friendFile = new FriendFile(player);
        if (friendFile.getList(FriendListType.LIST).size() < 1) {
            player.sendMessage(StringUtil.format("<red><b>[ FRIEND ] </b><white>당신과 친구를 맺은 플레이어를 찾을 수 없습니다!"));
            return;
        }

        new FriendMenu(player).friendListClick(player);
    }

    @Subcommand("수락|accept|acc")
    @Description("다른 플레이어로부터 온 친구 요청을 확인하고, 수락합니다.")
    @CommandCompletion("@friendreqlist")
    public void friendAccept(@NotNull CommandSender sender, @NotNull String offlinePlayerName) {
        Player player = checkConsole(sender);
        FriendFile friendFile = new FriendFile(player);
        if (friendFile.getList(FriendListType.REQUESTS).size() < 1) {
            player.sendMessage(StringUtil.format("<red><b>[ FRIEND ] </b><white>당신에게 친구 요청을 보낸 플레이어를 찾을 수 없습니다!"));
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(offlinePlayerName);
        if (!friendFile.getList(FriendListType.REQUESTS).contains(offlinePlayer.getName() + "(" + offlinePlayer.getUniqueId() + ")")) {
            player.sendMessage(StringUtil.format("<red><b>[ FRIEND ] </b><gray>" + offlinePlayer.getName() + " <white>님에게는 친구 요청을 받지 않았습니다!"));
            return;
        }

        new FriendMenu(player).friendRequestsClick(player);
    }

    @Subcommand("요청|추가|보내기|send|sending")
    @Description("그 플레이어에게 친구 요청을 전송합니다.")
    @CommandCompletion("@players")
    public void friendSendRequest(@NotNull CommandSender sender, @NotNull String offlinePlayerName) {
        Player player = checkConsole(sender);

        // /친구 요청 [자기자신] 을 입력했을 때에 대한 필터림.
        if (offlinePlayerName.contains(player.getName())) {
            player.sendMessage(StringUtil.format(PREFIX + " 자기 자신에게는 친구 요청을 보낼 수 없습니다!"));
            return;
        }

        // 커맨드 입력자의 친구 파일을 불러옴.
        FriendFile senderFriendFile = new FriendFile(player);
        // 커맨드 입력자가 기입한 오프라인 플레이어의 닉네임을 사용해 오프라인 플레이어를 불러옴.
        // /친구 요청 [오프라인 플레이어 닉네임]
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(offlinePlayerName);
        // 그렇게 선언한 오프라인 플레이어 필드를 사용하여 해당 오프라인 플레이어의 정보를 불러옴. (닉네임(UUID))
        String offlinePlayerInformation = offlinePlayer.getName() + "(" + offlinePlayer.getUniqueId() + ")";

        // '그 오프라인 플레이어랑 이미 친구 상태면' 에 대한 필터링.
        if (senderFriendFile.getList(FriendListType.LIST).contains(offlinePlayerInformation)) {
            player.sendMessage(StringUtil.format(PREFIX + " <gray>" + offlinePlayer.getName() + "<white> 님과는 이미 친구 상태입니다!"));
            return;
        }

        // '그 오프라인 플레이어에겐 이미 친구 요청을 보냄' 에 대한 필터링.
        if (senderFriendFile.getList(FriendListType.SENDING).contains(offlinePlayerInformation)) {
            player.sendMessage(StringUtil.format(PREFIX + " 이미 <gray>" + offlinePlayer.getName() + "<white> 님에게 친구 요청을 보낸 상태입니다!"));
            return;
        }

        // 오프라인 플레이어의 파일도 불러와줍시다.
        FriendFile targetFriendFile = new FriendFile(offlinePlayer);

        // ~ 파일 수정 ~
        senderFriendFile.addInformationToList(FriendListType.SENDING, offlinePlayer);
        targetFriendFile.addInformationToList(FriendListType.REQUESTS, player);

        // 메시지 전송
        player.sendMessage(StringUtil.format(PREFIX + " <gray>" + offlinePlayer.getName() + " <white>님에게 친구 요청을 <green>전송<white>했습니다!"));
    }

    /**
     * 명령어를 사용하는 유저가 콘솔인지를 체크합니다.
     * @param sender 체크할 커맨드 실행자
     * @return 콘솔이 아닌 {@link Player} 형태의 플레이어
     */
    private Player checkConsole(@NotNull CommandSender sender) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(StringUtil.format("[ FRIEND ] 콘솔에서는 해당 명령어를 사용하실 수 없습니다!"));
            return null;
        }
        return (Player) sender;
    }

    @Override
    public void send(Player player) {
        String parser = "올바른 사용법: <gray>";
        String hover = "<hover:show_text:'<gray>클릭 시 자동완성 됩니다.'>";

        String one = "        " + parser + hover + "<click:run_command:/친구 목록>/친구 목록</click></hover>";
        String two = "        " + parser + hover + "<click:suggest_command:/친구 요청 [플레이어]>/친구 요청 [플레이어]</click></hover>";
        String three = "        " + parser + hover + "<click:suggest_command:/친구 삭제 [플레이어]>/친구 삭제 [플레이어]</click></hover>";

        player.sendMessage(StringUtil.format("  <color:#FFDC10><b>===== [ FRIEND SYSTEM ] ====="));
        player.sendMessage(StringUtil.format(one));
        player.sendMessage(StringUtil.format(two));
        player.sendMessage(StringUtil.format(three));
        player.sendMessage(StringUtil.format("  <color:#FFDC10><b>===== ===== ===== ===== ====="));
    }

}

