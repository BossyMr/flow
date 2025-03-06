package com.bossymr.flow.instruction;

import com.bossymr.flow.Constant;
import com.bossymr.flow.expression.LiteralExpression;
import com.bossymr.flow.state.FlowEngine;
import com.bossymr.flow.state.FlowMethod;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.List;

/**
 * Pushes a value onto the stack.
 */
public final class PushInstruction implements Instruction {

    private final Constant<?> constant;

    public PushInstruction(Constant<?> constant) {
        this.constant = constant;
    }

    public Constant<?> getConstant() {
        return constant;
    }

    @Override
    public List<FlowSnapshot> call(FlowEngine engine, FlowMethod method, FlowSnapshot snapshot) {
        snapshot.push(new LiteralExpression(constant));
        FlowSnapshot successor = snapshot.successorState(method);
        return List.of(successor);
    }
}
