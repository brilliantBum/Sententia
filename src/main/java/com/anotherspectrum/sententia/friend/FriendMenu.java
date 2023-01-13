package com.anotherspectrum.sententia.friend;

import com.anotherspectrum.anotherlibrary.menu.ItemCreator;
import com.anotherspectrum.anotherlibrary.menu.MenuManager;
import com.anotherspectrum.anotherlibrary.utils.StringUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 친구 시스템의 메인 메뉴 클래스입니다.
 */
public class FriendMenu extends MenuManager {

    // /친구 를 입력한 플레이어의 친구 데이터 파일을 먼저 생성해두고,
    // 친구 관련된 메뉴에서 불러올 수 있도록 필드와 그에 따른 Getter 를 선언했습니다.
    private @Getter final FriendFile friendFile;

    // 검은색 유리판을 먼저 선언해둠으로써 해당 클래스에서는 이 아이템을
    // 변수 호출로 자유롭게 사용할 수 있습니다.
    private final ItemStack BLACK_PANE = ItemCreator.create(Material.BLACK_STAINED_GLASS_PANE, 1, Component.empty());

    public FriendMenu(Player player) {
        super(1, "<b>FRIEND", true);

        // 친구 메뉴를 오픈한 플레이어의 파일을 불러옵니다.
        this.friendFile = new FriendFile(player);

        fillRows(1, BLACK_PANE);

        setItems();
    }

    /**
     * 한 메소드안에서 이 긴 코드를 집어넣게 되면...
     * <p>괜히 보기 힘드니까 따로 만들어서 위 클래스 메소드에 불러와준겁니다.</p>
     */
    private void setItems() {
        setItem(1, ItemCreator.create(Material.BOOK, 1,
                StringUtil.format("<color:#E9FF1C>[ 친구 목록 ]"),
                StringUtil.ellipsis(
                        "<gray>- 당신과 친구 사이인 유저들을 모두 표시합니다.",
                        "",
                        "<gray>- 현재 당신의 친구 수: <white>{friends}명".replace(
                                "{friends}", String.valueOf(friendFile.getList(FriendListType.LIST).size())),
                        "<gray>- 현재 <green>온라인<gray> 상태인 친구 수: <white>{friends_online}명"
                        ), true), ((player, inventoryClickEvent) -> friendListClick(player)));

        setItem(4, ItemCreator.create(Material.WRITABLE_BOOK, 1,
                StringUtil.format("<color:#AAFF1C>[ 받은 친구 요청 목록 ]"),
                StringUtil.ellipsis(
                        "<gray>- 당신에게 친구 요청을 전송한 유저 목록을 모두 표시합니다.",
                        "",
                        "<gray>- 현재 당신에게 친구 요청을 보낸 유저 수: <white>{friends_request}명".replace(
                                "{friends_request}", String.valueOf(friendFile.getList(FriendListType.REQUESTS).size())
                        )
                ), true));

        setItem(7, ItemCreator.create(Material.OAK_SIGN, 1,
                StringUtil.format("<color:#C3FF76>[ 보낸 친구 요청 목록 ]"),
                StringUtil.ellipsis(
                        "<gray>- 당신이 친구 요청을 전송한 유저 목록을 모두 표시합니다.",
                        "",
                        "<gray>- 당신이 친구 요청을 보낸 유저 수: <white>{friends_sending}명".replace(
                                "{friends_sending}", String.valueOf(friendFile.getList(FriendListType.SENDING).size())
                        )
                ), true), ((player, inventoryClickEvent) -> friendSendingClick(player)));
    }

