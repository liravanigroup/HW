package pl.com.bottega.photostock.sales.model;

/**
 * Created by Slawek on 12/03/16.
 */
public class Reservation {

    private Client owner;
    private Picture[] items;

    public Reservation(Client owner) {
        this.owner = owner;
    }

    public void add(Picture picture){

    }

    public void remove(Picture picture){

    }

    public Offer generateOffer(){
        return null;
    }

    public int getItemsCount() {
        return 0; //TODO
    }
}
