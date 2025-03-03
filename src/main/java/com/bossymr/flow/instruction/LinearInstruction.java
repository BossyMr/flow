package com.bossymr.flow.instruction;

import com.bossymr.flow.expression.Expression;
import com.bossymr.flow.state.FlowEngine;
import com.bossymr.flow.state.FlowMethod;
import com.bossymr.flow.state.FlowSnapshot;

import java.util.List;

public sealed interface LinearInstruction extends Instruction permits AddInstruction, AndInstruction, ConstantBooleanInstruction, ConstantByteInstruction, ConstantIntegerInstruction, ConstantLongInstruction, ConstantStringInstruction, DivideInstruction, EqualToInstruction, GreaterThanInstruction, IntegerToRealInstruction, LessThanInstruction, ModuloInstruction, MultiplyInstruction, NegateInstruction, NotInstruction, OrInstruction, RealToIntegerInstruction, SubtractInstruction, XorInstruction {
    @Override
    default List<FlowSnapshot> call(FlowEngine engine, FlowMethod method, FlowSnapshot snapshot) {
        snapshot.push(getExpression(snapshot));
        FlowSnapshot successor = snapshot.successorState(method);
        return List.of(successor);
    }

    Expression getExpression(FlowSnapshot predecessor);
}
