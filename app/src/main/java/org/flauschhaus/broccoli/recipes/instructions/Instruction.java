package org.flauschhaus.broccoli.recipes.instructions;

import java.util.Objects;

public class Instruction {

    private int position;
    private String text;

    Instruction(int position, String text) {
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
        Instruction that = (Instruction) o;
        return position == that.position &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, text);
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "position=" + position +
                ", text='" + text + '\'' +
                '}';
    }
}
