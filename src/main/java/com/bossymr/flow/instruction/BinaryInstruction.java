package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.Objects;

/**
 * Pops two values off the stack, performs the provided operator on the values and pushes the result back onto the
 * stack.
 */
public final class BinaryInstruction implements LinearInstruction {

    private final BinaryOperator operator;

    public BinaryInstruction(BinaryOperator operator) {
        this.operator = operator;
    }

    @Override
    public void perform(Flow.Method method, FlowSnapshot snapshot) {
        Expression right = snapshot.pop();
        Expression left = snapshot.pop();
        snapshot.push(new BinaryExpression(operator, left, right));
    }

    public BinaryOperator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return "binary(" + operator + ")";
    }
}
