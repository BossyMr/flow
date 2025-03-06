package com.bossymr.flow;

import com.bossymr.flow.type.ValueType;

import java.util.List;
import java.util.Objects;

/**
 * A {@code MethodKind} represents a method signature.
 */
public class MethodKind {

    private final ValueType returnType;
    private final List<ValueType> arguments;

    private MethodKind(ValueType returnType, List<ValueType> arguments) {
        this.returnType = returnType;
        this.arguments = arguments;
    }

    /**
     * Create a new {@code MethodKind}.
     *
     * @param returnType the method's return type.
     * @param arguments the method's arguments.
     * @return a new {@code MethodKind}.
     */
    public static MethodKind of(ValueType returnType, List<ValueType> arguments) {
        return new MethodKind(returnType, arguments);
    }

    /**
     * Create a new {@code MethodKind}.
     *
     * @param returnType the method's return type.
     * @param arguments the method's arguments.
     * @return a new {@code MethodKind}.
     */
    public static MethodKind of(ValueType returnType, ValueType... arguments) {
        return new MethodKind(returnType, List.of(arguments));
    }

    /**
     * {@return the method's return type}
     */
    public ValueType getReturnType() {
        return returnType;
    }

    /**
     * {@return the method's arguments}
     */
    public List<ValueType> getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MethodKind that = (MethodKind) o;
        return Objects.equals(returnType, that.returnType) && arguments.equals(that.arguments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(returnType);
        result = 31 * result + arguments.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MethodKind{" +
                "returnType=" + returnType +
                ", arguments=" + arguments +
                '}';
    }
}
