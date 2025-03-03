package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Pops two values off the stack, subtracts the values and pushes the result to the stack.
 * <p>
 * {@code [integer] [integer] -> [integer]}
 * <p>
 * {@code [real] [real] -> [real]}
 */
public final class SubtractInstruction implements LinearInstruction {
    @Override
    public Expression getExpression(FlowSnapshot snapshot) {
        Expression right = snapshot.pop();
        Expression left = snapshot.pop();
        return new BinaryExpression(BinaryExpression.Operator.SUBTRACT, left, right);
    }

    @Override
    public String toString() {
        return "subtract";
    }
}
