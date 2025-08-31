package com.bossymr.flow.expression;

import com.bossymr.flow.type.ValueType;

import java.util.function.Function;

public final class AnyExpression implements Expression {

    private final ValueType valueType;

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
}
