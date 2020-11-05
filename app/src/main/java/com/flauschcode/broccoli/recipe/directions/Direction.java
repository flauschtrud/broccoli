package com.flauschcode.broccoli.recipe.directions;

import java.util.Objects;

public class Direction {

    private int position;
    private String text;

    Direction(int position, String text) {
        this.position = position;
        this.text = text;
    }

    public int getPosition() {
        return position;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Direction that = (Direction) o;
        return position == that.position &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, text);
    }

    @Override
    public String toString() {
        return "Direction{" +
                "position=" + position +
                ", text='" + text + '\'' +
                '}';
    }
}
