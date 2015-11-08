package com.maxsky5.codeofwar.world;

/**
 * Created by TheArsenik on 16/08/15.
 */

/**
 * A Cell
 */
public class Cell {

    private Long id;
    private int line;
    private int column;
    private boolean left;
    private boolean right;
    private boolean top;
    private boolean bottom;
    private Item item;

    public Cell() {

    }

    public Cell(Long id, boolean left, boolean right, boolean top, boolean bottom, int line, int column, Item item) {
        this.id = id;
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.line = line;
        this.column = column;
        this.item = item;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean canLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean canRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean canTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

    public boolean canBottom() {
        return bottom;
    }

    public void setBottom(boolean bottom) {
        this.bottom = bottom;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Boolean isIntersection() {
        Integer nbWay = 0;
        if (left) {
            nbWay++;
        }
        if (right) {
            nbWay++;
        }
        if (top) {
            nbWay++;
        }
        if (bottom) {
            nbWay++;
        }

        return nbWay > 2;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "id=" + id +
                ", line=" + line +
                ", column=" + column +
                ", left=" + left +
                ", right=" + right +
                ", top=" + top +
                ", bottom=" + bottom +
                ", item=" + item +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Cell cell = (Cell) o;

        if (line != cell.line) {
            return false;
        }
        if (column != cell.column) {
            return false;
        }
        return !(id != null ? !id.equals(cell.id) : cell.id != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + line;
        result = 31 * result + column;
        return result;
    }
}
