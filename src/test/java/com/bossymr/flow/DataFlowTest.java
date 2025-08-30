package com.bossymr.flow;

import com.bossymr.flow.constraint.Constraint;
import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.LiteralExpression;
import com.bossymr.flow.expression.Variable;
import com.bossymr.flow.instruction.BinaryOperator;
import com.bossymr.flow.instruction.Label;
import com.bossymr.flow.state.FlowSnapshot;
import com.bossymr.flow.type.ValueType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class DataFlowTest {

    @Test
    void returnConstant() {
        Flow flow = new Flow();
        Label label = new Label();
        Variable variable = new Variable("variable", ValueType.booleanType());
        Flow.Method method = flow.createMethod("foo", new Signature(ValueType.emptyType()), codeBuilder -> codeBuilder
                .pushInteger(1)
                .pushInteger(1)
                .add()
                .pushInteger(2)
                .equalTo()
                .insertLabel(label)
                .assign(variable)
                .returnValue());
        List<FlowSnapshot> snapshots = method.afterInstruction(label);
        Assertions.assertEquals(1, snapshots.size());
        FlowSnapshot snapshot = snapshots.getFirst();
        Constraint constraint = snapshot.compute(new BinaryExpression(BinaryOperator.EQUAL_TO, variable, LiteralExpression.booleanLiteral(true)));
        Assertions.assertEquals(Constraint.ALWAYS_TRUE, constraint);
    }
}
