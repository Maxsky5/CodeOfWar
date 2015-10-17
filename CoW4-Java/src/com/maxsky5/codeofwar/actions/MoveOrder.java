package com.maxsky5.codeofwar.actions;

public class MoveOrder extends Order {
    private Long target;

    public MoveOrder(Long targetCellId) {
        this.target = targetCellId;
        type = OrderType.MOVE_ORDER.getLabel();
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MoveOrder moveOrder = (MoveOrder) o;

        return !(target != null ? !target.equals(moveOrder.target) : moveOrder.target != null);

    }

    @Override
    public int hashCode() {
        return target != null ? target.hashCode() : 0;
    }
}
