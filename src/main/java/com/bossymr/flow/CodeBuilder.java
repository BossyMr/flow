package com.bossymr.flow;

import com.bossymr.flow.expression.Variable;
import com.bossymr.flow.instruction.*;
import com.bossymr.flow.type.BooleanType;
import com.bossymr.flow.type.EmptyType;
import com.bossymr.flow.type.ValueType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CodeBuilder {

    private final Flow.Method method;
    private final List<ValueType> stack;
    private final List<Instruction> instructions = new ArrayList<>();

    public CodeBuilder(Flow.Method method, List<ValueType> stack) {
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

    /**
     * Adds a branch instruction to this code block.
     *
     * @param kind the type of branch
     * @param label the target
     * @return this builder
     */
    public CodeBuilder branch(BranchKind kind, Label label) {
        if (kind == BranchKind.CONDITIONALLY) {
            if (stack.isEmpty()) {
                throw new IllegalArgumentException("branch '" + kind + "': " + stack);
            }
            ValueType argument = stack.removeLast();
            if (!(argument instanceof BooleanType)) {
                throw new IllegalArgumentException("branch '" + kind + "': " + argument);
            }
        }
        instructions.add(new BranchInstruction(kind, label));
        return this;
    }

    public void conditionalJump(Label thenLabel) {
        branch(BranchKind.CONDITIONALLY, thenLabel);
    }

    public void jump(Label label) {
        branch(BranchKind.ALWAYS, label);
    }

    public CodeBuilder call(Flow.Method method) {
        ValueType returnType = method.getSignature().returnType();
        List<ValueType> arguments = method.getSignature().arguments();
        validateStack(method.getArguments().size(), List.of(new InstructionKind(returnType, arguments)));
        if (!(returnType instanceof EmptyType)) {
            stack.add(returnType);
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

    /**
     * Adds a push instruction to this code block.
     *
     * @param constant the constant
     * @return this builder
     */
    public CodeBuilder push(Constant<?> constant) {
        stack.add(constant.getType());
        instructions.add(new PushInstruction(constant));
        return this;
    }

    public CodeBuilder pushByte(byte value) {
        return push(new Constant.Integer(value));
    }

    public CodeBuilder pushInteger(int value) {
        return push(new Constant.Integer(value));
    }

    public CodeBuilder pushLong(long value) {
        return push(new Constant.Integer(value));
    }

    public CodeBuilder pushString(String value) {
        return push(new Constant.String(value));
    }

    public CodeBuilder pushBoolean(boolean value) {
        return push(new Constant.Boolean(value));
    }

    /**
     * Adds a binary instruction to this code block.
     *
     * @param operator the binary operator
     * @return this builder
     * @throws IllegalArgumentException if the stack is not valid
     */
    public CodeBuilder binary(BinaryOperator operator) {
        if (stack.size() < 2) {
            throw new IllegalArgumentException("binary operation '" + operator + "': " + stack);
        }
        List<ValueType> arguments = stack.subList(stack.size() - 2, stack.size());
        ValueType left = arguments.getFirst();
        ValueType right = arguments.getLast();
        ValueType result = operator.getType(left, right);
        if (result == null) {
            throw new IllegalArgumentException("binary operation '" + operator + "': " + arguments);
        }
        arguments.clear();
        stack.add(result);
        instructions.add(new BinaryInstruction(operator));
        return this;
    }

    public CodeBuilder add() {
        return binary(BinaryOperator.ADD);
    }

    public CodeBuilder subtract() {
        return binary(BinaryOperator.SUBTRACT);
    }

    public CodeBuilder multiply() {
        return binary(BinaryOperator.MULTIPLY);
    }

    public CodeBuilder divide() {
        return binary(BinaryOperator.DIVIDE);
    }

    public CodeBuilder modulo() {
        return binary(BinaryOperator.MODULO);
    }

    public CodeBuilder equalTo() {
        return binary(BinaryOperator.EQUAL_TO);
    }

    public CodeBuilder greaterThan() {
        return binary(BinaryOperator.GREATER_THAN);
    }

    public CodeBuilder lessThan() {
        return binary(BinaryOperator.LESS_THAN);
    }

    public CodeBuilder and() {
        return binary(BinaryOperator.AND);
    }

    public CodeBuilder xor() {
        return binary(BinaryOperator.XOR);
    }

    public CodeBuilder or() {
        return binary(BinaryOperator.OR);
    }

    /**
     * Adds a unary instruction to this code block.
     *
     * @param operator the unary operator
     * @return this builder
     * @throws IllegalArgumentException if the stack is not valid
     */
    public CodeBuilder unary(UnaryOperator operator) {
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("unary operation '" + operator + "': " + stack);
        }
        ValueType argument = stack.removeLast();
        ValueType result = operator.getType(argument);
        if (result == null) {
            throw new IllegalArgumentException("unary operation '" + operator + "': " + argument);
        }
        stack.add(result);
        instructions.add(new UnaryInstruction(operator));
        return this;
    }

    public CodeBuilder realToInteger() {
        return unary(UnaryOperator.REAL_TO_INTEGER);
    }

    public CodeBuilder integerToReal() {
        return unary(UnaryOperator.INTEGER_TO_REAL);
    }

    public CodeBuilder not() {
        return unary(UnaryOperator.NOT);
    }

    public CodeBuilder negate() {
        return unary(UnaryOperator.NEGATE);
    }

    public CodeBuilder assign(Variable variable) {
        validateStack(1, List.of(new InstructionKind(null, variable.getType())));
        instructions.add(new StoreInstruction(variable));
        return this;
    }

    public void returnValue() {
        if (!(method.getSignature().returnType() instanceof EmptyType)) {
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
