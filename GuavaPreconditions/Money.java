package pl.com.bottega.photostock.sales.model.deal;


import com.google.common.base.Preconditions;
import pl.com.bottega.commons.math.model.Fraction;

import static com.google.common.base.Preconditions.checkArgument;
import static pl.com.bottega.commons.math.utilits.MathUtilits.getSymbolCount;
import static pl.com.bottega.photostock.sales.model.deal.Money.Currency.PLN;

public class Money {

    public final static Money ZERO_PL = new Money(0);
    public final static Money ONE_PL = new Money(1);
    public final static Money FIVE_PL = new Money(5);

    private Fraction value;
    private Currency currency;

    public Money(Fraction value, Currency currency) {
        validate(value, currency);
        setValue(value);
        setCurrency(currency);
    }

    public Money(double value, String currency) {
        this(value, Currency.valueOf(currency));
    }

    public Money(double value) {
        this(value, PLN);
    }

    public Money(double value, Currency currency) {
        validate(value);
        setValue(getFraction(value));
        setCurrency(currency);
    }

    public Money(int integerValue, int cents, Currency currency) {
        validate(integerValue, cents);
        setValue(getFraction(integerValue, cents));
        setCurrency(currency);
    }

    private static double getFractionPart(String digit) {
        char[] expNumber = digit.toCharArray();
        StringBuilder result = new StringBuilder();
        for (int index = expNumber.length - 1; index > 0; index--) {
            result.append(expNumber[index]);
            if (expNumber[index] == '.') {
                result.append("0");
                break;
            }
        }
        return Double.parseDouble(result.reverse().toString());
    }

    private static int getLongFractionPart(String digit) {
        char[] expNumber = digit.toCharArray();
        StringBuilder result = new StringBuilder();
        for (int index = expNumber.length - 1; index > 0; index--) {
            if (expNumber[index] == '.')
                break;
            result.append(expNumber[index]);
        }
        return result.toString().length();
    }

    private Fraction getFraction(int integerValue, int cents) {
        if (cents == 100) {
            integerValue++;
            cents = 0;
        }
        int denominator = (int) Math.pow(10, getSymbolCount(cents));
        if (cents < 10)
            denominator *= 10;
        int nominator = integerValue * denominator + cents;
        return new Fraction(nominator, denominator);
    }

    private void validateCurrency(Money value) {
       checkArgument(currency.equals(value.currency), "Forbidden! Different currency!");
    }

    private void validate(Fraction fraction, Currency currency) {
       checkArgument(fraction != null, "Forbidden! Try to use null argument!");
       checkArgument(currency != null, "Currency is null");
    }

    private void validate(int integerValue, int cents) {
        checkArgument((integerValue >= 0 && cents >= 0 && cents <= 100), "Bad value! Permissible value should be greater than 0 and cents should be less 100");
    }

    private void validate(double value) {
        checkArgument(value >= 0, "Bad value! Permissible value should be greater than 0");
    }

    private void setValue(Fraction fraction) {
        this.value = fraction;
    }

    private Fraction getFraction(double value) {
        String digit = String.valueOf(value);
        int denominator = (int) Math.pow(10, getLongFractionPart(digit));
        int nominator = (int) value * denominator + (int) (getFractionPart(digit) * denominator);
        return new Fraction(nominator, denominator);
    }

    public Money add(Money money) {
        validateCurrency(money);
        return new Money(this.value.add(money.value), currency);
    }

    public Money substract(Money money) {
        validateCurrency(money);
        return new Money(this.value.substract(money.value), currency);
    }

    public Money multiple(int ratio) {
        int resultNominator = ratio * value.getNominator();
        return new Money(new Fraction(resultNominator, value.getDenominator()), currency);
    }

    public Money multiple(double ratio) {
        Fraction fraction = getFraction(ratio);
        Fraction result = new Fraction(value.getNominator() * fraction.getNominator(), value.getDenominator() * fraction.getDenominator());
        return new Money(result, currency);
    }

