package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.LiteralExpression;
import com.bossymr.flow.state.FlowEngine;
import com.bossymr.flow.state.FlowMethod;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.List;

/**
 * Pushes the specified string to the stack.
 * <p>
 * {@code -> [string]}
 */
public final class ConstantStringInstruction implements LinearInstruction {

    private final String value;

    public ConstantStringInstruction(String value) {
        this.value = value;
    }

    @Override
    public List<FlowSnapshot> call(FlowEngine engine, FlowMethod method, FlowSnapshot predecessor) {
        FlowSnapshot snapshot = predecessor.successorState(this);
        snapshot.push(LiteralExpression.stringLiteral(value));
        FlowSnapshot successor = snapshot.successorState(method);
        return List.of(successor);

    }

    @Override
    public Expression getExpression(FlowSnapshot predecessor) {
        return null;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "pushString \"" + value + "\"";
    }
}
