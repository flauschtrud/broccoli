package org.flauschhaus.broccoli.recipes.instructions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;

@RunWith(JUnit4.class)
public class InstructionBuilderTest {

    private List<Instruction> instructions;

    @Test
    public void empty_string_should_yield_empty_list() {
        instructions = InstructionBuilder.from("");
        assertThat(instructions, is(empty()));
    }

    @Test
    public void null_should_yield_empty_list() {
        instructions = InstructionBuilder.from(null);
        assertThat(instructions, is(empty()));
    }

    @Test
    public void single_paragraph_should_yield_one_instruction() {
        instructions = InstructionBuilder.from("Erstmal den Teig machen.");
        assertThat(instructions, hasSize(1));
        assertThat(instructions, hasItem(new Instruction(1, "Erstmal den Teig machen.")));
    }

    @Test
    public void instructions_should_be_split_by_new_lines() {
        instructions = InstructionBuilder.from("Erstmal den Teig machen.\nLauch schneiden und kochen.\nAb in den Backofen!");
        assertThat(instructions, hasSize(3));
        assertThat(instructions, hasItem(new Instruction(1, "Erstmal den Teig machen.")));
        assertThat(instructions, hasItem(new Instruction(2, "Lauch schneiden und kochen.")));
        assertThat(instructions, hasItem(new Instruction(3, "Ab in den Backofen!")));
    }

    @Test
    public void empty_lines_and_tabs_should_be_skipped() {
        instructions = InstructionBuilder.from("   Erstmal den Teig machen.\n \n Lauch schneiden und kochen.\n \tAb in den Backofen!");
        assertThat(instructions, hasSize(3));
        assertThat(instructions, hasItem(new Instruction(1, "Erstmal den Teig machen.")));
        assertThat(instructions, hasItem(new Instruction(2, "Lauch schneiden und kochen.")));
        assertThat(instructions, hasItem(new Instruction(3, "Ab in den Backofen!")));
    }

    @Test
    public void preceding_numerations_should_be_omitted() {
        instructions = InstructionBuilder.from("1. Erstmal den Teig machen.\n 2. Lauch schneiden und kochen.\n 3. Ab in den 2. Backofen!");
        assertThat(instructions, hasSize(3));
        assertThat(instructions, hasItem(new Instruction(1, "Erstmal den Teig machen.")));
        assertThat(instructions, hasItem(new Instruction(2, "Lauch schneiden und kochen.")));
        assertThat(instructions, hasItem(new Instruction(3, "Ab in den 2. Backofen!")));
    }

}