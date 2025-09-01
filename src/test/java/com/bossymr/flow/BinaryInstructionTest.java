package com.bossymr.flow;

import com.bossymr.flow.state.FlowGraph;
import com.bossymr.flow.type.ValueType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BinaryInstructionTest {

    @DisplayName("Assert 1 + 1 == 2")
    @Test
    void addInteger() {
        Flow flow = new Flow();
        Flow.Method method = flow.createMethod("foo", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushInteger(1)
                .pushInteger(1)
                .add()
                .pushInteger(2)
                .equalTo()
                .debugAssert()
                .returnValue());
        System.out.println(flow.getStatistics());
        System.out.println(new FlowGraph().withMethod(method).getText());
    }

}
