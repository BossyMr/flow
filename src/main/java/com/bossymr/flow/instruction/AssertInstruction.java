package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
import com.bossymr.flow.constraint.Constraint;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.state.FlowSnapshot;

/**
 * Asserts the value at the top of the stack.
 * <p>
 * Throws a runtime exception if the value at the top of the stack is not {@code true}.
 */
public final class AssertInstruction implements LinearInstruction {

    private final Constraint expected;

    public AssertInstruction() {
        this.expected = Constraint.ALWAYS_TRUE;
    }

    public AssertInstruction(Constraint expected) {
        this.expected = expected;
    }

    @Override
    public void perform(Flow.Method method, FlowSnapshot snapshot) {
        Expression expression = snapshot.pop();
        Constraint constraint = snapshot.compute(expression);
        if (expected == Constraint.ALWAYS_TRUE || expected == Constraint.ALWAYS_FALSE) {
            // If we expect a certain value, the actual value needs to be that exact value.
            if (constraint != this.expected) {
                throw new AssertionError("expression '" + expression + "': " + constraint);
            }
            return;
        }
        if (this.expected == Constraint.ANY_VALUE) {
            // If we expect any value, the actual value needs to be a known value.
            if (constraint == Constraint.NO_VALUE || constraint == Constraint.UNKNOWN) {
                throw new AssertionError("expression '" + expression + "': " + constraint);
            }
            return;
        }
        if (constraint != this.expected) {
            throw new AssertionError("expression '" + expression + "': " + constraint);
        }
    }
}
