package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Pops two values off the stack, performs or on the values and pushes it to the stack.
 * <p>
 * {@code [boolean] [boolean] -> [boolean]}
 */
public final class OrInstruction implements LinearInstruction {
    @Override
    public Expression getExpression(FlowSnapshot predecessor) {
        return new BinaryExpression(BinaryExpression.Operator.OR, predecessor.pop(), predecessor.pop());
    }

    @Override
    public String toString() {
        return "or";
    }
}
