package com.bossymr.flow;

import com.bossymr.flow.constraint.Constraint;
import com.bossymr.flow.instruction.Label;
import com.bossymr.flow.type.ValueType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BranchTest {

    @DisplayName("Assert condition is true/false in then/else clause")
    @Test
    void conditionInIfStatement() {
        Flow flow = new Flow();
        flow.createMethod("foo", new Signature(ValueType.emptyType(), ValueType.booleanType()), codeBuilder -> codeBuilder
                .load(0)
                .duplicate()
                .ifThenElse(thenBuilder -> thenBuilder.debugAssert(),
                        elseBuilder -> elseBuilder.not().debugAssert())
                .returnValue());
        System.out.println(flow.getStatistics());
    }

    @DisplayName("Assert then clause is not evaluated if condition is always false")
    @Test
    void unreachableThenClauseSkipped() {
        Flow flow = new Flow();
        flow.createMethod("foo", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushBoolean(false)
                // This doesn't throw an exception because the code is unreachable, so it's never evaluated.
                .ifThen(thenBuilder -> thenBuilder.pushBoolean(false).debugAssert())
                .returnValue());
        System.out.println(flow.getStatistics());
    }

    @DisplayName("Assert else clause is not evaluated if condition is always true")
    @Test
    void unreachableElseClauseSkipped() {
        Flow flow = new Flow();
        flow.createMethod("foo", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushBoolean(true)
                .ifThenElse(thenBuilder -> {},
                        // This doesn't throw an exception because the code is unreachable, so it's never evaluated.
                        elseBuilder -> elseBuilder.pushBoolean(false).debugAssert())
                .returnValue());
        System.out.println(flow.getStatistics());
    }

    @DisplayName("Assert both paths in an if-statement are merged")
    @Test
    void mergeDiffAfterIfStatement() {
        Flow flow = new Flow();
        flow.createMethod("foo", new Signature(ValueType.emptyType(), ValueType.booleanType()), codeBuilder -> codeBuilder
                .load(0)
                .ifThenElse(thenBuilder -> thenBuilder.pushBoolean(true),
                        elseBuilder -> elseBuilder.pushBoolean(false))
                .debugAssert(Constraint.ANY_VALUE)
                .returnValue());
        System.out.println(flow.getStatistics());
    }

    @DisplayName("Assert only reachable paths in an if-statement are merged")
    @Test
    void mergeEqualAfterIfStatement() {
        Flow flow = new Flow();
        flow.createMethod("foo", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushBoolean(true)
                .ifThenElse(thenBuilder -> thenBuilder.pushBoolean(true),
                        elseBuilder -> elseBuilder.pushBoolean(false))
                .debugAssert(Constraint.ALWAYS_TRUE)
                .returnValue());
        System.out.println(flow.getStatistics());
    }

    @DisplayName("Assert statement after a jump is not reachable")
    @Test
    void unreachableStatementAfterJump() {
        Flow flow = new Flow();
        Label label = new Label();
        Label compare = new Label();
        flow.createMethod("foo", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .jump(label)
                .pushBoolean(false)
                .store(0)
                .jump(compare)
                .insertLabel(label)
                .pushBoolean(true)
                .store(0)
                .insertLabel(compare)
                .load(0)
                .debugAssert(Constraint.ALWAYS_TRUE)
                .returnValue());
        System.out.println(flow.getStatistics());
    }
}
