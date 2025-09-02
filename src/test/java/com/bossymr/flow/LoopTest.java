package com.bossymr.flow;

import com.bossymr.flow.constraint.Constraint;
import com.bossymr.flow.type.ValueType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoopTest {

    @DisplayName("Increment a counter by 1 for 100 iterations")
    @Test
    void finiteForLoop() {
        Flow flow = new Flow();
        int iterator = 0;
        int counter = 1;
        int iterations = 100;
        flow.createMethod("foo", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushInteger(0)
                .store(iterator)
                .pushInteger(0)
                .store(counter)
                .loop((breakLabel, loopBuilder) -> loopBuilder
                        .load(iterator)
                        .duplicate()
                        .store(iterator)
                        .pushInteger(iterations)
                        .lessThan()
                        .ifThenElse(thenBuilder -> thenBuilder
                                        .load(iterator)
                                        .pushInteger(1)
                                        .add()
                                        .store(iterator)
                                        .load(counter)
                                        .pushInteger(1)
                                        .add()
                                        .store(counter),
                                elseBuilder -> elseBuilder
                                        .jump(breakLabel)))
                .load(iterator)
                .pushInteger(iterations)
                .equalTo()
                .debugAssert()
                .load(counter)
                .pushInteger(iterations)
                .equalTo()
                .debugAssert(Constraint.ALWAYS_TRUE)
                .returnValue());
        System.out.println(flow.getStatistics());
    }

}
