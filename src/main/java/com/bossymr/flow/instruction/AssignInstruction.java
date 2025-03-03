package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.Variable;
import com.bossymr.flow.state.FlowEngine;
import com.bossymr.flow.state.FlowMethod;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.List;

/**
 * Assign the variable at the top of the stack to the specified variable.
 */
public final class AssignInstruction implements Instruction {

    private final Variable variable;

    public AssignInstruction(Variable variable) {
        this.variable = variable;
    }

    @Override
    public List<FlowSnapshot> call(FlowEngine engine, FlowMethod method, FlowSnapshot snapshot) {
        Expression value = snapshot.pop();
        snapshot.require(new BinaryExpression(BinaryExpression.Operator.EQUAL_TO, variable, value));
        FlowSnapshot successor = snapshot.successorState(method);
        return List.of(successor);
    }

    public Variable getVariable() {
        return variable;
    }

    @Override
    public String toString() {
        return "assign";
    }
}
