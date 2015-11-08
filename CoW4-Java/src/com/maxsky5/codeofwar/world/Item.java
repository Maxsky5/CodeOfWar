package com.maxsky5.codeofwar.world;

/**
 * Created by Arsenik on 18/08/15.
 */
public class Item {
    private ItemType type;

    public Item(ItemType type) {
        this.type = type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public ItemType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Item item = (Item) o;

        return type == item.type;

    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }
}
