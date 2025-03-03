package com.bossymr.flow.instruction;

import com.bossymr.flow.state.FlowEngine;
import com.bossymr.flow.state.FlowMethod;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.List;

/**
 * A label represents a point in the program.
 */
public final class Label implements Instruction {
    @Override
    public List<FlowSnapshot> call(FlowEngine engine, FlowMethod method, FlowSnapshot predecessor) {
        FlowSnapshot snapshot = predecessor.successorState(this);
        FlowSnapshot successor = snapshot.successorState(method);
        return List.of(successor);
    }

    @Override
    public String toString() {
        return "label";
    }
}
