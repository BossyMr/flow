package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Pops two values off the stack, checks if the first value is smaller than the second and pushes it to the stack.
 * <p>
 * {@code [integer] [integer] -> [integer]}
 * <p>
 * {@code [real] [real] -> [real]}
 */
public final class LessThanInstruction implements LinearInstruction {
    @Override
    public Expression getExpression(FlowSnapshot predecessor) {
        Expression right = predecessor.pop();
        Expression left = predecessor.pop();
        return new BinaryExpression(BinaryExpression.Operator.LESS_THAN, left, right);
    }

    @Override
    public String toString() {
        return "lessThan";
    }
}
