package com.game.bigwheel.domain.bigwheel;


public enum BetZone{
  SILVER(24, 2),
  GOLD(15,3),
  EMERALD(7,6),
  DIAMOND(4, 11),
  CRYSTAL(2, 21),
  JOKER(1, 41),
  MEGA(1,41);

  private final int count; // 휠상 개수
  private final int payout; // 배당률

  BetZone(int count, int payout){
    this.count = count;
    this.payout = payout;
  }

  public int getCount(){
    return count;
  }

  public int getPayout(){
    return payout;
  }
}