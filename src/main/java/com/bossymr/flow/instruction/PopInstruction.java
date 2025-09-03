package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
import com.bossymr.flow.state.FlowSnapshot;

public final class PopInstruction implements LinearInstruction {

    @Override
    public void perform(Flow.Method method, FlowSnapshot snapshot) {
        snapshot.pop();
    }

    @Override
    public String toString() {
        return "pop";
    }
}
