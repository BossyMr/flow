package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Pops two values off the stack, performs xor the values and pushes it to the stack.
 * <p>
 * {@code [boolean] [boolean] -> [boolean]}
 */
public final class XorInstruction implements LinearInstruction {
    @Override
    public Expression getExpression(FlowSnapshot predecessor) {
        return new BinaryExpression(BinaryExpression.Operator.XOR, predecessor.pop(), predecessor.pop());
    }

    @Override
    public String toString() {
        return "xor";
    }
}
