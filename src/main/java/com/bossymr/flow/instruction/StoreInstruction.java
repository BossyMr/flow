package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.Variable;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.Objects;

/**
 * Store the value at the top of the stack to the specified variable.
 */
public final class StoreInstruction implements LinearInstruction {

    private final Variable variable;

    public StoreInstruction(Variable variable) {
        this.variable = variable;
    }

    @Override
    public void perform(Flow.Method method, FlowSnapshot snapshot) {
        Expression value = snapshot.pop();
        snapshot.require(new BinaryExpression(BinaryOperator.EQUAL_TO, variable, value));
    }

    public Variable getVariable() {
        return variable;
    }

    @Override
    public String toString() {
        return "store(" + variable + ")";
    }

}
