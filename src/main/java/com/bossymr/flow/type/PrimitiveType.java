package com.bossymr.flow.type;

import io.github.cvc5.Sort;
import io.github.cvc5.TermManager;

import java.util.function.Function;

///  A primitive type.
public enum PrimitiveType implements ValueType {
    ///  A boolean type.
    BOOLEAN(TermManager::getBooleanSort),
    ///  An integer type.
    INTEGER(TermManager::getIntegerSort),
    ///  A real type.
    REAL(TermManager::getRealSort),
    ///  A string type.
    STRING(TermManager::getStringSort),
    EMPTY(manager -> {
        throw new IllegalStateException("cannot create an object with empty type");
    }) {
        @Override
        public boolean isEmpty() {
            return true;
        }
    };

    private final Function<TermManager, Sort> computeSort;

    PrimitiveType(Function<TermManager, Sort> computeSort) {
        this.computeSort = computeSort;
    }

    @Override
    public Sort getSort(TermManager manager) {
        return computeSort.apply(manager);
    }
}
