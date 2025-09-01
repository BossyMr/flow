package com.bossymr.flow;

import com.bossymr.flow.constraint.Constraint;
import com.bossymr.flow.instruction.*;
import com.bossymr.flow.type.EmptyType;
import com.bossymr.flow.type.ValueType;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A builder for a code block.
 * <p>
 * A builder is received by a handler when creating a method.
 */
public class CodeBuilder {

    private final Flow.Method method;

    private final List<ValueType> stack;
    private final Map<Integer, ValueType> variables;

    private final List<Instruction> instructions;

    private final Label startLabel = new Label();
    private final Label endLabel = new Label();

    public CodeBuilder(Flow.Method method) {
        this.method = method;
        this.stack = new ArrayList<>();
        this.variables = new HashMap<>();
        this.instructions = method.getInstructions();
        List<ValueType> arguments = method.getSignature().arguments();
        for (int i = 0; i < arguments.size(); i++) {
            ValueType argumentType = arguments.get(i);
            variables.put(i, argumentType);
        }
    }

    public CodeBuilder(CodeBuilder parent) {
        this.method = parent.method;
        this.stack = new ArrayList<>(parent.stack);
        this.variables = new HashMap<>(parent.variables);
        this.instructions = parent.instructions;
    }

    /**
     * {@return a new unbound label}
     */
    public Label newLabel() {
        return new Label();
    }

    /**
     * {@return a new bound label}
     */
    public Label newBoundLabel() {
        Label label = new Label();
        insertLabel(label);
        return label;
    }

    /**
     * Binds the provided label to the next instruction.
     *
     * @param label the label
     * @return this builder
     */
    public CodeBuilder insertLabel(Label label) {
        instructions.add(label);
        return this;
    }

    /**
     * {@return a label associated with the beginning of the current block}
     */
    public Label startLabel() {
        return startLabel;
    }

    /**
     * {@return a label associated with the end of the current block}
     */
    public Label endLabel() {
        return endLabel;
    }

    /**
     * {@return the next available variable}
     */
    public int availableVariable() {
        for (int i = 0; ; i++) {
            if (!variables.containsKey(i)) {
                return i;
            }
        }
    }

    /**
     * Adds a block.
     *
     * @param block handler used to generate the body of the block.
     * @return this builder
     */
    public CodeBuilder block(Consumer<CodeBuilder> block) {
        CodeBuilder codeBuilder = new CodeBuilder(this);
        block.accept(codeBuilder);
        this.stack.clear();
        this.stack.addAll(codeBuilder.stack);
        return this;
    }

    /**
     * Adds a branch instruction.
     *
     * @param kind whether to branch conditionally or unconditionally
     * @param label the label to branch to
     * @return this builder
     */
    public CodeBuilder branch(BranchKind kind, Label label) {
        if (kind == BranchKind.CONDITIONALLY) {
            validateStack(new Signature(ValueType.emptyType(), ValueType.booleanType()));
        }
        // TODO: Compare the stack and variable slots at the label
        instructions.add(new BranchInstruction(kind, label));
        return this;
    }

    /**
     * Adds a conditional branch instruction.
     *
     * @param thenLabel the label to branch to
     * @return this builder
     */
    public CodeBuilder conditionalJump(Label thenLabel) {
        return branch(BranchKind.CONDITIONALLY, thenLabel);
    }

    /**
     * Adds a jump instruction.
     *
     * @param label the label to jump to
     * @return this builder
     */
    public CodeBuilder jump(Label label) {
        return branch(BranchKind.ALWAYS, label);
    }

    /**
     * Adds a method call.
     *
     * @param method the method
     * @return this builder
     */
    public CodeBuilder call(Flow.Method method) {
        validateStack(method.getSignature());
        instructions.add(new CallInstruction(method));
        return this;
    }

    /**
     * Adds an "if-then" block.
     *
     * @param thenHandler the handler used to generate the body of the then-clause
     * @return this builder
     */
    public CodeBuilder ifThen(Consumer<CodeBuilder> thenHandler) {
        Label elseLabel = newLabel();
        not().conditionalJump(elseLabel);
        CodeBuilder thenCodeBuilder = new CodeBuilder(this);
        thenHandler.accept(thenCodeBuilder);
        insertLabel(elseLabel);
        return this;
    }

