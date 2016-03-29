package pl.com.bottega.photostock.sales.application;

import pl.com.bottega.photostock.sales.model.Client;

/**
 * Created by Slawek on 13/03/16.
 */
public class ClientTestConsoleApp{
    public static void main(String[] args) {
        Client panJanusz = new Client("Janusz", "ksiezyc", false, 4, 10, 20);
        panJanusz.recharge(10);
        if (panJanusz.canAfford(12)) {
            panJanusz.charge(12, "za coś");
            System.out.println("Saldo: " + panJanusz.getSaldo());
        }
        else{
            System.out.println("can not afford!");
        }
    }
}
