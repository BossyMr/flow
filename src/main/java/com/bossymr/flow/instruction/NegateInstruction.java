package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.UnaryExpression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Pops a value off the stack, negates the value and pushes it to the stack.
 * <p>
 * {@code [integer] -> [integer]}
 * <p>
 * {@code [real] -> [real]}
 */
public final class NegateInstruction implements LinearInstruction {
    @Override
    public Expression getExpression(FlowSnapshot predecessor) {
        return new UnaryExpression(UnaryExpression.Operator.NEGATE, predecessor.pop());
    }

    @Override
    public String toString() {
        return "negate";
    }
}
