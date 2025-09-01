package com.bossymr.flow.type;

import io.github.cvc5.Sort;
import io.github.cvc5.TermManager;

/**
 * A {@code RealType} perfectly represents any numeric value.
 */
public final class RealType implements NumericType {

    private Sort sort;

    @Override
    public boolean isStructure() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public Sort getSort(TermManager manager) {
        if (sort != null) {
            return sort;
        }
        return sort = manager.getRealSort();
    }

    @Override
    public int hashCode() {
        return RealType.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RealType;
    }

    @Override
    public String toString() {
        return "real";
    }

    /**
     * A {@code Fraction} represents a real number.
     *
     * @param numerator the numerator.
     * @param denominator the denominator.
     */
    public record Fraction(long numerator, long denominator) {}
}
