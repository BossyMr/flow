package com.bossymr.flow.instruction;

import com.bossymr.flow.Constant;
import com.bossymr.flow.Flow;
import com.bossymr.flow.expression.LiteralExpression;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.Objects;

/**
 * Pushes a value onto the stack.
 */
public final class PushInstruction implements LinearInstruction {

    private final Constant<?> constant;

    public PushInstruction(Constant<?> constant) {
        this.constant = constant;
    }

    @Override
    public void perform(Flow.Method method, FlowSnapshot snapshot) {
        snapshot.push(new LiteralExpression(constant));

    }

    public Constant<?> getConstant() {
        return constant;
    }

    @Override
    public String toString() {
        return "push(" + constant + ")";
    }

}