    public boolean isGreaterOrEqualsThan(Money money) {
        validateCurrency(money);
        return this.value.isGreaterOrEqualsThan(money.value);
    }

    public boolean isLessOrEqualsThan(Money money) {
        validateCurrency(money);
        return this.value.isLessOrEqualsThan(money.value);
    }

    public boolean isLessThan(Money money) {
        validateCurrency(money);
        return this.value.isLessThan(money.value);
    }

    public boolean isGreaterThan(Money money) {
        validateCurrency(money);
        return this.value.isGreaterThan(money.value);
    }

    public double getPercentDifferent(Money money) {
        if (this.isGreaterThan(money)) {
            double tempNominator = 100 * (value.getNominator() * money.value.getDenominator() - value.getDenominator() * money.value.getNominator());
            double tempDenominator = value.getDenominator() * money.value.getNominator();
            return tempNominator / tempDenominator;
        } else {
            double tempNominator = 100 * (money.value.getNominator() * value.getDenominator() - value.getNominator() * money.value.getDenominator());
            double tempDenominator = money.value.getDenominator() * value.getNominator();
            return tempNominator / tempDenominator;
        }
    }

    @Override
    public String toString() {
        return "Money{" +
                "value=" + (double) value.getNominator() / value.getDenominator() +
                ", currency=" + currency +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Money money = (Money) o;

        return value.equals(money.value) && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + currency.hashCode();
        return result;
    }

    public String getPriceFraction() {
        return value.getNominator() + "/" + value.getDenominator();
    }

    public Currency getCurrency() {
        return currency;
    }

    private void setCurrency(Currency currency) {
        checkArgument(currency != null, "Currency is null");
        this.currency = currency;
    }

    public double getPriceInCents() {
        return value.getNominator() / (double) value.getDenominator();
    }

    public enum Currency {
        BYR, BDT, BIF, RUB, TND, YUM, CNY, VND, TMT, CVE, ZWN, BRL, MVR, MXV, DJF, RON, KZT, LRD, FRF, SGD,
        AZN, BGN, QAR, XFO, CDF, MUR, SRD, TJS, SLL, ESP, ERN, MZN, UZS, RUR, COP, HUF, ZAR, INR, CLP, TMM,
        BHD, MZM, HRK, MXN, AZM, KHR, NZD, NGN, SRG, VEB, JMD, PGK, NAD, XBC, ETB, XTS, GTQ, GHS, COU, CRC,
        SVC, SSP, STD, IRR, ADP, AYM, MDL, JOD, XBD, XFU, MWK, FKP, BTN, ISK, KWD, SYP, NIO, GYD, XDR, XBB,
        IDR, XBA, AUD, ZMK, BZD, ZWR, AFN, LYD, SAR, THB, KPW, OMR, GEL, AWG, BMD, NPR, PAB, GBP, LTL, UAH,
        EUR, AED, BGL, TTD, LSL, BYB, XSU, ALL, SZL, IQD, XAG, CHW, CUC, MKD, BAM, CZK, DKK, TPE, SKK, LUF,
        CHF, WST, RWF, TRY, ATS, ZWL, MMK, SHP, SBD, GHC, NOK, ROL, BOV, XPD, GNF, SOS, VUV, CLF, BBD, ILS,
        UYU, MGF, DZD, MAD, PTE, LAK, XOF, ANG, ARS, XPF, CYP, SIT, JPY, XUA, GWP, HNL, NLG, FIM, KES, AMD,
        XCD, SCR, BOB, MRO, ZWD, YER, AFA, VEF, XAF, KYD, UYI, DOP, SDG, SDD, TWD, DEM, GRD, HTG, PYG, MGA,
        LVL, HKD, KMF, TOP, BWP, BEF, USD, PLN, TZS, GIP, PHP, TRL, IEP, USS, XAU, MTL, LKR, BSD, KGS, FJD,
        XXX, ZMW, RSD, LBP, CHE, USN, KRW, PKR, MYR, XPT, BND, MOP, MNT, SEK, EEK, CUP, EGP, PEN, AOA, CSD,
        GMD, ITL, UGX, CAD
    }
}