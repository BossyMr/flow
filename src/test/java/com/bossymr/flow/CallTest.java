package com.bossymr.flow;

import com.bossymr.flow.type.ValueType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CallTest {

    @Test
    void functionCallWithNoArguments() {
        Flow flow = new Flow();
        Flow.Method method = flow.createMethod("foo", new Signature(ValueType.integerType()), codeBuilder -> codeBuilder
                .pushInteger(1)
                .returnValue());
        flow.createMethod("bar", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .call(method)
                .pushInteger(1)
                .equalTo()
                .debugAssert()
                .returnValue());
    }

    @Test
    void functionCallWithArguments() {
        Flow flow = new Flow();
        Flow.Method method = flow.createMethod("foo", new Signature(ValueType.integerType(), ValueType.integerType()), codeBuilder -> codeBuilder
                .load(0)
                .pushInteger(1)
                .add()
                .returnValue());
        flow.createMethod("bar", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushInteger(1)
                .call(method)
                .pushInteger(2)
                .equalTo()
                .debugAssert()
                .returnValue());
    }

    @Test
    void functionCallWithArgumentsWrongResult() {
        Flow flow = new Flow();
        Flow.Method method = flow.createMethod("foo", new Signature(ValueType.integerType(), ValueType.integerType()), codeBuilder -> codeBuilder
                .load(0)
                .pushInteger(1)
                .add()
                .returnValue());
        Assertions.assertThrows(AssertionError.class, () -> flow.createMethod("bar", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushInteger(1)
                .call(method)
                .pushInteger(3)
                .equalTo()
                .debugAssert()
                .returnValue()));
    }

}
