package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.List;

/**
 * An instruction.
 */
public sealed interface Instruction permits LinearInstruction, CallInstruction, BranchInstruction, ReturnInstruction, PseudoInstruction {

    /**
     * Calls this instruction with the current memory state. This instruction should modify the provided snapshot,
     * then create a new snapshot belonging to the next instruction and return it.
     *
     * @param method the current method.
     * @param snapshot the current program state.
     * @param successor the next instruction.
     * @return a list of possible program states after this instruction.
     */
    List<FlowSnapshot> call(Flow.Method method, FlowSnapshot snapshot, Instruction successor);
}
