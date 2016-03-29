package pl.com.bottega.photostock.sales.application;

import pl.com.bottega.photostock.sales.model.Client;
import pl.com.bottega.photostock.sales.model.LightBox;
import pl.com.bottega.photostock.sales.model.Picture;

/**
 * Created by Slawek on 19/03/16.
 */
public class LightBoxTestConcoleApp {
    public static void main(String[] args) {
        Client client = new Client("Pan janusz", "tajny adres", 20);

        LightBox lightBoxJanusza1 = client.createLightBox();
        LightBox lightBoxJanusza2 = client.createLightBox();
        LightBox lightBoxJanusza3 = client.createLightBox();


        Picture lumberJack = new Picture("nr1", "lumberJack", 2, null, false);
        Picture BadJack = new Picture("nr15", "lumberJack", 2, null, true);
        Picture GoodJack = new Picture("nr1", "lumberJack", 2, null, true);
        Picture MiddleJack = new Picture("nr158", "lumberJregr", 25, null, true);
        Picture HardJack = new Picture("nr15645", "lumberJack", 2, null, true);
        Picture HugeJack = new Picture("nr1", "lumberJackf", 26, null, false);
        Picture SmallJack = new Picture("nr1", "lumberJack", 24, null, true);
        Picture LiteJack = new Picture("nr1", "lumberJack", 25, null, true);
        Picture BlackJack = new Picture("nr1", "lumberJack", 27, null, true);
        Picture kitty = new Picture("nr2", "kitty", 2, null, true);
        Picture HugeJack2 = new Picture("nr1", "lumberJackf", 26, null, false);
        Picture SmallJack2 = new Picture("nr1", "lumberJack", 24, null, true);
        Picture LiteJack2 = new Picture("nr1", "lumberJack", 25, null, true);
        Picture BlackJack2 = new Picture("nr1", "lumberJack", 27, null, true);
        Picture kitty2 = new Picture("nr2", "kitty", 3, null, true);



        try {
            lightBoxJanusza1.add(lumberJack);
            lightBoxJanusza1.add(BadJack);
            lightBoxJanusza1.add(GoodJack);
            lightBoxJanusza1.add(MiddleJack);
            lightBoxJanusza1.add(HardJack);
            lightBoxJanusza1.add(HugeJack);
            lightBoxJanusza1.add(SmallJack);
            lightBoxJanusza1.add(LiteJack);
            lightBoxJanusza1.add(BlackJack);
            lightBoxJanusza1.add(kitty);


//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

            lightBoxJanusza2.add(lumberJack);
            lightBoxJanusza2.add(BadJack);
            lightBoxJanusza2.add(GoodJack);
            lightBoxJanusza2.add(MiddleJack);
            lightBoxJanusza2.add(HardJack);
            lightBoxJanusza2.add(HugeJack2);
            lightBoxJanusza2.add(SmallJack2);
            lightBoxJanusza2.add(LiteJack2);
            lightBoxJanusza2.add(BlackJack2);
            lightBoxJanusza2.add(kitty2);

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

            lightBoxJanusza3.add(lumberJack);
            lightBoxJanusza3.add(BadJack);
            lightBoxJanusza3.add(GoodJack);
            lightBoxJanusza3.add(MiddleJack);
            lightBoxJanusza3.add(HardJack);
            lightBoxJanusza3.add(HugeJack);
            lightBoxJanusza3.add(SmallJack);
            lightBoxJanusza3.add(LiteJack);
            lightBoxJanusza3.add(BlackJack);
            lightBoxJanusza3.add(kitty);

        } catch (IllegalStateException skucha) {
            System.out.println("nie udało się " + skucha.getLocalizedMessage());
        } catch (IllegalArgumentException ex) {
            //..
        } finally {//przykład, kod, który wykona się niezależnie od tego czy był wyjątek czy nie
            System.out.println("fajnie że żyjesz");
        }

        int count = lightBoxJanusza1.getItemsCount();
       System.out.println("ilosc elementow " + count);


       // System.out.println(client.printLightboxesContent());
        System.out.println((client.getCommonLightBox()).toString());


    }
}
