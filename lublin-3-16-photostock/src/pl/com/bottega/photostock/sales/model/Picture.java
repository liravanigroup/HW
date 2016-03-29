package pl.com.bottega.photostock.sales.model;

import java.util.Arrays;

/**
 * Created by Slawek on 12/03/16.
 */
public class Picture {

    private String number;
    private String name;
    private double price;
    private String[] tags;
    private boolean isAvailabe;

    public Picture(String number, String name, double price, String[] tags, boolean isAvailabe) {
        this.number = number;
        this.name = name;
        this.price = price;
        this.tags = tags;
        this.isAvailabe = isAvailabe;
    }

    public double calculatePrice(){
        return 0; //TODO dodac alg wyliczania
    }

    public void reservedPer(Client reservingClient) {

    }

    public void unreservedPer(Client unreservigClient){

    }

    public String getName() {
        return name;
    }

    public boolean isAvailable(){
        return isAvailabe;

    }

    public void cancel() {
        isAvailabe = false;
    }

    public String getNumber() {
        return number;
    }


    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Picture picture = (Picture) o;

        if (Double.compare(picture.price, price) != 0) return false;
        if (isAvailabe != picture.isAvailabe) return false;
        if (!number.equals(picture.number)) return false;
        if (!name.equals(picture.name)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(tags, picture.tags);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = number.hashCode();
        result = 31 * result + name.hashCode();
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + Arrays.hashCode(tags);
        result = 31 * result + (isAvailabe ? 1 : 0);
        return result;
    }
}
