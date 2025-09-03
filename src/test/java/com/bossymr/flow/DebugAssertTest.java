package com.bossymr.flow;

import com.bossymr.flow.constraint.Constraint;
import com.bossymr.flow.type.ValueType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DebugAssertTest {

    @Test
    void trueAssertAlwaysTrue() {
        Flow flow = new Flow();
        flow.createMethod("foo", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushBoolean(true)
                .debugAssert());
        flow.createMethod("foo", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushBoolean(true)
                .debugAssert(Constraint.ALWAYS_TRUE));
    }

    @Test
    void falseAssertAlwaysFalse() {
        Flow flow = new Flow();
        flow.createMethod("foo", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushBoolean(false)
                .debugAssert(Constraint.ALWAYS_FALSE));
    }

    @Test
    void falseAssertAlwaysFalseThrows() {
        Flow flow = new Flow();
        Assertions.assertThrows(AssertionError.class, () -> flow.createMethod("foo", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushBoolean(false)
                .debugAssert()));
    }

    @Test
    void falseAssertAlwaysTrueThrows() {
        Flow flow = new Flow();
        Assertions.assertThrows(AssertionError.class, () -> flow.createMethod("foo", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushBoolean(true)
                .debugAssert(Constraint.ALWAYS_FALSE)));
    }
}
