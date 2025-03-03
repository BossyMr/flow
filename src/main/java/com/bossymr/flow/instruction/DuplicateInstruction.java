package com.bossymr.flow.instruction;

import com.bossymr.flow.state.FlowEngine;
import com.bossymr.flow.state.FlowMethod;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.List;

/**
 * Duplicates the value at the top of the stack.
 * <p>
 * {@code [any] -> [any] [any]}
 */
public final class DuplicateInstruction implements Instruction {

    @Override
    public List<FlowSnapshot> call(FlowEngine engine, FlowMethod method, FlowSnapshot snapshot) {
        snapshot.push(snapshot.getStack().getLast());
        FlowSnapshot successor = snapshot.successorState(method);
        return List.of(successor);
    }

    @Override
    public String toString() {
        return "duplicate";
    }
}
