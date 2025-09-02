package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
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
                // Before:
                // {SatisfiabilityQueries=106, SatisfiabilityAssertions=1483, SatisfiabilityPush=1483, SatisfiabilityPop=1431, Snapshots=1080}
                // After:
                // {SatisfiabilityQueries=106, SatisfiabilityAssertions=1483, SatisfiabilityPush=1483, SatisfiabilityPop=1431, Snapshots=978}
                // After fixing common predecessor:
                // {SatisfiabilityQueries=106, SatisfiabilityAssertions=157, SatisfiabilityPush=157, SatisfiabilityPop=105, Snapshots=1080}
                // After fixing branch instruction:
                // {SatisfiabilityQueries=106, SatisfiabilityAssertions=107, SatisfiabilityPush=107, SatisfiabilityPop=55, Snapshots=978}
                Expression condition = snapshot.pop();
                List<FlowSnapshot> successors = new ArrayList<>();
                FlowSnapshot falseSnapshot = snapshot.successorState(successor);
                falseSnapshot.require(new UnaryExpression(new UnaryOperator.Not(), condition));
                if (falseSnapshot.isReachable()) {
                    successors.add(falseSnapshot);
                }
                FlowSnapshot trueSnapshot = snapshot.successorState(instruction);
                trueSnapshot.require(condition);
                if (trueSnapshot.isReachable()) {
                    successors.add(trueSnapshot);
                }
                yield List.copyOf(successors.reversed());
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
