package com.bossymr.flow.state;

import com.bossymr.flow.Flow;
import com.bossymr.flow.constraint.Constraint;
import com.bossymr.flow.constraint.Reachable;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.expression.UnaryExpression;
import com.bossymr.flow.instruction.Instruction;
import com.bossymr.flow.instruction.UnaryOperator;
import io.github.cvc5.Result;
import io.github.cvc5.Solver;

import java.util.*;

import static com.bossymr.flow.constraint.Reachable.REACHABLE;
import static com.bossymr.flow.constraint.Reachable.UNKNOWN;

/**
 * A snapshot of the program at a specific instruction.
 */
public class FlowSnapshot {

    private final Flow flow;

    private final FlowSnapshot predecessor;
    private final FlowSnapshot weakPredecessor;
    private final Instruction instruction;

    private final Set<Expression> constraints = new HashSet<>();

    private final List<Expression> stack = new ArrayList<>();
    private final HashMap<Integer, Expression> variables = new HashMap<>();

    private FlowSnapshot(Flow flow, Instruction instruction) {
        flow.getStatistics().get(Flow.Statistic.Snapshots).increment();
        this.flow = flow;
        this.instruction = instruction;
        this.predecessor = null;
        this.weakPredecessor = null;
    }

    private FlowSnapshot(Flow flow, FlowSnapshot predecessor, FlowSnapshot weakPredecessor, Instruction instruction) {
        flow.getStatistics().get(Flow.Statistic.Snapshots).increment();
        this.flow = flow;
        this.weakPredecessor = weakPredecessor;
        this.instruction = instruction;
        Objects.requireNonNull(predecessor);
        this.predecessor = predecessor;
        this.stack.addAll(predecessor.stack);
        this.variables.putAll(predecessor.variables);
    }

    /**
     * Create a new, empty, snapshot.
     *
     * @param engine the current flow engine.
     * @return a new snapshot.
     */
    public static FlowSnapshot emptyState(Flow engine) {
        return new FlowSnapshot(engine, null);
    }

    /**
     * Create a successor to this snapshot representing the same instruction as this snapshot.
     *
     * @return a new snapshot.
     */
    public FlowSnapshot successorState() {
        return new FlowSnapshot(this.flow, this, null, this.instruction);
    }

    /**
     * Create a successor to this snapshot representing the same instruction as this snapshot.
     *
     * @param snapshot a disconnected predecessor to this snapshot.
     * @return a new snapshot.
     */
    public FlowSnapshot successorState(FlowSnapshot snapshot) {
        return new FlowSnapshot(this.flow, this, snapshot, this.instruction);
    }

    /**
     * Create a successor to this snapshot.
     *
     * @param instruction the instruction.
     * @return a new snapshot.
     */
    public FlowSnapshot successorState(Instruction instruction) {
        return new FlowSnapshot(this.flow, this, null, instruction);
    }

    /**
     * Creates a successor to this snapshot, which is not stored as a successor to this snapshot.
     *
     * @return a new snapshot.
     */
    public FlowSnapshot disconnectedState() {
        return new FlowSnapshot(this.flow, this, null, null);
    }

    public Flow getFlow() {
        return flow;
    }

    /**
     * {@return the instruction this snapshot represents}
     */
    public Instruction getInstruction() {
        return instruction;
    }

    /**
     * {@return the predecessor of this snapshot}
     */
    public FlowSnapshot getPredecessor() {
        return predecessor;
    }

    /**
     * {@return all predecessors up until this snapshot}
     */
    public List<FlowSnapshot> getPredecessors() {
        List<FlowSnapshot> snapshots = new ArrayList<>();
        FlowSnapshot snapshot = this;
        while (snapshot != null) {
            snapshots.add(snapshot);
            snapshot = snapshot.getPredecessor();
        }
        return snapshots.reversed();
    }

