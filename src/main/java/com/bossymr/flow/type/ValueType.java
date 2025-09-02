package com.bossymr.flow.type;

import io.github.cvc5.Sort;
import io.github.cvc5.TermManager;

/**
 * A {@code ValueType} represents the type of an expression or value.
 */
public sealed interface ValueType permits ArrayType, PrimitiveType, StructureType {

    /**
     * Returns an empty type.
     *
     * @return an empty type.
     */
    static ValueType emptyType() {
        return PrimitiveType.EMPTY;
    }

    /**
     * Returns a boolean type.
     *
     * @return a boolean type.
     */
    static ValueType booleanType() {
        return PrimitiveType.BOOLEAN;
    }

    /**
     * Returns a string type.
     *
     * @return a string type.
     */
    static ValueType stringType() {
        return PrimitiveType.STRING;
    }

    /**
     * Returns a numeric type.
     *
     * @return a numeric type.
     */
    static ValueType realType() {
        return PrimitiveType.REAL;
    }

    /**
     * Returns an integer type.
     *
     * @return an integer type.
     */
    static ValueType integerType() {
        return PrimitiveType.INTEGER;
    }

    /**
     * Checks whether this type represents a structure.
     *
     * @return whether this type represents a structure.
     */
    default boolean isStructure() {
        return false;
    }

    /**
     * Checks whether this type represents an array.
     *
     * @return whether this type represents an array.
     */
    default boolean isArray() {
        return false;
    }

    /**
     * Checks whether this type represents an empty type.
     *
     * @return whether this type represents an empty type.
     */
    default boolean isEmpty() {
        return false;
    }

    /**
     * Create a type representing an array of this type.
     *
     * @return a type representing an array of a type.
     */
    default ArrayType createArrayType() {
        return new ArrayType(this);
    }

    Sort getSort(TermManager manager);
}
