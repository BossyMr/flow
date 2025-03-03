package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.LiteralExpression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Pushes the specified boolean to the stack.
 * <p>
 * {@code -> [boolean]}
 */
public final class ConstantBooleanInstruction implements LinearInstruction {

    private final boolean value;

    public ConstantBooleanInstruction(boolean value) {
        this.value = value;
    }

    @Override
    public Expression getExpression(FlowSnapshot predecessor) {
        return LiteralExpression.booleanLiteral(value);
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "pushBoolean " + value;
    }
}
