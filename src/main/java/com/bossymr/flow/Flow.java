package com.bossymr.flow;

import com.bossymr.flow.constraint.FlowSolver;
import com.bossymr.flow.expression.AnyExpression;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.instruction.Instruction;
import com.bossymr.flow.instruction.ReturnInstruction;
import com.bossymr.flow.state.FlowSnapshot;
import com.bossymr.flow.type.ValueType;

import java.util.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

/**
 * A data flow analyzer.
 */
public class Flow {

    private final FlowSolver solver;

    private final EnumMap<Statistic, LongAdder> statistics;

    public Flow() {
        this.solver = new FlowSolver(this);
        this.statistics = new EnumMap<>(Statistic.class);
        for (Statistic statistic : Statistic.values()) {
            statistics.put(statistic, new LongAdder());
        }
    }

    /**
     * Create a new method.
     *
     * @param name the method's name.
     * @param signature the method's signature.
     * @param code the method's code body.
     * @return a new method.
     */
    public Method createMethod(String name, Signature signature, Consumer<CodeBuilder> code) {
        return new Method(name, signature, code);
    }

    public FlowSolver getSolver() {
        return solver;
    }

    /**
     * {@return the statistics kept by this data flow analyzer}
     */
    public EnumMap<Statistic, LongAdder> getStatistics() {
        return statistics;
    }

    public enum Statistic {
        /**
         * How many times the solver was asked whether a set of assertions was satisfiable.
         */
        SatisfiabilityQueries,

        /**
         * How many times the solver was given an assertion.
         */
        SatisfiabilityAssertions,

        /**
         * How many times a layer was added to the solver.
         */
        SatisfiabilityPush,

        /**
         * How many times a layer was removed from the solver.
         */
        SatisfiabilityPop,

        /**
         * How many times a snapshot was created.
         */
        Snapshots,
    }

    /**
     * A method.
     * <p>
     * A method is type checked to assert correctness during data flow analysis. Trying to create an invalid method will
     * throw an exception, as such, it is up to the user to validate any code before it is passed to the analyzer. For
     * example, casting objects from one type to another is not supported natively, and needs to be implemented in the
     * code passed to the method.
     */
    public class Method {

        private final String name;
        private final Signature signature;
        private final List<Instruction> instructions;

        private final FlowSnapshot entryPoint;
        private final List<FlowSnapshot> exitPoints = new ArrayList<>();
        private final List<Expression> arguments;

        private Method(String name, Signature signature, Consumer<CodeBuilder> code) {
            this.name = name;
            this.signature = signature;
            this.instructions = new ArrayList<>();
            CodeBuilder codeBuilder = new CodeBuilder(this);
            code.accept(codeBuilder);
            Instruction lastInstruction = instructions.getLast();
            if (!(lastInstruction instanceof ReturnInstruction)) {
                if (signature.returnType() == ValueType.emptyType()) {
                    instructions.add(new ReturnInstruction());
                } else {
                    throw new IllegalStateException("method '" + this + "' must return a value");
                }
            }
            // We need the entry point to be before the first instruction in the method.
            // This is so that we can call #beforeInstruction(...) on the first instruction.
            this.entryPoint = FlowSnapshot.emptyState(Flow.this);
            this.arguments = new ArrayList<>();
            for (int i = 0; i < signature.arguments().size(); i++) {
                ValueType argument = signature.arguments().get(i);
                Expression variable = new AnyExpression(argument);
                this.arguments.add(variable);
                this.entryPoint.store(i, variable);
            }
            compute();
        }

        private void compute() {
            Deque<FlowSnapshot> queue = new ArrayDeque<>();
            Instruction firstInstruction = instructions.getFirst();
            if (firstInstruction != null) {
                queue.addFirst(entryPoint.successorState(firstInstruction));
            }
            while (!queue.isEmpty()) {
                FlowSnapshot snapshot = queue.pop();
                Instruction instruction = snapshot.getInstruction();
                int index = instructions.indexOf(instruction);
                if (index < 0) {
                    throw new IllegalStateException("memory state belongs to instruction from other method");
                }
                Instruction successor;
                if (index + 1 >= instructions.size()) {
                    successor = null;
                } else {
                    successor = instructions.get(index + 1);
                }
                List<FlowSnapshot> successors = instruction.call(this, snapshot, successor);
                for (FlowSnapshot successorState : successors.reversed()) {
                    queue.addFirst(successorState);
                }
            }
        }

