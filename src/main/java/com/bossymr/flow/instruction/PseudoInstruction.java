package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.List;

/**
 * A {@code PseudoInstruction} stores metadata in the program and does not represent any instruction. As a result, a
 * {@code PseudoInstruction} will not be found in any snapshot.
 */
public sealed interface PseudoInstruction extends Instruction permits Label {
    @Override
    default List<FlowSnapshot> call(Flow.Method method, FlowSnapshot snapshot, Instruction successor) {
        FlowSnapshot successorState = snapshot.successorState(successor);
        return List.of(successorState);
    }
}
