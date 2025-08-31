package com.bossymr.flow;

import com.bossymr.flow.type.ValueType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BinaryInstructionTest {

    @DisplayName("Assert 1 + 1 == 2")
    @Test
    void addInteger() {
        Flow flow = new Flow();
        flow.createMethod("foo", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushInteger(1)
                .pushInteger(1)
                .add()
                .pushInteger(2)
                .equalTo()
                .debugAssert()
                .returnValue());
        System.out.println(flow.getStatistics());
    }

}
