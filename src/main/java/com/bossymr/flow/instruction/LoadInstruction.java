package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Load the value from the specified variable to the top of the stack.
 */
public final class LoadInstruction implements LinearInstruction {

    private final int variable;

    public LoadInstruction(int variable) {
        this.variable = variable;
    }

    @Override
    public void perform(Flow.Method method, FlowSnapshot snapshot) {
        snapshot.push(snapshot.load(variable));
    }

    @Override
    public String toString() {
        return "load(" + variable + ")";
    }

}
