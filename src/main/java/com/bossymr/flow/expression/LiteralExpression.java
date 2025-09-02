package com.bossymr.flow.expression;

import com.bossymr.flow.Constant;
import com.bossymr.flow.type.ValueType;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;

import java.util.function.Function;

/**
 * A {@code LiteralExpression} represents a literal value.
 */
public final class LiteralExpression implements Expression {

    private final Constant<?> constant;

    private Term term;

    public LiteralExpression(Constant<?> constant) {
        this.constant = constant;
    }

    /**
     * Create a new {@code LiteralExpression} with the provided boolean value.
     *
     * @param value the value.
     * @return a new {@code LiteralExpression}.
     */
    public static LiteralExpression booleanLiteral(boolean value) {
        return new LiteralExpression(new Constant.Boolean(value));
    }

    /**
     * Create a new {@code LiteralExpression} with the provided string value.
     *
     * @param value the value.
     * @return a new {@code LiteralExpression}.
     */
    public static LiteralExpression stringLiteral(String value) {
        return new LiteralExpression(new Constant.String(value));
    }

    /**
     * Create a new {@code LiteralExpression} with the provided numeric value.
     *
     * @param value the value.
     * @return a new {@code LiteralExpression}.
     */
    public static LiteralExpression numericLiteral(long value) {
        return new LiteralExpression(new Constant.Real(value));
    }

    /**
     * Create a new {@code LiteralExpression} with the provided numeric value.
     *
     * @param numerator the numerator.
     * @param denominator the denominator.
     * @return a new {@code LiteralExpression}.
     */
    public static LiteralExpression numericLiteral(long numerator, long denominator) {
        return new LiteralExpression(new Constant.Real(numerator, denominator));
    }

    /**
     * Create a new {@code LiteralExpression} with the provided integer value.
     *
     * @param value the value.
     * @return a new {@code LiteralExpression}.
     */
    public static LiteralExpression integerLiteral(long value) {
        return new LiteralExpression(new Constant.Integer(value));
    }

    @Override
    public ValueType getType() {
        return constant.getType();
    }

    @Override
    public Expression translate(Function<Expression, Expression> mapper) {
        return mapper.apply(this);
    }

    /**
     * Returns the literal value of this expression.
     *
     * @return the literal value of this expression.
     */
    public Object getValue() {
        return constant.getValue();
    }

    @Override
    public Term convert(TermManager manager) {
        if (term != null) {
            return term;
        }
        return term = constant.convert(manager);
    }

    @Override
    public String toString() {
        return constant.toString();
    }
}
