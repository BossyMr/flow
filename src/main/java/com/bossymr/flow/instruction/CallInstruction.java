package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.List;

/**
 * Calls a method specified by the current method at the index specified by the next integer. Depending on the method,
 * all arguments are popped off the stack in the order they are defined. The return value of the method is pushed onto
 * the stack.
 * <p>
 * {@code [argument 1] [argument 2] [...] -> [return type]}
 */
public final class CallInstruction implements Instruction {

    private final Flow.Method method;

    public CallInstruction(Flow.Method method) {
        this.method = method;
    }

    @Override
    public List<FlowSnapshot> call(Flow.Method method, FlowSnapshot snapshot, Instruction successor) {
        return getMethod().getExitPoints(snapshot).stream()
                .map(exitPoint -> exitPoint.successorState(successor))
                .toList();
    }

    public Flow.Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "call(" + method + ")";
    }

}
