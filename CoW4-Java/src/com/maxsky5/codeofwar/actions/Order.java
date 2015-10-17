package com.maxsky5.codeofwar.actions;

/**
 * Created by Arsenik on 19/08/15.
 */
public abstract class Order {
    protected String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