    /**
     * Returns the last common predecessor of this snapshot and the provided snapshot. Trying to get the common
     * predecessor of this snapshot and {@code null} returns all predecessors.
     *
     * @param snapshot the snapshot
     * @return the last common predecessor, or {@code null} if a common predecessor was not found
     */
    public FlowSnapshot commonPredecessor(FlowSnapshot snapshot) {
        List<FlowSnapshot> predecessors = getPredecessors();
        while (snapshot != null) {
            if (predecessors.contains(snapshot)) {
                return snapshot;
            }
            snapshot = snapshot.getPredecessor();
        }
        return null;
    }

    /**
     * Returns the weak predecessor of this snapshot.
     * <p>
     * The weak predecessor of a snapshot is a snapshot that this snapshot is based on, but does not succeed. This is
     * used to identify the exit state of another method, which this snapshot's predecessor called.
     *
     * @return the weak predecessor of this snapshot.
     */
    public FlowSnapshot getWeakPredecessor() {
        return weakPredecessor;
    }

    /**
     * {@return all constraints defined in this snapshot}
     */
    public Set<Expression> getConstraints() {
        return constraints;
    }

    /**
     * {@return the current program stack}
     */
    public List<Expression> getStack() {
        return stack;
    }

    /**
     * {@return if this snapshot is reachable}
     */
    public boolean isReachable() {
        return switch (getReachability()) {
            case REACHABLE, UNKNOWN -> true;
            case NOT_REACHABLE -> false;
        };
    }

    /**
     * {@return if this snapshot is reachable}
     */
    public Reachable getReachability() {
        Solver solver = flow.getSolver().getSolver(this);
        flow.getStatistics().get(Flow.Statistic.SatisfiabilityQueries).increment();
        Result result = solver.checkSat();
        if (result.isSat()) {
            return Reachable.REACHABLE;
        }
        if (result.isUnsat()) {
            return Reachable.NOT_REACHABLE;
        }
        return Reachable.UNKNOWN;
    }

    /**
     * Attempts to compute the result of the provided expression.
     *
     * @param expression the expression to compute.
     * @return the result of the provided expression.
     */
    public Constraint compute(Expression expression) {
        FlowSnapshot trueSnapshot = disconnectedState();
        trueSnapshot.require(expression);
        Reachable trueReachability = trueSnapshot.getReachability();
        if (trueReachability == UNKNOWN) {
            return Constraint.UNKNOWN;
        }
        FlowSnapshot falseSnapshot = disconnectedState();
        falseSnapshot.require(new UnaryExpression(new UnaryOperator.Not(), expression));
        Reachable falseReachability = falseSnapshot.getReachability();
        if (falseReachability == UNKNOWN) {
            return Constraint.UNKNOWN;
        }
        if (trueReachability == REACHABLE && falseReachability == REACHABLE) {
            return Constraint.ANY_VALUE;
        }
        if (trueReachability == REACHABLE) {
            return Constraint.ALWAYS_TRUE;
        }
        if (falseReachability == REACHABLE) {
            return Constraint.ALWAYS_FALSE;
        }
        return Constraint.NO_VALUE;
    }

    /**
     * Pushes the specified expression to the stack.
     *
     * @param expression the expression.
     */
    public void push(Expression expression) {
        stack.addLast(expression);
    }

    /**
     * Pops an expression from the stack.
     *
     * @return an expression.
     */
    public Expression pop() {
        return stack.removeLast();
    }

    /**
     * Stores an expression in a variable.
     *
     * @param variable the variable
     * @param expression the expression
     */
    public void store(int variable, Expression expression) {
        variables.put(variable, expression);
    }

    /**
     * Loads an expression from a variable.
     *
     * @param variable the variable
     * @return the expression
     */
    public Expression load(int variable) {
        return variables.get(variable);
    }

    /**
     * Adds the specified expression as a constraint.
     *
     * @param expression the expression.
     */
    public void require(Expression expression) {
        constraints.add(expression);
    }
}
