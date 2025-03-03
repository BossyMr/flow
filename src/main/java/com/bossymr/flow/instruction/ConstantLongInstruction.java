package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.LiteralExpression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Pushes the specified long to the stack.
 * <p>
 * {@code -> [long]}
 */
public final class ConstantLongInstruction implements LinearInstruction {

    private final long value;

    public ConstantLongInstruction(long value) {
        this.value = value;
    }

    @Override
    public Expression getExpression(FlowSnapshot predecessor) {
        return LiteralExpression.integerLiteral(value);
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "pushLong " + value;
    }
}
