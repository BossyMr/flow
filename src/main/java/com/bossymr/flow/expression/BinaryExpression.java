package com.bossymr.flow.expression;

import com.bossymr.flow.instruction.BinaryOperator;
import com.bossymr.flow.type.ValueType;
import io.github.cvc5.Op;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;

import java.util.function.Function;

/**
 * A {@code BinaryExpression} represents a binary expression.
 */
public final class BinaryExpression implements Expression {

    private final BinaryOperator operator;
    private final ValueType type;
    private final Expression left;
    private final Expression right;

    private Term term;

    /**
     * Create a new {@code BinaryExpression}.
     *
     * @param operator the operator.
     * @param left the expression to the left of the operator.
     * @param right the expression to the right of the operator.
     * @throws IllegalArgumentException if the expression is not valid.
     */
    public BinaryExpression(BinaryOperator operator, Expression left, Expression right) {
        ValueType type = operator.getType(left.getType(), right.getType());
        if (type == null) {
            throw new IllegalArgumentException("binary expression '" + left + " " + operator + " " + right + "' is not valid");
        }
        this.operator = operator;
        this.type = type;
        this.left = left;
        this.right = right;
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
        Expression left = mapper.apply(this.left);
        Expression right = mapper.apply(this.right);
        if (left != this.left || right != this.right) {
            return new BinaryExpression(operator, left, right);
        }
        return this;
    }

    /**
     * Returns the operator of this expression.
     *
     * @return the operator of this expression.
     */
    public BinaryOperator getOperator() {
        return operator;
    }

    /**
     * Returns the expression to the left of the operator.
     *
     * @return the expression to the left of the operator.
     */
    public Expression getLeft() {
        return left;
    }

    /**
     * Returns the expression to the right of the operator.
     *
     * @return the expression to the right of the operator.
     */
    public Expression getRight() {
        return right;
    }

    @Override
    public Term convert(TermManager manager) {
        if (term != null) {
            return term;
        }
        Term left = getLeft().convert(manager);
        Term right = getRight().convert(manager);
        Op operator = getOperator().convert(manager);
        return term = manager.mkTerm(operator, left, right);
    }

    @Override
    public String toString() {
        return "(" + getLeft() + " " + getOperator() + " " + getRight() + ")";
    }

}
