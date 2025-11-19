package com.game.bigwheel.coin;

public enum Coin {
  CHIP_1000(1000), // 1,000원
  CHIP_5000(5000), // 5,000원
  CHIP_10000(10000), // 10,000원
  CHIP_100000(100000), // 100,000원
  CHIP_1000000(1000000); // 1,000,000원

  private final int value;

  Coin(int value){
    this.value = value;
  }

  public int getValue(){
    return value;
  }
}
