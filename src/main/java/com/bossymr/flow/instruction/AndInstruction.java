package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Pops two values off the stack, performs and the values and pushes it to the stack.
 * <p>
 * {@code [boolean] [boolean] -> [boolean]}
 */
public final class AndInstruction implements LinearInstruction {
    @Override
    public Expression getExpression(FlowSnapshot snapshot) {
        return new BinaryExpression(BinaryExpression.Operator.AND, snapshot.pop(), snapshot.pop());
    }

    @Override
    public String toString() {
        return "and";
    }
}