        public Flow getFlow() {
            return Flow.this;
        }

        /**
         * {@return the method's name}
         */
        public String getName() {
            return name;
        }

        /**
         * {@return the method's signature}
         */
        public Signature getSignature() {
            return signature;
        }

        /**
         * {@return the method's instructions}
         */
        public List<Instruction> getInstructions() {
            return instructions;
        }

        /**
         * Returns the expressions used to reference this method's arguments.
         *
         * @return a list of expressions
         */
        public List<Expression> getArguments() {
            return arguments;
        }

        /**
         * A snapshot made at the start of this method before any instruction is called.
         *
         * @return a snapshot of the program
         */
        public FlowSnapshot getEntryPoint() {
            return entryPoint;
        }

        /**
         * All possible snapshots made at the exit points of this method.
         *
         * @return a list of all possible snapshots
         */
        public List<FlowSnapshot> getExitPoints() {
            return exitPoints;
        }

        /**
         * All possible snapshots made at the exit points of this method if this method was called by the provided
         * snapshot. All arguments to this method are popped from the stack of the provided snapshot in the order they
         * are declared.
         *
         * @param caller a snapshot of the program calling this method.
         * @return a list of all possible snapshots
         */
        public List<FlowSnapshot> getExitPoints(FlowSnapshot caller) {
            if (signature.returnType() == ValueType.emptyType()) {
                return List.of(caller);
            }
            List<FlowSnapshot> states = new ArrayList<>();
            // Iterate over all possible exit points of this method.
            for (FlowSnapshot exitPoint : exitPoints) {
                // Create a copy of the memory state.
                FlowSnapshot snapshot = caller.successorState(exitPoint);
                Map<Expression, Expression> values = new HashMap<>();
                // For all arguments, pop the argument from the stack (in reverse order since the first argument is
                // popped last).
                for (Expression argument : arguments.reversed()) {
                    Expression value = snapshot.pop();
                    values.put(argument, value);
                }
                Expression returnValue = exitPoint.getStack().getLast().translate(child -> values.getOrDefault(child, child));
                snapshot.push(returnValue);
                // All constraints from the call site have been added to the disconnected state, as such, we can check
                // if it is possible for this memory state to be returned given the arguments we pass to the method.
                if (!snapshot.isReachable()) {
                    continue;
                }
                states.add(snapshot);
            }
            return states;
        }

        /**
         * All possible snapshots made before the provided instruction.
         *
         * @param instruction the instruction
         * @return a list of all possible snapshots
         */
        public List<FlowSnapshot> beforeInstruction(Instruction instruction) {
            Deque<FlowSnapshot> queue = new ArrayDeque<>(exitPoints);
            List<FlowSnapshot> states = new ArrayList<>();
            while (!queue.isEmpty()) {
                // Search all snapshots for snapshots belonging to the specified instruction.
                // If an instruction belongs to the specified instruction, add its predecessor to the list.
                // We need to search until we reach the end of the method, since an instruction might be encountered
                // than once.
                FlowSnapshot snapshot = queue.pop();
                FlowSnapshot predecessor = snapshot.getPredecessor();
                if (instruction.equals(snapshot.getInstruction())) {
                    // The predecessor cannot be null, because the first instruction always has a predecessor that
                    // doesn't belong to any instruction.
                    states.add(predecessor);
                }
                if (predecessor != null) {
                    queue.add(predecessor);
                }
            }
            return states;
        }

        /**
         * All possible snapshots made after the provided instruction.
         *
         * @param instruction the instruction
         * @return a list of all possible snapshots
         */
        public List<FlowSnapshot> afterInstruction(Instruction instruction) {
            Deque<FlowSnapshot> queue = new ArrayDeque<>(exitPoints);
            List<FlowSnapshot> states = new ArrayList<>();
            while (!queue.isEmpty()) {
                // Search all snapshots for snapshots where it's predecessor belongs to the specified instruction, but not
                // the instruction itself.
                FlowSnapshot snapshot = queue.pop();
                FlowSnapshot predecessor = snapshot.getPredecessor();
                if (predecessor != null) {
                    if (!instruction.equals(snapshot.getInstruction())) {
                        if (instruction.equals(predecessor.getInstruction())) {
                            states.add(snapshot);
                        }
                    }
                    queue.add(predecessor);
                }
            }
            return states;
        }

        @Override
        public String toString() {
            return name + signature;
        }
    }
}
