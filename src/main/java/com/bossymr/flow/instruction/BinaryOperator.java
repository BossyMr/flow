package com.bossymr.flow.instruction;

import com.bossymr.flow.type.*;

import java.util.List;

/**
 * An {@code Operator} represents a binary operator.
 */
public enum BinaryOperator {
    EQUAL_TO("=") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            return ValueType.booleanType();
        }
    },
    GREATER_THAN(">") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (!(left instanceof NumericType) || !left.equals(right)) {
                return null;
            }
            return ValueType.booleanType();
        }
    },
    LESS_THAN("<") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (!(left instanceof NumericType) || !left.equals(right)) {
                return null;
            }
            return ValueType.booleanType();
        }
    },
    ADD("+") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (left instanceof StringType && right instanceof StringType) {
                return left;
            }
            if (!(left instanceof NumericType) || !left.equals(right)) {
                return null;
            }
            return left;
        }
    },
    SUBTRACT("-") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (!(left instanceof NumericType) || !left.equals(right)) {
                return null;
            }
            return left;
        }
    },
    MULTIPLY("*") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (!(left instanceof NumericType) || !left.equals(right)) {
                return null;
            }
            return left;
        }
    },
    DIVIDE("/") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (!(left instanceof NumericType) || !left.equals(right)) {
                return null;
            }
            return left;
        }
    },
    MODULO("%") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (!(left instanceof IntegerType) || !(right instanceof IntegerType)) {
                return null;
            }
            return left;
        }
    },
    AND("AND") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (!(left instanceof BooleanType) || !(right instanceof BooleanType)) {
                return null;
            }
            return ValueType.booleanType();
        }
    },
    XOR("XOR") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (!(left instanceof BooleanType) || !(right instanceof BooleanType)) {
                return null;
            }
            return ValueType.booleanType();
        }
    },
    OR("OR") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (!(left instanceof BooleanType) || !(right instanceof BooleanType)) {
                return null;
            }
            return ValueType.booleanType();
        }
    };

    private final String name;

    BinaryOperator(String name) {
        this.name = name;
    }

    /**
     * Returns the type of an expression with this operator.
     *
     * @param left the type of the expression to the left of this operator.
     * @param right the type of the expression to the right of this operator.
     * @return the type of an expression with this operator, or {@code null} if the expression isn't valid.
     */
    public abstract ValueType getType(ValueType left, ValueType right);

    @Override
    public String toString() {
        return name;
    }
}
