package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.List;

/**
 * Duplicates the value at the top of the stack.
 * <p>
 * {@code [any] -> [any] [any]}
 */
public final class DuplicateInstruction implements LinearInstruction {

    @Override
    public void perform(Flow.Method method, FlowSnapshot snapshot) {
        snapshot.push(snapshot.getStack().getLast());
    }

    @Override
    public String toString() {
        return "duplicate";
    }

}
