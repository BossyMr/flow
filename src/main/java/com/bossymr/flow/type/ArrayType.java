package com.bossymr.flow.type;

import io.github.cvc5.Sort;
import io.github.cvc5.TermManager;

import java.util.Objects;

/**
 * An array of a specific type.
 */
public final class ArrayType implements ValueType {

    private final ValueType elementType;

    private Sort sort;

    /**
     * Create a new {@code ArrayType}.
     * @param elementType the element type.
     */
    public ArrayType(ValueType elementType) {
        this.elementType = elementType;
    }

    /**
     * Returns the element type of this array type.
     * @return the element type of this array type.
     */
    public ValueType getElementType() {
        return elementType;
    }
    
    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public Sort getSort(TermManager manager) {
        if (sort == null) {
            Sort integerSort = ValueType.integerType().getSort(manager);
            sort = manager.mkArraySort(integerSort, elementType.getSort(manager));
        }
        return sort;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ArrayType arrayType = (ArrayType) o;
        return Objects.equals(elementType, arrayType.elementType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(elementType);
    }

    @Override
    public String toString() {
        return "[" + elementType + "]";
    }
}
