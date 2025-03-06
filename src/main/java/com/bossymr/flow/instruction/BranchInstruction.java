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
 * Jump to a provided instruction depending on some criteria.
 * <p>
 * Depending on this instruction's {@link BranchKind}, this instruction will either always jump to the provided
 * instruction, or jump depending on the value at the top of the stack.
 */
public final class BranchInstruction implements Instruction {

    private final BranchKind kind;
    private final Label instruction;

    public BranchInstruction(BranchKind kind, Label instruction) {
        this.kind = kind;
        this.instruction = instruction;
    }

    @Override
    public List<FlowSnapshot> call(FlowEngine engine, FlowMethod method, FlowSnapshot snapshot) {
        return switch (kind) {
            case ALWAYS -> {
                FlowSnapshot successor = snapshot.successorState(instruction);
                yield List.of(successor);
            }
            case CONDITIONALLY -> {
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
                    successor.require(new UnaryExpression(UnaryOperator.NOT, condition));
                    successors.add(successor.successorState(method));
                }
                yield List.copyOf(successors);
            }
        };
    }

    public Label getInstruction() {
        return instruction;
    }

    @Override
    public String toString() {
        return "branch";
    }
}
