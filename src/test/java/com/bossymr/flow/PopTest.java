package com.bossymr.flow;

import com.bossymr.flow.type.ValueType;
import org.junit.jupiter.api.Test;

class PopTest {

    @Test
    void popFromStack() {
        Flow flow = new Flow();
        flow.createMethod("foo", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushBoolean(true)
                .pushBoolean(false)
                .pop()
                .debugAssert());
    }

}
