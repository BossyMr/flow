package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Pops two values off the stack, checks the values for equality and pushes it to the stack.
 * <p>
 * {@code [any] [any] -> [boolean]}
 */
public final class EqualToInstruction implements LinearInstruction {
    @Override
    public Expression getExpression(FlowSnapshot snapshot) {
        return new BinaryExpression(BinaryExpression.Operator.EQUAL_TO, snapshot.pop(), snapshot.pop());
    }

    @Override
    public String toString() {
        return "equalTo";
    }
}
