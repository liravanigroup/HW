package pl.com.bottega.photostock.sales.model;

/**
 * Created by Slawek on 12/03/16.
 */
public class Offer {

    private Picture[] items;

    public Offer(Picture[] items){
        this.items = items;
    }

    public double getTotalCost() {
        return 0;//TODO
    }

    public int getItemsCount(){
        return 0; //TODO
    }

    public Picture[] getItems() {
        return items;
    }
}
