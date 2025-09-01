package com.bossymr.flow;

import com.bossymr.flow.type.ValueType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The signature of a method.
 *
 * @param returnType the method's return type.
 * @param arguments the method's arguments.
 */
public record Signature(ValueType returnType, List<ValueType> arguments) {

    public Signature(ValueType returnType, ValueType... arguments) {
        this(returnType, List.of(arguments));
    }

    @Override
    public String toString() {
        String arguments = this.arguments.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",", "(", ")"));
        return arguments + ": " + returnType;
    }
}
