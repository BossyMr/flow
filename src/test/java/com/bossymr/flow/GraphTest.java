package com.bossymr.flow;

import com.bossymr.flow.state.FlowGraph;
import com.bossymr.flow.type.ValueType;
import org.junit.jupiter.api.Test;

class GraphTest {

    @Test
    void functionCall() {
        Flow flow = new Flow();
        Flow.Method abs = flow.createMethod("abs", new Signature(ValueType.integerType(), ValueType.integerType()), codeBuilder -> codeBuilder
                .duplicate()
                .pushInteger(0)
                .greaterThan()
                .ifThenElse(thenHandler -> thenHandler
                                .returnValue(),
                        elseHandler -> elseHandler
                                .negate()
                                .returnValue()));
        Flow.Method foo = flow.createMethod("foo", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushInteger(-1)
                .call(abs)
                .pushInteger(1)
                .equalTo()
                .returnValue());
        String text = new FlowGraph()
                .withMethod(foo)
                .getText();
        System.out.println(flow.getStatistics());
        System.out.println(text);
    }
}
