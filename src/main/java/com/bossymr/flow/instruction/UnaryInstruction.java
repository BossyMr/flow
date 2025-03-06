package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.UnaryExpression;
import com.bossymr.flow.state.FlowEngine;
import com.bossymr.flow.state.FlowMethod;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.List;

/**
 * Pops a value off the stack, performs the provided operator on the value and pushes the result back onto the
 * stack.
 */
public final class UnaryInstruction implements Instruction {

    private final UnaryOperator operator;

    public UnaryInstruction(UnaryOperator operator) {
        this.operator = operator;
    }

    public UnaryOperator getOperator() {
        return operator;
    }

    @Override
    public List<FlowSnapshot> call(FlowEngine engine, FlowMethod method, FlowSnapshot snapshot) {
        Expression expression = snapshot.pop();
        snapshot.push(new UnaryExpression(operator, expression));
        FlowSnapshot successor = snapshot.successorState(method);
        return List.of(successor);
    }
}
