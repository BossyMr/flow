package com.bossymr.flow.instruction;

import com.bossymr.flow.type.*;

/**
 * An {@code Operator} represents a unary operator.
 */
public enum UnaryOperator {
    NOT("!") {
        @Override
        public ValueType getType(ValueType type) {
            if (!(type instanceof BooleanType)) {
                return null;
            }
            return ValueType.booleanType();
        }
    },
    NEGATE("-") {
        @Override
        public ValueType getType(ValueType type) {
            if (!(type instanceof NumericType)) {
                return null;
            }
            return type;
        }
    },
    // TODO: Create ConvertInstruction.
    INTEGER_TO_REAL("{int -> real}") {
        @Override
        public ValueType getType(ValueType type) {
            if (!(type instanceof IntegerType)) {
                return null;
            }
            return ValueType.numericType();
        }
    },
    REAL_TO_INTEGER("{real -> int}") {
        @Override
        public ValueType getType(ValueType type) {
            if (!(type instanceof RealType)) {
                return null;
            }
            return ValueType.integerType();
        }
    };

    private final String name;

    UnaryOperator(String name) {
        this.name = name;
    }

    /**
     * Returns the type of an expression with this operator.
     *
     * @param type the type of the expression to the right of this expression.
     * @return the type of an expression with this operator, or {@code null} if this expression isn't valid.
     */
    public abstract ValueType getType(ValueType type);

    @Override
    public String toString() {
        return name;
    }
}
