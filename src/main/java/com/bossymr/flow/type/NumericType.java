package com.bossymr.flow.type;

/**
 * A {@code NumericType} represents a numeric type.
 */
public sealed interface NumericType extends ValueType permits IntegerType, RealType {}
