package fr.ttfx.cow4.world;

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

    public Cell() {

    }

    public Cell(Long id, boolean left, boolean right, boolean top, boolean bottom, int line, int column) {
        this.id = id;
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.line = line;
        this.column = column;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

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

    public int getColumn() { return column; }

    public void setColumn(int column) { this.column = column; }

}