    /**
     * 유저가 메뉴에서 친구 목록 버튼을 클릭했을 때 발동되는 메소드 입니다.
     * @param player 메뉴를 클릭한 플레이어
     */
    public void friendListClick(Player player) {
        MenuManager menu = new MenuManager(6, "<b>FRIEND LIST", true);
        menu.open(player);

        // 필터링을 해준 겁니다.
        // 해당 플레이어의 친구 파일 속 friend.list 경로 리스트에 값이 존재하지 않거나 사이즈가 0이면 (친구가 없으면) 구문을 통과힙니다.
        if (friendFile.getList(FriendListType.LIST) == null || friendFile.getList(FriendListType.LIST).size() < 1) {
            // ...그리고 해당 아이템을 클릭하면 인벤토리를 닫습니다.
            menu.setItem(22, ItemCreator.create(Material.RED_STAINED_GLASS_PANE, 1,
                    StringUtil.format("<red>[ 당신과 친구를 맺은 유저가 존재하지 않습니다. ]"),
                    StringUtil.ellipsis("<gray>- /친구 요청 [플레이어] 명령어를 사용해 친구 요청을 보낼 수 있습니다!")),
                    (clicker, event) -> clicker.closeInventory());
            return;
        }

        // 친구 수 만큼 반복문을 돌려줍시다.
        // 인벤토리 속 슬롯에 해당 플레이어의 친구들의 머리를 전시할 겁니다.
        for (int i = 0; i < friendFile.getList(FriendListType.LIST).size(); i++) {

            // 먼저 친구 목록에 있는 모든 친구들의 정보를 로드합니다. (예시: '닉네임(UUID)')
            String offlineFriendNameBefore = friendFile.getList(FriendListType.LIST).get(i);

            // 그리고 그 정보에서 닉네임만을 가져옵니다. 문자열 0번 자리에서부터 '(' 가 있는 곳의 index 까지를 가져오는 겁니다.
            String offlineFriendNameAfter = offlineFriendNameBefore.substring(0, offlineFriendNameBefore.indexOf("("));

            // 위의 선언한 변수는 친구의 닉네임입니다. 이를 오프라인 플레이어로 가져옵니다.
            OfflinePlayer offlineFriend = Bukkit.getOfflinePlayer(offlineFriendNameAfter);

            // 이제 AnotherLibrary 를 사용해 메뉴에 위 친구의 머리를 가져옵시다.
            menu.setItem(i, ItemCreator.createSkull(offlineFriend,
                    StringUtil.format("<dark_gray>[ <gray>" + offlineFriend.getName() + " <dark_gray>]"),
                    StringUtil.ellipsis(
                            "<gray>- 현재 <white>" + offlineFriend.getName() + "<gray> 님은 " + (offlineFriend.isOnline() ? "<green>온라인<gray>" : "<red>오프라인<gray>") + " 상태입니다.",
                            "", "<gray>- 우클릭 시 <white>" + offlineFriend.getName() + "<gray> 님과의 친구를 <red>끊<gray>습니다.")),
                    (clicker, inventoryClickEvent) -> {
                        // 머리를 우클릭하면 이 친구를 친구에서 삭제할 수 있습니다.
                        // 그 기능을 만들어볼겁니다.
                        if (inventoryClickEvent.isRightClick()) {
                            removeConfirm(offlineFriend);
                        }
                    });
        }

    }

