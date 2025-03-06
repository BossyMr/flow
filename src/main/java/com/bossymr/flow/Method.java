package com.bossymr.flow;

import com.bossymr.flow.instruction.Instruction;
import com.bossymr.flow.type.ValueType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A {@code Method} represents a method. A method isn't bound to any module or scope, and as such, can be referenced by
 * any other method.
 */
public class Method {

    private final String name;
    private final MethodKind signature;
    private final List<Instruction> instructions;

    /**
     * Creates a new {@code Method}.
     * @param name the method's name
     * @param signature the method's signature
     * @param code the method's body
     */
    public Method(String name, MethodKind signature, Consumer<CodeBuilder> code) {
        this.name = name;
        this.signature = signature;
        CodeBuilder codeBuilder = new CodeBuilder(this, new ArrayList<>(signature.getArguments()));
        code.accept(codeBuilder);
        List<Instruction> block = codeBuilder.getInstructions();
        this.instructions = List.copyOf(block);
    }

    /**
     * {@return the name of this method}
     */
    public String getName() {
        return name;
    }

    /**
     * {@return the return type of this method}
     */
    public ValueType getReturnType() {
        return signature.getReturnType();
    }

    /**
     * {@return all parameters in this method}
     */
    public List<ValueType> getArguments() {
        return signature.getArguments();
    }

    /**
     * {@return all instructions in this method}
     */
    public List<Instruction> getInstructions() {
        return instructions;
    }
}