    /**
     * Adds an "if-then-else" block.
     *
     * @param thenHandler the handler used to generate the body of the then-clause
     * @param elseHandler the handler used to generate the body of the else-clause
     * @return this builder
     */
    public CodeBuilder ifThenElse(Consumer<CodeBuilder> thenHandler, Consumer<CodeBuilder> elseHandler) {
        Label thenLabel = newLabel();
        Label afterLabel = newLabel();
        conditionalJump(thenLabel);
        CodeBuilder elseCodeBuilder = new CodeBuilder(this);
        elseHandler.accept(elseCodeBuilder);
        jump(afterLabel);
        insertLabel(thenLabel);
        CodeBuilder thenCodeBuilder = new CodeBuilder(this);
        thenHandler.accept(thenCodeBuilder);
        insertLabel(afterLabel);
        stack.clear();
        stack.addAll(thenCodeBuilder.getStack());
        return this;
    }

    /**
     * Adds a loop block.
     *
     * @param loopHandler the handler used to generate the body of the loop and a label to break out of the loop
     * @return this builder
     */
    public CodeBuilder loop(BiConsumer<Label, CodeBuilder> loopHandler) {
        Label breakLabel = newLabel();
        Label startLabel = newBoundLabel();
        CodeBuilder codeBuilder = new CodeBuilder(this);
        loopHandler.accept(breakLabel, codeBuilder);
        jump(startLabel);
        insertLabel(breakLabel);
        // TODO: The stack at this point should be the same as where we jump to the breakLabel
        return this;
    }

    /**
     * Duplicates the value at the top of the stack.
     *
     * @return this builder
     */
    public CodeBuilder duplicate() {
        validateStackLength(1);
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

    public CodeBuilder debugAssert() {
        return debugAssert(Constraint.ALWAYS_TRUE);
    }

    public CodeBuilder debugAssert(Constraint constraint) {
        validateStack(new Signature(ValueType.emptyType(), ValueType.booleanType()));
        instructions.add(new AssertInstruction(constraint));
        return this;
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
            throw new IllegalStateException("cannot call instruction binary(" + operator + ") with stack: " + stack);
        }
        List<ValueType> arguments = stack.subList(stack.size() - 2, stack.size());
        ValueType left = arguments.getFirst();
        ValueType right = arguments.getLast();
        ValueType result = operator.getType(left, right);
        if (result == null) {
            throw new IllegalStateException("cannot call instruction binary(" + operator + ") with stack: " + arguments);
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
            throw new IllegalStateException("cannot call instruction unary(" + operator + ") with stack: " + stack);
        }
        ValueType argument = stack.removeLast();
        ValueType result = operator.getType(argument);
        if (result == null) {
            throw new IllegalStateException("cannot call instruction unary(" + operator + ") with stack: " + argument);
        }
        stack.add(result);
        instructions.add(new UnaryInstruction(operator));
        return this;
    }

    public CodeBuilder convert(ValueType fromType, ValueType toType) {
        return unary(new UnaryOperator.Convert(fromType, toType));
    }

    public CodeBuilder not() {
        return unary(new UnaryOperator.Not());
    }

    public CodeBuilder negate() {
        return unary(new UnaryOperator.Negate());
    }

    public CodeBuilder load(int variable) {
        if (!variables.containsKey(variable)) {
            throw new IllegalArgumentException("variable '" + variable + "' not found");
        }
        ValueType valueType = variables.remove(variable);
        instructions.add(new LoadInstruction(variable));
        stack.add(valueType);
        return this;
    }

    public CodeBuilder store(int variable) {
        ValueType valueType = stack.removeLast();
        instructions.add(new StoreInstruction(variable));
        variables.put(variable, valueType);
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
            throw new IllegalArgumentException("cannot call instruction with stack: " + stack);
        }
    }

    private Signature validateStack(Signature... instructions) {
        List<ValueType> longest = new ArrayList<>();
        for (Signature instruction : instructions) {
            List<ValueType> expected = instruction.arguments();
            if (stack.size() < expected.size()) {
                continue;
            }
            List<ValueType> actual = stack.subList(stack.size() - expected.size(), stack.size());
            if (actual.size() > longest.size()) {
                longest = actual;
            }
            if (expected.equals(actual)) {
                actual.clear();
                if (!(instruction.returnType() instanceof EmptyType)) {
                    stack.add(instruction.returnType());
                }
                return instruction;
            }
        }
        throw new IllegalStateException("cannot call instructions '" + Arrays.toString(instructions) + "' with stack: " + longest);
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public List<ValueType> getStack() {
        return stack;
    }
}