    public void friendRequestsClick(Player player) {
        MenuManager menu = new MenuManager(6, "<b>FRIEND REQUESTS", true);
        menu.open(player);

        // 필터링을 해준 겁니다.
        // 해당 플레이어의 친구 파일 속 friend.requests 경로 리스트에 값이 존재하지 않거나 사이즈가 0이면 (보낸 요청이 없으면) 구문을 통과힙니다.
        if (friendFile.getList(FriendListType.REQUESTS) == null || friendFile.getList(FriendListType.REQUESTS).size() < 1) {
            // ...그리고 해당 아이템을 클릭하면 인벤토리를 닫습니다.
            menu.setItem(22, ItemCreator.create(Material.RED_STAINED_GLASS_PANE, 1,
                            StringUtil.format("<red>[ 당신은 아무 친구 요청도 받지 않았습니다. ]"),
                            StringUtil.ellipsis("<gray>- /친구 요청 [플레이어] 명령어를 사용해 친구 요청을 보낼 수 있습니다!")),
                    (clicker, event) -> clicker.closeInventory());
            return;
        }

        // 요청을 보낸 유저 수 만큼 반복문을 돌려줍시다.
        // 인벤토리 속 슬롯에 해당 '오프라인 플레이어'의 친구들의 머리를 전시할 겁니다.
        for (int i = 0; i < friendFile.getList(FriendListType.REQUESTS).size(); i++) {

            // 먼저 sending 경로에 있는 모든 친구들의 정보를 로드합니다. (예시: '닉네임(UUID)')
            String offlineFriendNameBefore = friendFile.getList(FriendListType.REQUESTS).get(i);

            // 그리고 그 정보에서 닉네임만을 가져옵니다. 문자열 0번 자리에서부터 '(' 가 있는 곳의 index 까지를 가져오는 겁니다.
            String offlineFriendNameAfter = offlineFriendNameBefore.substring(0, offlineFriendNameBefore.indexOf("("));

            // 위의 선언한 변수는 친구의 닉네임입니다. 이를 오프라인 플레이어로 가져옵니다.
            OfflinePlayer offlineFriend = Bukkit.getOfflinePlayer(offlineFriendNameAfter);

            // 이제 AnotherLibrary 를 사용해 메뉴에 위 친구의 머리를 가져옵시다.
            menu.setItem(i, ItemCreator.createSkull(offlineFriend,
                    StringUtil.format("<dark_gray>[ <gray>" + offlineFriend.getName() + " <dark_gray>]"),
                    StringUtil.ellipsis(
                            "<gray>- 현재 <white>" + offlineFriend.getName() + "<gray> 님은 " +
                                    (offlineFriend.isOnline() ? "<green>온라인<gray>" : "<red>오프라인<gray>") + " 상태입니다.",
                            "", "<gray>- 우클릭 시 친구 요청을 <green>수락<gray>합니다.")
            ), (clicker, inventoryClickEvent) -> {
                if (inventoryClickEvent.isRightClick()) {
                    friendAccept(clicker, offlineFriend);
                }
            });
        }
    }

    public void friendAccept(Player player, OfflinePlayer target) {
        if (!friendFile.getList(FriendListType.REQUESTS).contains(target.getName() + "(" + target.getUniqueId() + ")")) {
            player.sendMessage(StringUtil.format("<red><b>[ FRIEND ] </b><white>당신은 <gray>" + target.getName() + "<white> 님에게 친구 요청을 받지 않았습니다."));
            return;
        }
        FriendFile targetFriend = new FriendFile(target);

        friendFile.addInformationToList(FriendListType.LIST, target);
        targetFriend.addInformationToList(FriendListType.LIST, player);

        friendFile.removeInformationToList(FriendListType.REQUESTS, target);
        targetFriend.removeInformationToList(FriendListType.SENDING, player);
    }

