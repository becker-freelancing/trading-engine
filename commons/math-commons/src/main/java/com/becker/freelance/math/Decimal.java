package com.becker.freelance.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class Decimal extends BigDecimal {

    public static final Decimal ZERO = new Decimal(BigDecimal.ZERO);
    public static final Decimal ONE = new Decimal(BigDecimal.ONE);
    public static final Decimal TWO = new Decimal("2");
    public static final Decimal TEN = new Decimal(BigDecimal.TEN);
    public static final Decimal DOUBLE_MAX = new Decimal(Double.MAX_VALUE);

    public static Decimal valueOf(double val) {
        return new Decimal(val);
    }

    public static Decimal valueOf(long val) {
        return new Decimal(val);
    }

    public static Decimal exp(Decimal exponent) {
        return Decimal.valueOf(Math.exp(exponent.doubleValue()));
    }

    public Decimal(String val) {
        super(val);
    }

    public Decimal(double val) {
        super(String.valueOf(val));
    }

    public Decimal(BigInteger val) {
        super(val);
    }

    public Decimal(int val) {
        super(val);
    }

    public Decimal(long val) {
        super(val);
    }

    public Decimal(long val, MathContext mc) {
        super(val, mc);
    }

    public Decimal(BigDecimal bigDecimal) {
        this(bigDecimal.toPlainString());
    }

    @Override
    public Decimal add(BigDecimal augend) {
        return new Decimal(super.add(augend));
    }

    public Decimal add(double augend) {
        return add(BigDecimal.valueOf(augend));
    }

    @Override
    public Decimal multiply(BigDecimal multiplicand) {
        return new Decimal(super.multiply(multiplicand));
    }

    @Override
    public Decimal divide(BigDecimal divisor) {
        if (isEqualToZero()){
            return this;
        }
        return new Decimal(super.divide(divisor, getMathContext(5)));
    }

    public boolean isGreaterThan(BigDecimal other){
        return this.compareTo(other) > 0;
    }

    public boolean isLessThan(BigDecimal other){
        return this.compareTo(other) < 0;
    }


    public boolean isLessThan(Double other){
        return isLessThan(new Decimal(other));
    }

    public boolean isGreaterThanOrEqualTo(BigDecimal other){
        return isGreaterThan(other) || isEqualTo(other);
    }

    public boolean isGreaterThanOrEqualTo(double other){
        return isGreaterThan(other) || isEqualTo(other);
    }

    public boolean isLessThanOrEqualTo(BigDecimal other){
        return isLessThan(other) || isEqualTo(other);
    }

    public boolean isEqualTo(BigDecimal other){
        return this.compareTo(other) == 0;
    }


    public boolean isEqualTo(double other){
        return this.compareTo(new Decimal(other)) == 0;
    }

    public boolean isEqualToZero(){
        return isEqualTo(BigDecimal.ZERO);
    }

    public boolean isGreaterThanZero(){
        return isGreaterThan(BigDecimal.ZERO);
    }

    public boolean isLessThanZero(){
        return isLessThan(BigDecimal.ZERO);
    }

    @Override
    public Decimal abs() {
        return new Decimal(super.abs());
    }

    public Decimal round(int precision){
        if (isEqualToZero()){
            return this;
        }
        return new Decimal(setScale(precision, RoundingMode.HALF_UP));
    }

    private MathContext getMathContext(int fractionalPlaces) {
        return new MathContext(precision() - scale() + fractionalPlaces);
    }

    @Override
    public Decimal subtract(BigDecimal subtrahend) {
        return new Decimal(super.subtract(subtrahend));
    }

    public boolean isLessThanOrEqualTo(double other) {
        return this.doubleValue() <= other;
    }

    public boolean isGreaterThan(double lowValue) {
        return isGreaterThan(new Decimal(lowValue));
    }

    public Decimal subtract(double subtrahend) {
        return subtract(new Decimal(subtrahend));
    }

    public Decimal add(int add) {
        return add(new BigDecimal(add));
    }

    @Override
    public Decimal negate() {
        return new Decimal(super.negate());
    }

    public Decimal multiply(int i) {
        return multiply(new Decimal(i));
    }

    @Override
    public Decimal max(BigDecimal val) {
        return new Decimal(super.max(val));
    }

    @Override
    public Decimal min(BigDecimal val) {
        return new Decimal(super.min(val));
    }

    @Override
    public Decimal round(MathContext mc) {
        return new Decimal(super.round(mc));
    }

    @Override
    public Decimal setScale(int newScale, RoundingMode roundingMode) {
        return new Decimal(super.setScale(newScale, roundingMode));
    }
}
