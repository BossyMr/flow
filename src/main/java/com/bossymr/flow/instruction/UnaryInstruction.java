package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.UnaryExpression;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.Objects;

/**
 * Pops a value off the stack, performs the provided operator on the value and pushes the result back onto the stack.
 */
public final class UnaryInstruction implements LinearInstruction {

    private final UnaryOperator operator;

    public UnaryInstruction(UnaryOperator operator) {
        this.operator = operator;
    }

    @Override
    public void perform(Flow.Method method, FlowSnapshot snapshot) {
        Expression expression = snapshot.pop();
        snapshot.push(new UnaryExpression(operator, expression));
    }

    public UnaryOperator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return "unary(" + operator + ")";
    }

}
