package pl.com.bottega.photostock.sales.model;

import java.util.Date;

/**
 * Created by Slawek on 12/03/16.
 */
public class Purchase {

    private Client owner;

    private Date createDate;

    private Picture[] items;

    public Purchase(Client owner, Picture[] items) {
        this.owner = owner;
        this.items = items;
        this.createDate = new Date();
    }
}
