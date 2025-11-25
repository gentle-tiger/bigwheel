package com.game.bigwheel.domain.bigwheel;

/**
 * ordinal 로 명명할 경우 추후 값이 추가될 경우 기존 데이터와 꼬일 수 있음.
 * ex)  ONLINE: 0, OFFLINE: 1로 할때 TEST를 0으로 추가하고 한 칸씩 밀때, 기존 데이터가 망가지게 됨.
 * ENUM으로 관리하면 문자열로 저장하기 떄문에 조금의 저장공간은 필요하겠지만 가독성, 디버깅, 순서 변경에 있어 장점이 있음.
 */
public enum GameMode {
  ONLINE,
  OFFLINE
}
