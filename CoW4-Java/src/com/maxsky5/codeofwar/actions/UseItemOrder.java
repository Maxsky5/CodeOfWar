package com.maxsky5.codeofwar.actions;

import com.maxsky5.codeofwar.world.Item;

public class UseItemOrder extends Order {
    private Item item;

    public UseItemOrder (Item item) {
        type = OrderType.USE_ITEM_ORDER.getLabel();
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
