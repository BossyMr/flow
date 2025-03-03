package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.UnaryExpression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Pops a value off the stack, converts it into an integer value and pushes it to the stack.
 * <p>
 * {@code [real] -> [integer]}
 */
public final class RealToIntegerInstruction implements LinearInstruction {
    @Override
    public Expression getExpression(FlowSnapshot predecessor) {
        return new UnaryExpression(UnaryExpression.Operator.REAL_TO_INTEGER, predecessor.pop());
    }

    @Override
    public String toString() {
        return "realToInteger";
    }
}
