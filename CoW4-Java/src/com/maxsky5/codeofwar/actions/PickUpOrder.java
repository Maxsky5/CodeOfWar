package com.maxsky5.codeofwar.actions;

/**
 * Created by Arsenik on 19/08/15.
 */
public class PickUpOrder extends Order {

    public PickUpOrder() {
        type = OrderType.PICK_UP_ITEM_ORDER.getLabel();
    }
}
