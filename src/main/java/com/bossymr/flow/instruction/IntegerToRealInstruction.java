package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.UnaryExpression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Pops a value off the stack, converts it into a real value and pushes it to the stack.
 * <p>
 * {@code [integer] -> [real]}
 */
public final class IntegerToRealInstruction implements LinearInstruction {
    @Override
    public Expression getExpression(FlowSnapshot predecessor) {
        return new UnaryExpression(UnaryExpression.Operator.INTEGER_TO_REAL, predecessor.pop());
    }

    @Override
    public String toString() {
        return "integerToReal";
    }
}
