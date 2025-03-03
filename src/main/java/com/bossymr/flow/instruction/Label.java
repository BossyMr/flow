package com.bossymr.flow.instruction;

/**
 * A label represents a point in the program.
 */
public final class Label implements PseudoInstruction {
    @Override
    public String toString() {
        return "label";
    }
}
