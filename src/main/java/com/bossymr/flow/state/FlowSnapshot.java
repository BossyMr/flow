package com.bossymr.flow.state;

import com.bossymr.flow.Flow;
import com.bossymr.flow.constraint.Constraint;
import com.bossymr.flow.constraint.ConstraintEngine;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.instruction.Instruction;

import java.util.*;

/**
 * A snapshot of the program at a specific instruction.
 */
public class FlowSnapshot {

    private final Flow flow;

    private final FlowSnapshot predecessor;
    private final FlowSnapshot weakPredecessor;
    private final Set<FlowSnapshot> successors = new HashSet<>();
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
        this.constraints.addAll(predecessor.constraints);
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
        FlowSnapshot successor = new FlowSnapshot(this.flow, this, null, this.instruction);
        getSuccessors().add(successor);
        return successor;
    }

    /**
     * Create a successor to this snapshot representing the same instruction as this snapshot.
     *
     * @param snapshot a disconnected predecessor to this snapshot.
     * @return a new snapshot.
     */
    public FlowSnapshot successorState(FlowSnapshot snapshot) {
        FlowSnapshot successor = new FlowSnapshot(this.flow, this, snapshot, this.instruction);
        getSuccessors().add(successor);
        return successor;
    }

    /**
     * Create a successor to this snapshot.
     *
     * @param instruction the instruction.
     * @return a new snapshot.
     */
    public FlowSnapshot successorState(Instruction instruction) {
        FlowSnapshot successor = new FlowSnapshot(this.flow, this, null, instruction);
        getSuccessors().add(successor);
        return successor;
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
     * {@return the successors of this snapshot}
     */
    public Set<FlowSnapshot> getSuccessors() {
        return successors;
    }

    /**
     * {@return the predecessor of this snapshot}
     */
    public FlowSnapshot getPredecessor() {
        return predecessor;
    }

    /**
     * Returns the weak predecessor of this snapshot.
     * <p>
     * The weak predecessor of a snapshot is a snapshot that this snapshot is based on, but does not succeed. This
     * is used to identify the exit state of another method, which this snapshot's predecessor called.
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
        return switch (ConstraintEngine.isReachable(this)) {
            case REACHABLE, UNKNOWN -> true;
            case NOT_REACHABLE -> false;
        };
    }

    /**
     * Attempts to compute the result of the provided expression.
     *
     * @param expression the expression to compute.
     * @return the result of the provided expression.
     */
    public Constraint compute(Expression expression) {
        return ConstraintEngine.getConstraint(this, expression);
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
