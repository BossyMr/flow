package com.bossymr.flow.expression;

import com.bossymr.flow.instruction.UnaryOperator;
import com.bossymr.flow.type.*;

import java.util.function.Function;

/**
 * A {@code UnaryExpression} represents a unary expression.
 */
public final class UnaryExpression implements Expression {

    private final UnaryOperator operator;
    private final ValueType type;
    private final Expression expression;

    /**
     * Create a new {@code UnaryExpression}.
     *
     * @param operator the operator.
     * @param expression the expression.
     */
    public UnaryExpression(UnaryOperator operator, Expression expression) {
        ValueType type = operator.getType(expression.getType());
        if (type == null) {
            throw new IllegalArgumentException("unary expression '" + operator + " " + expression + "' is not valid");
        }
        this.operator = operator;
        this.type = type;
        this.expression = expression;
    }

    @Override
    public ValueType getType() {
        return type;
    }

    @Override
    public Expression translate(Function<Expression, Expression> mapper) {
        Expression self = mapper.apply(this);
        if (self != this) {
            return self;
        }
        Expression expression = mapper.apply(this.expression);
        if (expression != this.expression) {
            return new UnaryExpression(operator, expression);
        }
        return this;
    }

    /**
     * Returns the operator of this expression.
     *
     * @return the operator of this expression.
     */
    public UnaryOperator getOperator() {
        return operator;
    }

    /**
     * Returns the expression to the right of this expression.
     *
     * @return the expression to the right of this expression.
     */
    public Expression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "(" + getOperator() +  " " + getExpression()  + ")";
    }

}
