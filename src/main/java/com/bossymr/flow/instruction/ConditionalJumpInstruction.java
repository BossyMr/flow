package com.bossymr.flow.instruction;

import com.bossymr.flow.constraint.Constraint;
import com.bossymr.flow.constraint.ConstraintEngine;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.UnaryExpression;
import com.bossymr.flow.state.FlowEngine;
import com.bossymr.flow.state.FlowMethod;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Pops a value off the stack, jumps to the specified instruction if the value is {@code true}.
 * <p>
 * {@code [boolean] -> }
 */
public final class ConditionalJumpInstruction implements Instruction {

    private final Label instruction;

    public ConditionalJumpInstruction(Label instruction) {
        this.instruction = instruction;
    }

    @Override
    public List<FlowSnapshot> call(FlowEngine engine, FlowMethod method, FlowSnapshot snapshot) {
        Expression condition = snapshot.pop();
        Constraint constraint = ConstraintEngine.getConstraint(snapshot, condition);
        List<FlowSnapshot> successors = new ArrayList<>();
        if (constraint == Constraint.ANY_VALUE || constraint == Constraint.UNKNOWN || constraint == Constraint.ALWAYS_TRUE) {
            FlowSnapshot successor = snapshot.successorState();
            successor.require(condition);
            successors.add(successor.successorState(method));
        }
        if (constraint == Constraint.ANY_VALUE || constraint == Constraint.UNKNOWN || constraint == Constraint.ALWAYS_FALSE) {
            FlowSnapshot successor = snapshot.successorState();
            successor.require(new UnaryExpression(UnaryExpression.Operator.NOT, condition));
            successors.add(successor.successorState(method));
        }
        return List.copyOf(successors);
    }

    public Label getInstruction() {
        return instruction;
    }

    @Override
    public String toString() {
        return "conditionalJump";
    }
}
