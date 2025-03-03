package com.bossymr.flow;

import com.bossymr.flow.expression.Variable;
import com.bossymr.flow.instruction.*;
import com.bossymr.flow.type.EmptyType;
import com.bossymr.flow.type.ValueType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CodeBuilder {

    private final Method method;
    private final List<ValueType> stack;
    private final List<Instruction> instructions = new ArrayList<>();

    public CodeBuilder(Method method, List<ValueType> stack) {
        this.method = method;
        this.stack = stack;
    }

    public Label newlabel() {
        return new Label();
    }

    public Label startLabel() {
        Label label = newlabel();
        insertLabel(label);
        return label;
    }

    public CodeBuilder insertLabel(Label label) {
        instructions.add(label);
        return this;
    }

    public CodeBuilder conditionalJump(Label thenLabel) {
        validateStack(1, List.of(new InstructionKind(ValueType.emptyType(), ValueType.booleanType())));
        instructions.add(new ConditionalJumpInstruction(thenLabel));
        return this;
    }

    public CodeBuilder jump(Label label) {
        instructions.add(new JumpInstruction(label));
        return this;
    }

    public CodeBuilder call(Method method) {
        validateStack(method.getArguments().size(), List.of(new InstructionKind(method.getReturnType(), method.getArguments())));
        if (!(method.getReturnType() instanceof EmptyType)) {
            stack.add(method.getReturnType());
        }
        instructions.add(new CallInstruction(method));
        return this;
    }

    public CodeBuilder ifThen(Consumer<CodeBuilder> thenHandler) {
        // 1) if not condition -> 3
        // 2) thenHandler
        // 3) elseLabel
        Label elseLabel = newlabel();
        not();
        conditionalJump(elseLabel);
        CodeBuilder thenCodeBuilder = new CodeBuilder(method, new ArrayList<>(stack));
        thenHandler.accept(thenCodeBuilder);
        instructions.addAll(thenCodeBuilder.getInstructions());
        insertLabel(elseLabel);
        if (!stack.equals(thenCodeBuilder.getStack())) {
            throw new IllegalArgumentException();
        }
        return this;
    }

    public CodeBuilder ifThenElse(Consumer<CodeBuilder> thenHandler, Consumer<CodeBuilder> elseHandler) {
        // 1) if (condition) -> 4
        // 2) elseHandler
        // 3) jump -> 5
        // 4) (thenLabel) thenHandler
        // 5) joinLabel
        Label thenLabel = newlabel();
        Label joinLabel = newlabel();
        conditionalJump(thenLabel);
        CodeBuilder elseCodeBuilder = new CodeBuilder(method, new ArrayList<>(stack));
        elseHandler.accept(elseCodeBuilder);
        instructions.addAll(elseCodeBuilder.getInstructions());
        jump(joinLabel);
        insertLabel(thenLabel);
        CodeBuilder thenCodeBuilder = new CodeBuilder(method, new ArrayList<>(stack));
        thenHandler.accept(thenCodeBuilder);
        instructions.addAll(thenCodeBuilder.getInstructions());
        insertLabel(joinLabel);
        if (!elseCodeBuilder.getStack().equals(thenCodeBuilder.getStack())) {
            throw new IllegalArgumentException();
        }
        stack.clear();
        stack.addAll(elseCodeBuilder.getStack());
        return this;
    }

    public CodeBuilder duplicate() {
        if (stack.isEmpty()) {
            throw new IllegalArgumentException();
        }
        stack.add(stack.getLast());
        instructions.add(new DuplicateInstruction());
        return this;
    }

    public CodeBuilder pushByte(byte value) {
        stack.add(ValueType.integerType());
        instructions.add(new ConstantByteInstruction(value));
        return this;
    }

    public CodeBuilder pushInteger(int value) {
        stack.add(ValueType.integerType());
        instructions.add(new ConstantIntegerInstruction(value));
        return this;
    }


    public CodeBuilder pushLong(long value) {
        stack.add(ValueType.integerType());
        instructions.add(new ConstantLongInstruction(value));
        return this;
    }

    public CodeBuilder pushString(String value) {
        stack.add(ValueType.stringType());
        instructions.add(new ConstantStringInstruction(value));
        return this;
    }


    public CodeBuilder pushBoolean(boolean value) {
        stack.add(ValueType.booleanType());
        instructions.add(new ConstantBooleanInstruction(value));
        return this;
    }

    public CodeBuilder add() {
        InstructionKind type = validateStack(2, List.of(
                new InstructionKind(ValueType.integerType(), ValueType.integerType(), ValueType.integerType()),
                new InstructionKind(ValueType.numericType(), ValueType.numericType(), ValueType.numericType()),
                new InstructionKind(ValueType.stringType(), ValueType.stringType(), ValueType.stringType())
        ));
        stack.add(type.output());
        instructions.add(new AddInstruction());
        return this;
    }

    public CodeBuilder subtract() {
        InstructionKind type = validateStack(2, List.of(
                new InstructionKind(ValueType.integerType(), ValueType.integerType(), ValueType.integerType()),
                new InstructionKind(ValueType.numericType(), ValueType.numericType(), ValueType.numericType())
        ));
        stack.add(type.output());
        instructions.add(new SubtractInstruction());
        return this;
    }

    public CodeBuilder multiply() {
        InstructionKind type = validateStack(2, List.of(
                new InstructionKind(ValueType.integerType(), ValueType.integerType(), ValueType.integerType()),
                new InstructionKind(ValueType.numericType(), ValueType.numericType(), ValueType.numericType())
        ));
        stack.add(type.output());
        instructions.add(new MultiplyInstruction());
        return this;
    }

    public CodeBuilder divide() {
        InstructionKind type = validateStack(2, List.of(
                new InstructionKind(ValueType.integerType(), ValueType.integerType(), ValueType.integerType()),
                new InstructionKind(ValueType.numericType(), ValueType.numericType(), ValueType.numericType())
        ));
        stack.add(type.output());
        instructions.add(new DivideInstruction());
        return this;
    }

    public CodeBuilder modulo() {
        InstructionKind type = validateStack(2, List.of(
                new InstructionKind(ValueType.integerType(), ValueType.integerType(), ValueType.integerType())
        ));
        stack.add(type.output());
        instructions.add(new ModuloInstruction());
        return this;
    }

    public CodeBuilder realToInteger() {
        InstructionKind type = validateStack(1, List.of(
                new InstructionKind(ValueType.integerType(), ValueType.numericType())
        ));
        stack.add(type.output());
        instructions.add(new RealToIntegerInstruction());
        return this;
    }

    public CodeBuilder integerToReal() {
        InstructionKind type = validateStack(1, List.of(
                new InstructionKind(ValueType.numericType(), ValueType.integerType())
        ));
        stack.add(type.output());
        instructions.add(new IntegerToRealInstruction());
        return this;
    }

    public CodeBuilder equalTo() {
        validateStackLength(2);
        stack.add(ValueType.booleanType());
        instructions.add(new EqualToInstruction());
        return this;
    }

    public CodeBuilder greaterThan() {
        InstructionKind type = validateStack(2, List.of(
                new InstructionKind(ValueType.booleanType(), ValueType.integerType(), ValueType.integerType()),
                new InstructionKind(ValueType.booleanType(), ValueType.numericType(), ValueType.numericType())
        ));
        stack.add(type.output());
        instructions.add(new GreaterThanInstruction());
        return this;
    }

    public CodeBuilder lessThan() {
        InstructionKind type = validateStack(2, List.of(
                new InstructionKind(ValueType.booleanType(), ValueType.integerType(), ValueType.integerType()),
                new InstructionKind(ValueType.booleanType(), ValueType.numericType(), ValueType.numericType())
        ));
        stack.add(type.output());
        instructions.add(new LessThanInstruction());
        return this;
    }

    public CodeBuilder and() {
        InstructionKind type = validateStack(2, List.of(
                new InstructionKind(ValueType.booleanType(), ValueType.booleanType(), ValueType.booleanType())
        ));
        stack.add(type.output());
        instructions.add(new AndInstruction());
        return this;
    }

    public CodeBuilder xor() {
        InstructionKind type = validateStack(2, List.of(
                new InstructionKind(ValueType.booleanType(), ValueType.booleanType(), ValueType.booleanType())
        ));
        stack.add(type.output());
        instructions.add(new XorInstruction());
        return this;
    }

    public CodeBuilder or() {
        InstructionKind type = validateStack(2, List.of(
                new InstructionKind(ValueType.booleanType(), ValueType.booleanType(), ValueType.booleanType())
        ));
        stack.add(type.output());
        instructions.add(new OrInstruction());
        return this;
    }

    public CodeBuilder not() {
        InstructionKind type = validateStack(1, List.of(
                new InstructionKind(ValueType.booleanType(), ValueType.booleanType())
        ));
        stack.add(type.output());
        instructions.add(new NotInstruction());
        return this;
    }

    public CodeBuilder negate() {
        InstructionKind type = validateStack(1, List.of(
                new InstructionKind(ValueType.integerType(), ValueType.integerType()),
                new InstructionKind(ValueType.numericType(), ValueType.numericType())
        ));
        stack.add(type.output());
        instructions.add(new NegateInstruction());
        return this;
    }

    public CodeBuilder assign(Variable variable) {
        validateStack(1, List.of(new InstructionKind(null, variable.getType())));
        instructions.add(new AssignInstruction(variable));
        return this;
    }

    public void returnValue() {
        if (!(method.getReturnType() instanceof EmptyType)) {
            validateStackLength(1);
            stack.removeLast();
        }
        instructions.add(new ReturnInstruction());
    }

    private void validateStackLength(int length) {
        if (stack.size() < length) {
            throw new IllegalArgumentException();
        }
    }

    private InstructionKind validateStack(int length, List<InstructionKind> alternatives) {
        List<ValueType> actual = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (stack.isEmpty()) {
                throw new IllegalArgumentException();
            }
            actual.add(stack.removeLast());
        }
        for (InstructionKind alternative : alternatives) {
            if (actual.equals(alternative.input())) {
                return alternative;
            }
        }
        throw new IllegalArgumentException();
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public List<ValueType> getStack() {
        return stack;
    }

    private record InstructionKind(ValueType output, List<ValueType> input) {

        public InstructionKind(ValueType output, ValueType... input) {
            this(output, List.of(input));
        }

    }
}
