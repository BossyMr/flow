package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.LiteralExpression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Pushes the specified integer to the stack.
 * <p>
 * {@code -> [integer]}
 */
public final class ConstantIntegerInstruction implements LinearInstruction {

    private final int value;

    public ConstantIntegerInstruction(int value) {
        this.value = value;
    }

    @Override
    public Expression getExpression(FlowSnapshot snapshot) {
        return LiteralExpression.integerLiteral(value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "pushInteger " + value;
    }
}
