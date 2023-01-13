package com.anotherspectrum.sententia.friend;

import java.util.Locale;

/**
 * 플레이어의 친구 데이터 파일에서 문자열 리스트를 불러올 때
 * 사용하는 열거형 클래스입니다. 호출 시의 필터링으로 사용합니다.
 */
public enum FriendListType {

    LIST,     // 어느 플레이어의 친구 목록 리스트
    SENDING,  // 어느 플레이어가 '보낸' 친구 요청 목록 리스트
    REQUESTS; // 어느 플레이어가 '받은' 친구 요청 목록 리스트


    /**
     * 열거 객체를 소문자로 불러옵니다.
     * <p>아래와 같이 사용할 수 있습니다.</p>
     * <pre>{@code FriendListType.SENDING.toString(); // 반환값: "sending"}</pre>
     * <p>{@link Object} 클래스의 toString() 메소드를 오버라이딩 하기에,</p>
     * <p>반드시 마지막에 '.toString()' 을 붙혀줄 필요가 없습니다.</p>
     * @return 불러온 열거 객체의 소문자 이름
     */
    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}
