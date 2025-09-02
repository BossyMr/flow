package com.bossymr.flow.instruction;

import com.bossymr.flow.type.ValueType;
import io.github.cvc5.Kind;
import io.github.cvc5.Op;
import io.github.cvc5.TermManager;

/**
 * A unary operation that can be applied to an expression.
 */
public interface UnaryOperator {

    /**
     * Checks whether this operator can be applied to an expression of the provided type.
     *
     * @param type the type of the expression to be applied to this operator
     * @return the type of the result of applying this operator, or {@code null} if this expression isn't valid.
     */
    ValueType getType(ValueType type);

    Op convert(TermManager manager);

    /**
     * Logically negates a boolean value.
     */
    final class Not implements UnaryOperator {
        @Override
        public ValueType getType(ValueType type) {
            if (type != ValueType.booleanType()) {
                return null;
            }
            return type;
        }

        @Override
        public Op convert(TermManager manager) {
            return manager.mkOp(Kind.NOT);
        }

        @Override
        public String toString() {
            return "!";
        }
    }

    /**
     * Negates a numerical value.
     */
    final class Negate implements UnaryOperator {
        @Override
        public ValueType getType(ValueType type) {
            if (!(type == ValueType.realType() || type == ValueType.integerType())) {
                return null;
            }
            return type;
        }

        @Override
        public Op convert(TermManager manager) {
            return manager.mkOp(Kind.NEG);
        }

        @Override
        public String toString() {
            return "-";
        }
    }

    /**
     * Converts a value from one type to another type.
     */
    final class Convert implements UnaryOperator {

        private final ValueType fromType;
        private final ValueType toType;

        /**
         * Create a new operator to convert a value from one type to another type.
         *
         * @param fromType the type to convert from
         * @param toType the type to convert to
         */
        public Convert(ValueType fromType, ValueType toType) {
            if (!(fromType == ValueType.integerType() || fromType == ValueType.realType()) || !(toType == ValueType.integerType() || toType == ValueType.realType())) {
                throw new IllegalArgumentException("convert " + fromType + " -> " + toType);
            }
            this.fromType = fromType;
            this.toType = toType;
        }

        /**
         * {@return the type to convert from}
         */
        public ValueType getFromType() {
            return fromType;
        }

        /**
         * {@return the type to convert to}
         */
        public ValueType getToType() {
            return toType;
        }

        @Override
        public ValueType getType(ValueType type) {
            if (!type.equals(fromType)) {
                return null;
            }
            return toType;
        }

        @Override
        public Op convert(TermManager manager) {
            if (getToType() == ValueType.realType()) {
                return manager.mkOp(Kind.TO_REAL);
            }
            if (getToType() == ValueType.integerType()) {
                return manager.mkOp(Kind.TO_INTEGER);
            }
            throw new IllegalStateException("convert " + getFromType() + " -> " + toType);
        }

        @Override
        public String toString() {
            return "convert(" + fromType + " -> " + toType + ")";
        }
    }
}
