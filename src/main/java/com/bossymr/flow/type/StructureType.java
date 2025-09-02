package com.bossymr.flow.type;

import io.github.cvc5.Sort;
import io.github.cvc5.TermManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A a structure with a set of values.
 */
public final class StructureType implements ValueType {

    private final List<ValueType> values;

    private Sort sort;

    /**
     * Create a new {@code StructureType}.
     *
     * @param values the values in this structure.
     */
    public StructureType(List<ValueType> values) {
        this.values = values;
    }

    /**
     * Create a new {@code StructureType}.
     *
     * @param values the values in this structure.
     */
    public StructureType(ValueType... values) {
        this(List.of(values));
    }

    /**
     * {@return the values in this structure}
     */
    public List<ValueType> getValues() {
        return values;
    }

    @Override
    public boolean isStructure() {
        return true;
    }

    @Override
    public Sort getSort(TermManager manager) {
        if (sort == null) {
            Sort[] sorts = values.stream()
                    .map(value -> value.getSort(manager))
                    .toArray(Sort[]::new);
            sort = manager.mkTupleSort(sorts);
        }
        return sort;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StructureType that = (StructureType) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(values);
    }

    @Override public String toString() {
        return "structure" + values.stream()
                .map(ValueType::toString)
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
