package pl.com.bottega.photostock.sales.model;

/**
 * Created by Slawek on 12/03/16.
 */
public class Money {
    private double value;
    private String currency;

    public Money(double value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    public Money(double value) {
        this(value, "PLN");
    }

    public Money() {
        this(0);
    }
}
