package com.maxsky5.codeofwar.socket;

import com.maxsky5.codeofwar.actions.Order;

import java.util.List;

/**
 * Created by Arsenik on 19/08/15.
 */
public class Response {
    private String type = "turnResult";
    private List<Order> actions;

    public String getType() {
        return type;
    }

    public List<Order> getActions() {
        return actions;
    }

    public void setActions(List<Order> actions) {
        this.actions = actions;
    }
}
