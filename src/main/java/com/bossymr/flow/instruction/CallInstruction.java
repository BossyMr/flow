package com.bossymr.flow.instruction;

import com.bossymr.flow.Method;
import com.bossymr.flow.expression.BinaryExpression;
import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.state.FlowEngine;
import com.bossymr.flow.state.FlowMethod;
import com.bossymr.flow.state.FlowSnapshot;
import com.bossymr.flow.type.EmptyType;
import com.bossymr.flow.type.ValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calls a method specified by the current method at the index specified by the next integer. Depending on the
 * method, all arguments are popped off the stack in the order they are defined. The return value of the method is
 * pushed onto the stack.
 * <p>
 * {@code [argument 1] [argument 2] [...] -> [return type]}
 */
public final class CallInstruction implements Instruction {

    private final Method method;

    public CallInstruction(Method method) {
        this.method = method;
    }

    @Override
    public List<FlowSnapshot> call(FlowEngine engine, FlowMethod method, FlowSnapshot snapshot) {
        if (this.method.getReturnType() instanceof EmptyType) {
            // This method is as far as we are considered 'pure'. We don't keep track of modifications to external
            // fields, so unless the memory returns a variable, it can't modify the program's state.
            FlowSnapshot successor = snapshot.successorState(method);
            return List.of(successor);
        }
        List<FlowSnapshot> successors = new ArrayList<>();
        FlowMethod target = engine.getMethod(this.method);
        // Iterate over all possible memory states at the end of the provided method.
        for (FlowSnapshot exitPoint : target.getExitPoints()) {
            // Create a disconnected copy of the memory state.
            // The copy won't be seen by calling: exitPoint.getSuccessors()
            FlowSnapshot copy = exitPoint.disconnectedState();
            // Add all constraints at the call site to the memory state.
            copy.getConstraints().addAll(snapshot.getConstraints());
            List<ValueType> arguments = this.method.getArguments();
            // For all arguments, pop the argument from the stack (in reverse order since the first argument is
            // popped last). Set the passed value to be equal to the argument in the method.
            Map<Expression, Expression> table = new HashMap<>();
            for (int i = 1; i < arguments.size() + 1; i++) {
                Expression expression = target.getParameter(arguments.size() - i);
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
                FlowSnapshot successor = snapshot.successorState(exitPoint);
                for (int i = 0; i < arguments.size(); i++) {
                    successor.pop();
                }
                successor.push(returnValue);
                successors.add(successor.successorState(method));
            }
        }
        return successors;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "call " + method.getName();
    }
}
