package pl.com.bottega.photostock.sales.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Slawek on 12/03/16.
 */
public class Client {

    private String name;
    private String address;
    private boolean isVip;
    private double debt;
    private double amount;
    private double creditLimit;
    private boolean active = true;
    private List<LightBox> lightboxes = new ArrayList();

    public Client(String name, String address, boolean isVip, double debt, double amount, double creditLimit) {
        this.name = name;
        this.address = address;
        this.isVip = isVip;
        this.debt = debt;
        this.amount = amount;
        this.creditLimit = creditLimit;
    }

    public Client(String name, String address, double creditLimit) {
        this(name, address, false, 0, 0, creditLimit);
    }

    public void addLightBox(LightBox lightBoxToAdd) {
        lightboxes.add(lightBoxToAdd);
    }

    public String printLightboxesContent() {
        StringBuilder s = new StringBuilder();
        for (LightBox lbx : lightboxes) {
            s.append(lbx.toString());
        }
        return s.toString();
    }

    public LightBox getCommonLightBox(){
        Set<Picture> uniqPictLB = new HashSet<Picture>();
        LightBox result = new LightBox(this);
        for (LightBox lbx : lightboxes) {
            for (Picture pic : lbx.getPictures()) {
                if(!uniqPictLB.contains(pic))
                    uniqPictLB.add(pic);
            }
        }
        for (Picture pic : uniqPictLB) {
            result.add(pic);
        }
        return result;
    }



    public LightBox createLightBox() {
        LightBox lbx = new LightBox(this);
        addLightBox(lbx);
        return lbx;
    }

    public boolean canAfford(double price) {
        //jeżeli jest VIP
        //saldo + limit >= amount
        //nie jest VIP
        //saldo >= amount

        if (isVip) {
            double purchasePotential = this.amount + (this.creditLimit - this.debt);
            return purchasePotential >= price;
        } else {
            return this.amount >= price;
        }
    }

    public String getName() {
        return name;
    }

    public void charge(double price, String cause) {
        if (canAfford(price)) {
            this.amount -= price;
            if (this.amount < 0) {
                this.debt -= amount;
                this.amount = 0;
            }
        }
    }

    public void recharge(double quantity) {
        this.debt -= quantity;
        if (this.debt < 0) {
            this.amount -= this.debt;
            this.debt = 0;
        }
    }

    public double getSaldo() {
        return amount - debt;
    }

    public boolean isActive() {
        return active;
    }
}