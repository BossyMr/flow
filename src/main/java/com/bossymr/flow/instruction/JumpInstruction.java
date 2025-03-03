package com.bossymr.flow.instruction;

import com.bossymr.flow.state.FlowEngine;
import com.bossymr.flow.state.FlowMethod;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.List;

/**
 * Jumps to the instruction at the index specified by the next integer.
 */
public final class JumpInstruction implements Instruction {

    private final Label instruction;

    public JumpInstruction(Label instruction) {
        this.instruction = instruction;
    }

    @Override
    public List<FlowSnapshot> call(FlowEngine engine, FlowMethod method, FlowSnapshot predecessor) {
        // This might seem wasteful, but we currently create a new snapshot for each instruction. In order to be
        // able to find a snapshot for each instruction, in the future, this should be reworked.
        FlowSnapshot snapshot = predecessor.successorState(this);
        FlowSnapshot successor = snapshot.successorState(instruction);
        return List.of(successor);
    }

    public Label getInstruction() {
        return instruction;
    }

    @Override
    public String toString() {
        return "jump";
    }
}
