package com.bossymr.flow;

import com.bossymr.flow.state.FlowEngine;
import com.bossymr.flow.state.FlowGraph;
import com.bossymr.flow.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.List;

class GraphTest {

    @Test
    void functionCall() {
        Method abs = new Method("abs", ValueType.integerType(), List.of(ValueType.integerType()), codeBuilder -> codeBuilder
                .duplicate()
                .pushInteger(0)
                .greaterThan()
                .ifThenElse(thenHandler -> thenHandler
                                .returnValue(),
                        elseHandler -> elseHandler
                                .negate()
                                .returnValue()));
        Method foo = new Method("foo", ValueType.emptyType(), List.of(), codeBuilder -> codeBuilder
                .pushInteger(-1)
                .call(abs)
                .pushInteger(1)
                .equalTo()
                .returnValue());
        FlowEngine engine = new FlowEngine();
        String text = new FlowGraph(engine)
                .withMethod(foo)
                .getText();
        System.out.println(text);
    }
}
