package com.bossymr.flow.instruction;

import com.bossymr.flow.type.ValueType;
import io.github.cvc5.Kind;
import io.github.cvc5.Op;
import io.github.cvc5.TermManager;

/**
 * An {@code Operator} represents a binary operator.
 */
public enum BinaryOperator {
    EQUAL_TO("=") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            return ValueType.booleanType();
        }

        @Override
        public Op convert(TermManager manager) {
            return manager.mkOp(Kind.EQUAL);
        }
    },
    GREATER_THAN(">") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (!(left == ValueType.realType() || left == ValueType.integerType()) || left != right) {
                return null;
            }
            return ValueType.booleanType();
        }

        @Override
        public Op convert(TermManager manager) {
            return manager.mkOp(Kind.GT);
        }
    },
    LESS_THAN("<") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (!(left == ValueType.realType() || left == ValueType.integerType()) || left != right) {
                return null;
            }
            return ValueType.booleanType();
        }

        @Override
        public Op convert(TermManager manager) {
            return manager.mkOp(Kind.LT);
        }
    },
    ADD("+") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (left == ValueType.stringType() && right == ValueType.stringType()) {
                return left;
            }
            if (!(left == ValueType.realType() || left == ValueType.integerType()) || left != right) {
                return null;
            }
            return left;
        }

        @Override
        public Op convert(TermManager manager) {
            return manager.mkOp(Kind.ADD);
        }
    },
    SUBTRACT("-") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (!(left == ValueType.realType() || left == ValueType.integerType()) || left != right) {
                return null;
            }
            return left;
        }

        @Override
        public Op convert(TermManager manager) {
            return manager.mkOp(Kind.SUB);
        }
    },
    MULTIPLY("*") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (!(left == ValueType.realType() || left == ValueType.integerType()) || left != right) {
                return null;
            }
            return left;
        }

        @Override
        public Op convert(TermManager manager) {
            return manager.mkOp(Kind.MULT);
        }
    },
    DIVIDE("/") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (!(left == ValueType.realType() || left == ValueType.integerType()) || left != right) {
                return null;
            }
            return left;
        }

        @Override
        public Op convert(TermManager manager) {
            return manager.mkOp(Kind.DIVISION);
        }
    },
    MODULO("%") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (left != ValueType.integerType() || right != ValueType.integerType()) {
                return null;
            }
            return left;
        }

        @Override
        public Op convert(TermManager manager) {
            return manager.mkOp(Kind.INTS_MODULUS);
        }
    },
    AND("AND") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (left != ValueType.booleanType() || right != ValueType.booleanType()) {
                return null;
            }
            return ValueType.booleanType();
        }

        @Override
        public Op convert(TermManager manager) {
            return manager.mkOp(Kind.AND);
        }
    },
    XOR("XOR") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (left != ValueType.booleanType() || right != ValueType.booleanType()) {
                return null;
            }
            return ValueType.booleanType();
        }

        @Override
        public Op convert(TermManager manager) {
            return manager.mkOp(Kind.XOR);
        }
    },
    OR("OR") {
        @Override
        public ValueType getType(ValueType left, ValueType right) {
            if (left != ValueType.booleanType() || right != ValueType.booleanType()) {
                return null;
            }
            return ValueType.booleanType();
        }

        @Override
        public Op convert(TermManager manager) {
            return manager.mkOp(Kind.OR);
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

    public abstract Op convert(TermManager manager);

    @Override
    public String toString() {
        return name;
    }
}
