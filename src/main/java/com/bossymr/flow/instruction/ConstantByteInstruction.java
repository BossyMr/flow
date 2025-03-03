package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.LiteralExpression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Pushes the specified byte to the stack.
 * <p>
 * {@code -> [byte]}
 */
public final class ConstantByteInstruction implements LinearInstruction {

    private final byte value;

    public ConstantByteInstruction(byte value) {
        this.value = value;
    }

    @Override
    public Expression getExpression(FlowSnapshot predecessor) {
        return LiteralExpression.integerLiteral(value);
    }

    public byte getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "pushByte " + value;
    }
}
