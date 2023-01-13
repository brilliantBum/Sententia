package com.anotherspectrum.sententia.friend;

import com.anotherspectrum.anotherlibrary.files.FileManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 친구 데이터 파일 속 문자열 리스트 경로에는
 * <li>'{@link OfflinePlayer} 형태의 플레이어의 이름(UUID)' 가 저장됩니다.</li>
 * <pre>{@code
 * friend:
 *  list:
 *   - Else_JunSuk(u-u-i-d...)
 * }</pre>
 * 이런 식으로요.
 */
public class FriendFile extends FileManager {

    /**
     * 친구 데이터파일을 불러옵니다.
     * <p>불러오면 해당 파일을 제어할 수 있습니다.</p>
     * <p>해당 클래스 인스턴스를 불러올 때, 플레이어의 데이터 파일이 존재하지 않으면</p>
     * <p>생성합니다.</p>
     * @param player 해당 플레이어의 친구 데이터 파일을 불러옵니다.
     */
    public FriendFile(@NotNull Player player) {
        super(player, "friend_data.yml");

        isNotContains("friend", () -> {
            getConfig().set("name", player.getName());
            getConfig().set("uuid", player.getUniqueId().toString());
            getConfig().set("friend.list", new ArrayList<>());     // 해당 플레이어의 모든 친구 목록
            getConfig().set("friend.sending", new ArrayList<>());  // 해당 플레이어가 '보낸' 친구 요청 목록
            getConfig().set("friend.requests", new ArrayList<>()); // 해당 플레이어가 '받은' 친구 요청 목록
            save();
        });
    }

    /**
     * 친구 데이터파일을 불러옵니다.
     * <p>해당 인스턴스는 오프라인 플레이어만을 취급합니다.</p>
     * <p>불러오면 해당 파일을 제어할 수 있습니다.</p>
     * <p>해당 클래스 인스턴스를 불러올 때, 플레이어의 데이터 파일이 존재하지 않으면</p>
     * <p>생성합니다.</p>
     * @param offlinePlayer 해당 플레이어의 친구 데이터 파일을 불러옵니다.
     */
    public FriendFile(@NotNull OfflinePlayer offlinePlayer) {
        super("data/" + offlinePlayer.getName() + "(" + offlinePlayer.getUniqueId() + ")", "friend_data.yml");

        isNotContains("friend", () -> {
            getConfig().set("name", offlinePlayer.getName());
            getConfig().set("uuid", offlinePlayer.getUniqueId().toString());
            getConfig().set("friend.list", new ArrayList<>());     // 해당 플레이어의 모든 친구 목록
            getConfig().set("friend.sending", new ArrayList<>());  // 해당 플레이어가 '보낸' 친구 요청 목록
            getConfig().set("friend.requests", new ArrayList<>()); // 해당 플레이어가 '받은' 친구 요청 목록
            save();
        });
    }

    /**
     * 친구 데이터 파일에서 특정 List 를 불러온 후,
     * 해당 플레이어의 정보를 추가합니다.
     * @param friendListType 불러올 열거 객체
     * @param player 타겟 플레이어
     */
    public void addInformationToList(FriendListType friendListType, Player player) {
        List<String> list = getList(friendListType);
        list.add(player.getName() + "(" + player.getUniqueId() + ")");
        setList(friendListType, list);
        save();
    }

    /**
     * 친구 데이터 파일에서 특정 List 를 불러온 후,
     * 해당 오프라인 플레이어의 정보를 추가합니다.
     * @param friendListType 불러올 열거 객체
     * @param offlinePlayer 타겟 오프라인 플레이어
     */
    public void addInformationToList(FriendListType friendListType, OfflinePlayer offlinePlayer) {
        List<String> list = getList(friendListType);
        list.add(offlinePlayer.getName() + "(" + offlinePlayer.getUniqueId() + ")");
        setList(friendListType, list);
        save();
    }

    /**
     * 친구 데이터 파일에서 특정 List 를 불러온 후,
     * 해당 플레이어의 정보를 삭제합니다.
     * @param friendListType 불러올 열거 객체
     * @param player 타겟 플레이어
     */
    public void removeInformationToList(FriendListType friendListType, Player player) {
        List<String> list = getList(friendListType);
        list.remove(player.getName() + "(" + player.getUniqueId() + ")");
        setList(friendListType, list);
        save();
    }

    /**
     * 친구 데이터 파일에서 특정 List 를 불러온 후,
     * 해당 오프라인 플레이어의 정보를 삭제합니다.
     * @param friendListType 불러올 열거 객체
     * @param offlinePlayer 타겟 오프라인 플레이어
     */
    public void removeInformationToList(FriendListType friendListType, OfflinePlayer offlinePlayer) {
        List<String> list = getList(friendListType);
        list.remove(offlinePlayer.getName() + "(" + offlinePlayer.getUniqueId() + ")");
        setList(friendListType, list);
        save();
    }

    /**
     * 친구 데이터 파일에서 특정 List 를 불러옵니다.
     * @param friendListType 불러올 열거 객체
     * @return 선택한 열거 객체에 따른 리스트
     */
    public List<String> getList(FriendListType friendListType) {
        return getConfig().getStringList("friend." + friendListType);
    }

    /**
     * 친구 데이터 파일에서 특정 List 를 세팅합니다.
     * @param friendListType 세팅할 열거 객체
     * @param value 선택한 열거 객체에 값을 세팅할 새로운 값
     */
    public void setList(FriendListType friendListType, List<String> value) {
        getConfig().set("friend." + friendListType, value);
    }

}
