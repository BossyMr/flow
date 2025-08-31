package com.bossymr.flow;

import com.bossymr.flow.constraint.Constraint;
import com.bossymr.flow.instruction.Label;
import com.bossymr.flow.state.FlowGraph;
import com.bossymr.flow.type.ValueType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoopTest {

    @DisplayName("Increment a counter by 1 for 5 iterations")
    @Test
    void finiteForLoop() {
        Flow flow = new Flow();
        Label label = new Label();
        int iterator = 0;
        int counter = 1;
        int iterations = 25;
        Flow.Method method = flow.createMethod("foo", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushInteger(0)
                .store(iterator)
                .pushInteger(0)
                .store(counter)
                .insertLabel(label)
                .load(iterator)
                .duplicate()
                .store(iterator)
                .pushInteger(iterations)
                .lessThan()
                .ifThen(thenBuilder -> thenBuilder
                        .load(iterator)
                        .pushInteger(1)
                        .add()
                        .store(iterator)
                        .load(counter)
                        .pushInteger(1)
                        .add()
                        .store(counter)
                        .jump(label))
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
        String text = new FlowGraph()
                .withMethod(method)
                .getText();
        System.out.println(text);
    }

}
