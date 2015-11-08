package com.maxsky5.codeofwar.actions;

public class PickUpOrder extends Order {

    public PickUpOrder() {
        type = OrderType.PICK_UP_ITEM_ORDER.getLabel();
    }
}