    public void friendSendingClick(Player player) {
        MenuManager menu = new MenuManager(6, "<b>FRIEND SENDING", true);
        menu.open(player);

        // 필터링을 해준 겁니다.
        // 해당 플레이어의 친구 파일 속 friend.sending 경로 리스트에 값이 존재하지 않거나 사이즈가 0이면 (보낸 요청이 없으면) 구문을 통과힙니다.
        if (friendFile.getList(FriendListType.SENDING) == null || friendFile.getList(FriendListType.SENDING).size() < 1) {
            // ...그리고 해당 아이템을 클릭하면 인벤토리를 닫습니다.
            menu.setItem(22, ItemCreator.create(Material.RED_STAINED_GLASS_PANE, 1,
                            StringUtil.format("<red>[ 당신은 아무에게도 친구 요청을 보내지 않았습니다. ]"),
                            StringUtil.ellipsis("<gray>- /친구 요청 [플레이어] 명령어를 사용해 친구 요청을 보낼 수 있습니다!")),
                    (clicker, event) -> clicker.closeInventory());
            return;
        }

        // 요청을 보낸 유저 수 만큼 반복문을 돌려줍시다.
        // 인벤토리 속 슬롯에 해당 '오프라인 플레이어'의 친구들의 머리를 전시할 겁니다.
        for (int i = 0; i < friendFile.getList(FriendListType.LIST).size(); i++) {

            // 먼저 sending 경로에 있는 모든 친구들의 정보를 로드합니다. (예시: '닉네임(UUID)')
            String offlineFriendNameBefore = friendFile.getList(FriendListType.SENDING).get(i);

            // 그리고 그 정보에서 닉네임만을 가져옵니다. 문자열 0번 자리에서부터 '(' 가 있는 곳의 index 까지를 가져오는 겁니다.
            String offlineFriendNameAfter = offlineFriendNameBefore.substring(0, offlineFriendNameBefore.indexOf("("));

            // 위의 선언한 변수는 친구의 닉네임입니다. 이를 오프라인 플레이어로 가져옵니다.
            OfflinePlayer offlineFriend = Bukkit.getOfflinePlayer(offlineFriendNameAfter);

            // 이제 AnotherLibrary 를 사용해 메뉴에 위 친구의 머리를 가져옵시다.
            menu.setItem(i, ItemCreator.createSkull(offlineFriend,
                            StringUtil.format("<dark_gray>[ <gray>" + offlineFriend.getName() + " <dark_gray>]"),
                            StringUtil.ellipsis(
                                    "<gray>- 현재 <white>" + offlineFriend.getName() + "<gray> 님은 " +
                                            (offlineFriend.isOnline() ? "<green>온라인<gray>" : "<red>오프라인<gray>") + " 상태입니다.")));
        }

    }

    /**
     * '정말로 target 님과의 친구를 끊으시겠어요?' 창을 띄워주기 위해
     * 메소드를 생성해서 좀 보기쉽게 만들어줍시다.
     * @param player 친구를 삭제하려고 하는 플레이어
     * @param target 친구 삭제당한 플레이어 (...불쌍)
     */
    private void removeConfirm(OfflinePlayer target) {
        MenuManager menu = new MenuManager(1, "<b>Really?!", true);
        menu.fillRows(1, BLACK_PANE);
        menu.setItem(4, ItemCreator.create(Material.LIME_STAINED_GLASS_PANE, 1,
                StringUtil.format("<color:#51FF73>[ " + target.getName() + " 님과 친구 끊기 ]"),
                StringUtil.ellipsis("<gray>- 클릭 시 친구를 끊습니다.")),
                (clicker, inventoryClickEvent) -> removeFriend(clicker, target));

        menu.setItem(6, ItemCreator.create(Material.RED_STAINED_GLASS_PANE, 1,
                StringUtil.format("<color:#FF5F46>[ 취소하기 ]"),
                StringUtil.ellipsis("<gray>- 클릭 시 취소합니다.")),
                (clicker, inventoryClickEvent) -> clicker.closeInventory());
    }


    private void removeFriend(Player player, OfflinePlayer target) {
        player.closeInventory();

        FriendFile targetFriend = new FriendFile(target);
        if (!targetFriend.getList(FriendListType.LIST).contains(player.getName() + "(" + player.getUniqueId() + ")")) {
            player.sendMessage(StringUtil.format("<red><b>[ FRIEND ] </b><white>당신은 <gray>" + target.getName() + "<white> 님과 친구가 아닙니다!"));
            return;
        }

        friendFile.removeInformationToList(FriendListType.LIST, target);
        targetFriend.removeInformationToList(FriendListType.LIST, player);

        player.sendMessage(StringUtil.format("<color:#51FF73><b>[ FRIEND ] </b><white>성공적으로 <gray>" + target.getName() + "<white> 님과 친구를 끊었습니다."));
    }

}
