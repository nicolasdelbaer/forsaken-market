package be.nicolasdelbaer.forsakenmarket.enums;

public enum MarketItemStatus {
    BOUGHT, //item is active & can be sold
    SOLD, //item is sold, inactive
    DECAYED, //item is decayed, cannot be sold anymore
    DISCARDED //item is ditched after being decayed
}
