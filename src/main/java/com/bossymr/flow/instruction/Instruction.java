package com.bossymr.flow.instruction;

import com.bossymr.flow.state.FlowEngine;
import com.bossymr.flow.state.FlowMethod;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.List;

/**
 * An {@code Instruction} represents an instruction.
 */
public sealed interface Instruction permits AssignInstruction, CallInstruction, ConditionalJumpInstruction, DuplicateInstruction, JumpInstruction, Label, LinearInstruction, ReturnInstruction {

    /**
     * Calls this instruction with the current memory state.
     *
     * @param engine the engine used to compute the data flow.
     * @param method the current method.
     * @param predecessor the current memory state.
     * @return a list of possible memory states after this instruction.
     */
    List<FlowSnapshot> call(FlowEngine engine, FlowMethod method, FlowSnapshot predecessor);

}
