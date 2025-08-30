package com.bossymr.flow.instruction;

import com.bossymr.flow.Flow;
import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.state.FlowSnapshot;
import com.bossymr.flow.type.EmptyType;
import com.bossymr.flow.type.ValueType;

import java.util.*;

/**
 * Calls a method specified by the current method at the index specified by the next integer. Depending on the method,
 * all arguments are popped off the stack in the order they are defined. The return value of the method is pushed onto
 * the stack.
 * <p>
 * {@code [argument 1] [argument 2] [...] -> [return type]}
 */
public final class CallInstruction implements Instruction {

    private final Flow.Method method;

    public CallInstruction(Flow.Method method) {
        this.method = method;
    }

    @Override
    public List<FlowSnapshot> call(Flow.Method method, FlowSnapshot snapshot, Instruction successor) {
        if (this.method.getSignature().returnType() instanceof EmptyType) {
            // This method is as far as we are considered 'pure'. We don't keep track of modifications to external
            // fields, so unless the memory returns a variable, it can't modify the program's state.
            FlowSnapshot successorState = snapshot.successorState(successor);
            return List.of(successorState);
        }
        List<FlowSnapshot> successors = new ArrayList<>();
        // Iterate over all possible memory states at the end of the provided method.
        for (FlowSnapshot exitPoint : this.method.getExitPoints()) {
            // Create a disconnected copy of the memory state.
            // The copy won't be seen by calling: exitPoint.getSuccessors()
            FlowSnapshot copy = exitPoint.disconnectedState();
            // Add all constraints at the call site to the memory state.
            copy.getConstraints().addAll(snapshot.getConstraints());
            List<ValueType> arguments = this.method.getSignature().arguments();
            // For all arguments, pop the argument from the stack (in reverse order since the first argument is
            // popped last). Set the passed value to be equal to the argument in the method.
            Map<Expression, Expression> table = new HashMap<>();
            for (int i = 1; i < arguments.size() + 1; i++) {
                Expression expression = this.method.getArguments().get(arguments.size() - i);
                Expression value = snapshot.getStack().get(snapshot.getStack().size() - i);
                table.put(expression, value);
                copy.require(new BinaryExpression(BinaryOperator.EQUAL_TO, expression, value));
            }
            // All constraints from the call site have been added to the disconnected state, as such, we can check
            // if it is possible for this memory state to be returned given the arguments we pass to the method.
            if (copy.isReachable()) {
                // We need to replace the argument placeholder with the actual argument value.
                Expression returnValue = copy.pop().translate(child -> {
                    if (table.containsKey(child)) {
                        return table.get(child);
                    }
                    return child;
                });
                FlowSnapshot successorState = snapshot.successorState(exitPoint);
                for (int i = 0; i < arguments.size(); i++) {
                    successorState.pop();
                }
                successorState.push(returnValue);
                successors.add(successorState.successorState(successor));
            }
        }
        return successors;
    }

    public Flow.Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "call(" + method + ")";
    }

}
