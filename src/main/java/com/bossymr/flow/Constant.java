package com.bossymr.flow;

import com.bossymr.flow.type.*;

import java.util.Objects;

public sealed interface Constant<T> {

    final class Boolean implements Constant<java.lang.Boolean> {

        private final boolean value;

        public Boolean(boolean value) {
            this.value = value;
        }

        public java.lang.Boolean getValue() {
            return value;
        }

        @Override
        public ValueType getType() {
            return ValueType.booleanType();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Boolean aBoolean = (Boolean) o;
            return value == aBoolean.value;
        }

        @Override
        public int hashCode() {
            return java.lang.Boolean.hashCode(value);
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.valueOf(value);
        }
    }

    final class Integer implements Constant<java.lang.Long> {

        private final long value;

        public Integer(long value) {
            this.value = value;
        }

        public java.lang.Long getValue() {
            return value;
        }

        @Override
        public ValueType getType() {
            return ValueType.integerType();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Integer numeric = (Integer) o;
            return Objects.equals(value, numeric.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.valueOf(value);
        }
    }

    final class Numeric implements Constant<RealType.Fraction> {

        private final RealType.Fraction value;

        public Numeric(RealType.Fraction value) {
            this.value = value;
        }

        public Numeric(long value) {
            this.value = new RealType.Fraction(value, 1);
        }

        public Numeric(long numerator, long denominator) {
            this.value = new RealType.Fraction(numerator, denominator);
        }

        public RealType.Fraction getValue() {
            return value;
        }

        @Override
        public ValueType getType() {
            return ValueType.numericType();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Numeric numeric = (Numeric) o;
            return Objects.equals(value, numeric.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.valueOf(value);
        }
    }

    final class String implements Constant<java.lang.String> {

        private final java.lang.String value;

        public String(java.lang.String value) {
            this.value = value;
        }

        public java.lang.String getValue() {
            return value;
        }

        @Override
        public ValueType getType() {
            return ValueType.stringType();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            String string = (String) o;
            return Objects.equals(value, string.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public java.lang.String toString() {
            return "\"" + value + "\"";
        }
    }

    T getValue();

    ValueType getType();
}
