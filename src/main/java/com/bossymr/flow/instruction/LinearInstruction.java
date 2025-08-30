package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.List;

/**
 * An instruction that will not change the next instruction.
 */
public sealed interface LinearInstruction extends Instruction permits UnaryInstruction, BinaryInstruction, DuplicateInstruction, LoadInstruction, StoreInstruction, PushInstruction {

    /**
     * Performs this instruction on the provided memory state.
     *
     * @param method the current method.
     * @param snapshot the current program state.
     */
    void perform(Flow.Method method, FlowSnapshot snapshot);

    @Override
    default List<FlowSnapshot> call(Flow.Method method, FlowSnapshot snapshot, Instruction successor) {
        perform(method, snapshot);
        FlowSnapshot successorState = snapshot.successorState(successor);
        return List.of(successorState);
    }
}
