package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
import com.bossymr.flow.expression.Variable;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.Objects;

/**
 * Load the value from the specified variable to the top of the stack.
 */
public final class LoadInstruction implements LinearInstruction {

    private final Variable variable;

    public LoadInstruction(Variable variable) {
        this.variable = variable;
    }

    @Override
    public void perform(Flow.Method method, FlowSnapshot snapshot) {
        snapshot.push(variable);
    }

    public Variable getVariable() {
        return variable;
    }

    @Override
    public String toString() {
        return "load(" + variable + ")";
    }

}
