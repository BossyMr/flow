package com.bossymr.flow.expression;

import com.bossymr.flow.type.ValueType;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;

import java.util.function.Function;

public final class AnyExpression implements Expression {

    private final ValueType valueType;

    private Term term;

    public AnyExpression(ValueType valueType) {
        this.valueType = valueType;
    }

    @Override
    public ValueType getType() {
        return valueType;
    }

    @Override
    public Expression translate(Function<Expression, Expression> mapper) {
        return mapper.apply(this);
    }

    @Override
    public Term convert(TermManager manager) {
        if (term != null) {
            return term;
        }
        return term = manager.mkConst(valueType.getSort(manager));
    }

    @Override
    public String toString() {
        return "any(" + valueType + ")";
    }
}
