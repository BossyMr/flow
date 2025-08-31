package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Store the value at the top of the stack to the specified variable.
 */
public final class StoreInstruction implements LinearInstruction {

    private final int variable;

    public StoreInstruction(int variable) {
        this.variable = variable;
    }

    @Override
    public void perform(Flow.Method method, FlowSnapshot snapshot) {
        snapshot.store(variable, snapshot.pop());
    }

    @Override
    public String toString() {
        return "store(" + variable + ")";
    }

}
