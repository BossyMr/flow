package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.List;

/**
 * Depending on return type of the current method, pops a value off the stack and returns it from this method. If the
 * current method does not return a value, this method does not modify the stack.
 * <p>
 * {@code [return type] -> }
 */
public final class ReturnInstruction implements Instruction {

    @Override
    public List<FlowSnapshot> call(Flow.Method method, FlowSnapshot snapshot, Instruction successor) {
        method.getExitPoints().add(snapshot);
        return List.of();
    }

    @Override
    public String toString() {
        return "return";
    }

}
