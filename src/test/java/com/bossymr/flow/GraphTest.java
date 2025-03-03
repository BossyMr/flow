package com.bossymr.flow;

import com.bossymr.flow.constraint.Constraint;
import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.LiteralExpression;
import com.bossymr.flow.expression.Variable;
import com.bossymr.flow.instruction.Label;
import com.bossymr.flow.state.*;
import com.bossymr.flow.type.ValueType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class GraphTest {

    @Test
    void functionCall() throws IOException, InterruptedException {
        Method abs = new Method("abs", ValueType.integerType(), List.of(ValueType.integerType()), codeBuilder -> codeBuilder
                .duplicate()
                .pushInteger(0)
                .greaterThan()
                .ifThenElse(thenHandler -> thenHandler
                                .returnValue(),
                        elseHandler -> elseHandler
                                .negate()
                                .returnValue()));
        Variable result = new Variable("result", ValueType.booleanType());
        Label label = new Label();
        Method foo = new Method("foo", ValueType.emptyType(), List.of(), codeBuilder -> codeBuilder
                .pushInteger(-1)
                .call(abs)
                .pushInteger(1)
                .equalTo()
                .assign(result)
                .insertLabel(label)
                .returnValue());
        FlowEngine engine = new FlowEngine();
        FlowMethod method = engine.getMethod(foo);
        List<FlowSnapshot> snapshots = method.afterElement(label);
        Assertions.assertEquals(1, snapshots.size());
        FlowSnapshot snapshot = snapshots.getFirst();
        Constraint constraint = snapshot.compute(new BinaryExpression(BinaryExpression.Operator.EQUAL_TO, result, LiteralExpression.booleanLiteral(true)));
        Assertions.assertEquals(Constraint.ALWAYS_TRUE, constraint);
    }
}
