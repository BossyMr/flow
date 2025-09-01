package com.bossymr.flow.constraint;

/**
 * The possible values of a boolean expression.
 */
public enum Constraint {
    /**
     * The expression is either {@code true} or {@code false}.
     */
    ANY_VALUE,

    /**
     * The expression is always {@code false}.
     */
    ALWAYS_FALSE,

    /**
     * The expression is always {@code true}.
     */
    ALWAYS_TRUE,

    /**
     * The expression is not reachable.
     * <p>
     * This will be returned if you try to determine the value of an expression where it can't be either {@code true} or
     * {@code false}.
     *
     * <pre>{@code
     *  if (variable && !variable) {
     *      // variable is NO_VALUE
     *  }
     * }</pre>
     */
    NO_VALUE,

    /**
     * The expression can have any value.
     * <p>
     * As opposed to {@link Constraint#ANY_VALUE}, the value of the expression couldn't be determined.
     */
    UNKNOWN,
}
