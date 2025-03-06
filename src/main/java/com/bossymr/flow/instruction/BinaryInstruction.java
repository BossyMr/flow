package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.state.FlowEngine;
import com.bossymr.flow.state.FlowMethod;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.List;

/**
 * Pops two values off the stack, performs the provided operator on the values and pushes the result back onto the
 * stack.
 */
public final class BinaryInstruction implements Instruction {

    private final BinaryOperator operator;

    public BinaryInstruction(BinaryOperator operator) {
        this.operator = operator;
    }

    public BinaryOperator getOperator() {
        return operator;
    }

    @Override
    public List<FlowSnapshot> call(FlowEngine engine, FlowMethod method, FlowSnapshot snapshot) {
        Expression right = snapshot.pop();
        Expression left = snapshot.pop();
        snapshot.push(new BinaryExpression(operator, left, right));
        FlowSnapshot successor = snapshot.successorState(method);
        return List.of(successor);
    }
}
