package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
import com.bossymr.flow.constraint.Constraint;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.UnaryExpression;
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
    public List<FlowSnapshot> call(Flow.Method method, FlowSnapshot snapshot, Instruction successor) {
        return switch (kind) {
            case ALWAYS -> {
                FlowSnapshot successorState = snapshot.successorState(instruction);
                yield List.of(successorState);
            }
            case CONDITIONALLY -> {
                Expression condition = snapshot.pop();
                Constraint constraint = snapshot.compute(condition);
                List<FlowSnapshot> successors = new ArrayList<>();
                if (constraint == Constraint.ANY_VALUE || constraint == Constraint.UNKNOWN || constraint == Constraint.ALWAYS_TRUE) {
                    FlowSnapshot successorState = snapshot.successorState();
                    successorState.require(condition);
                    successors.add(successorState.successorState(instruction));
                }
                if (constraint == Constraint.ANY_VALUE || constraint == Constraint.UNKNOWN || constraint == Constraint.ALWAYS_FALSE) {
                    FlowSnapshot successorState = snapshot.successorState();
                    successorState.require(new UnaryExpression(new UnaryOperator.Not(), condition));
                    successors.add(successorState.successorState(successor));
                }
                yield List.copyOf(successors);
            }
        };
    }

    public BranchKind getKind() {
        return kind;
    }

    public Label getInstruction() {
        return instruction;
    }

    @Override
    public String toString() {
        return switch (kind) {
            case ALWAYS -> "jump(" + instruction + ")";
            case CONDITIONALLY -> "branch(" +  instruction + ")";
        };
    }

}
