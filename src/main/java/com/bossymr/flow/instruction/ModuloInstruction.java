package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Pops two values off the stack, performs modulo on the values and pushes the result to the stack.
 * <p>
 * {@code [integer] [integer] -> [integer]}
 */
public final class ModuloInstruction implements LinearInstruction {
    @Override
    public Expression getExpression(FlowSnapshot predecessor) {
        return new BinaryExpression(BinaryExpression.Operator.MODULO, predecessor.pop(), predecessor.pop());
    }

    @Override
    public String toString() {
        return "modulo";
    }
}
