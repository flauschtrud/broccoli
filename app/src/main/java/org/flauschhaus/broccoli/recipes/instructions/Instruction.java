package org.flauschhaus.broccoli.recipes.instructions;

import java.util.Objects;

public class Instruction {

    private String prefix;
    private String text;

    Instruction(String prefix, String text) {
        this.prefix = prefix;
        this.text = text;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instruction that = (Instruction) o;
        return Objects.equals(prefix, that.prefix) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, text);
    }
}
