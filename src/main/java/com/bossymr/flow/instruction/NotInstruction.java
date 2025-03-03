package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.UnaryExpression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Pops a value off the stack, performs not on the value and pushes it to the stack.
 * <p>
 * {@code [boolean] -> [boolean]}
 */
public final class NotInstruction implements LinearInstruction {
    @Override
    public Expression getExpression(FlowSnapshot predecessor) {
        return new UnaryExpression(UnaryExpression.Operator.NOT, predecessor.pop());
    }

    @Override
    public String toString() {
        return "not";
    }
}
