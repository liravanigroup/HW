package pl.com.bottega.photostock.sales.application;


import pl.com.bottega.photostock.sales.model.*;

/**
 * Created by Slawek on 12/03/16.
 */
public class ConcoleApplication {
    
    public static void main(String[] args){

        //=====symulcja dostępu do bazy danych===================

        Client client = new Client("Kowalski", "ul x", 100); //klient posiada 100 creditów

        //zdjęcia kosztują w sumie 17 creditów

        Picture pic1 = new Picture(
                "nr1", "cat", 2, new String[]{"piła", "drewno"}, false);
        Picture pic2 = new Picture(
                "nr2", "dog", 5, new String[]{"pies", "niebo"}, true);
        Picture pic3 = new Picture(
                "nr3", "at", 10, new String[]{"ford", "mustang"}, true);

        Reservation reservation = new Reservation(client);

        //========================================

        reservation.add(pic1);
        reservation.add(pic2);
        reservation.add(pic3);

        //sytuacja niedozwolona - ponowne dodanie tych samych pozycji nie powinno być możliwe (ale metoda add jeszcze tego nie obsługuje)
        reservation.add(pic1);
        reservation.add(pic2);
        reservation.add(pic3);

        int count = reservation.getItemsCount();//jeszcze nie działa
        System.out.println("Ilość pozycji rezerwacji: " + count); //powinny być tylko 2 (bo jedno zdjęcie jest nidostępne), pomimo sześciu dodań

        //symulacja tego, że jakieś zdjęcie wycofano z handlu
        pic2.cancel();

        Offer offer = reservation.generateOffer();//jeszcze nie działa, bo generateOffer zwraca null
        count = offer.getItemsCount();
        System.out.println("Ilość pozycji oferty: " + count); //powinny być 1, bo już d zdjęcia są niedostępne


        double offerTotalCost = offer.getTotalCost();
        boolean canAfford = client.canAfford(offerTotalCost);

        if (canAfford){
            client.charge(offerTotalCost, "Za zdjęcia");
            Picture[] items = offer.getItems();
            Purchase purchase = new Purchase(client, items);
        }
        else{
            System.out.println("Can not afford! You need " + offerTotalCost);
        }


    }
}
