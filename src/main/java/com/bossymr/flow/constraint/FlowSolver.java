package com.bossymr.flow.constraint;

import com.bossymr.flow.Flow;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.state.FlowSnapshot;
import io.github.cvc5.CVC5ApiException;
import io.github.cvc5.Solver;
import io.github.cvc5.TermManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

/**
 * A solver owned by a specific snapshot.
 * <p>
 * The solver can be retrieved by any other snapshot, which will modify the solver to match the specified snapshot as
 * efficiently as possible.
 * <p>
 * For each snapshot the solver enters, a layer is added, as such, when it has to exit the snapshot, it can remove the
 * layer and only the constraints added by it.
 */
public class FlowSolver {

    private final Flow flow;
    private final TermManager manager;
    private final Solver solver;
    private FlowSnapshot owner;

    private List<LongAdder> constraints = new ArrayList<>();

    public FlowSolver(Flow flow) {
        this.flow = flow;
        this.manager = new TermManager();
        this.solver = new Solver(manager);
        try {
            solver.setLogic("ALL");
        } catch (CVC5ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public Solver getSolver(FlowSnapshot snapshot) {
        FlowSnapshot commonPredecessor = snapshot.commonPredecessor(owner);
        backtrack(commonPredecessor);
        enterAll(snapshot);
        return solver;
    }

    public TermManager getManager() {
        return manager;
    }

    /**
     * Successively enters all snapshots between the current owner of the solver and the provided snapshot.
     *
     * @param snapshot the snapshot
     */
    private void enterAll(FlowSnapshot snapshot) {
        List<FlowSnapshot> predecessors = snapshot.getPredecessors();
        for (FlowSnapshot predecessor : predecessors) {
            // Skip until we reach the element after the current owner.
            if (predecessor.getPredecessor() != owner) {
                continue;
            }
            owner = predecessor;
            if (predecessor.getConstraints().isEmpty()) {
                continue;
            }
            try {
                flow.getStatistics().get(Flow.Statistic.SatisfiabilityPush).increment();
                constraints.add(new LongAdder());
                solver.push();
            } catch (CVC5ApiException e) {
                throw new RuntimeException(e);
            }
            for (Expression constraint : predecessor.getConstraints()) {
                flow.getStatistics().get(Flow.Statistic.SatisfiabilityAssertions).increment();
                constraints.getLast().increment();
                solver.assertFormula(constraint.convert(manager));
            }
        }
    }

    /**
     * Successively pops constraints from the current owner of the solver to the provided snapshot.
     *
     * @param snapshot the snapshot
     */
    private void backtrack(FlowSnapshot snapshot) {
        while (owner != snapshot && owner != null) {
            if (owner.getConstraints().isEmpty()) {
                owner = owner.getPredecessor();
                continue;
            }
            owner = owner.getPredecessor();
            try {
                flow.getStatistics().get(Flow.Statistic.SatisfiabilityPop).increment();
                constraints.removeLast();
                solver.pop();
            } catch (CVC5ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
