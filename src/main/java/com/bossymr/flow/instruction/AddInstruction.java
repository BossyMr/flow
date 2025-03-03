package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Pops two values off the stack, adds the values and pushes the result to the stack.
 * <p>
 * {@code [integer] [integer] -> [integer]}
 * <p>
 * {@code [real] [real] -> [real]}
 * <p>
 * {@code [string] [string] -> [string]}
 */
public final class AddInstruction implements LinearInstruction {
    @Override
    public Expression getExpression(FlowSnapshot snapshot) {
        return new BinaryExpression(BinaryExpression.Operator.ADD, snapshot.pop(), snapshot.pop());
    }

    @Override
    public String toString() {
        return "add";
    }
}
