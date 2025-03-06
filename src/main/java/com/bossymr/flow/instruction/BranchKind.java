package com.bossymr.flow.instruction;

/**
 * A {@code BranchKind} specifies when a {@link BranchInstruction} will branch.
 */
public enum BranchKind {
    /**
     * Unconditionally jump to the provided instruction.
     */
    ALWAYS,

    /**
     * Pop a value off the stack and jump if the value is {@code true}.
     */
    CONDITIONALLY,
}
