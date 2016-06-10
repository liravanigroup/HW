package pl.com.bottega.photostock.sales.api;


import pl.com.bottega.photostock.sales.infrastructure.repositories.AbstractRepositoryFactory;
import pl.com.bottega.photostock.sales.infrastructure.repositories.Repository;
import pl.com.bottega.photostock.sales.infrastructure.repositories.RepositoryFactory;
import pl.com.bottega.photostock.sales.infrastructure.repositories.ReservationRepository;
import pl.com.bottega.photostock.sales.model.deal.Offer;
import pl.com.bottega.photostock.sales.model.deal.Purchase;
import pl.com.bottega.photostock.sales.model.products.Product;
import pl.com.bottega.photostock.sales.model.users.Client;
import pl.com.bottega.photostock.sales.model.usertool.Reservation;

import static pl.com.bottega.photostock.sales.infrastructure.repositories.RepositoryType.FAKE_REPOSITORY;

public class PurchaseProcess {

    private RepositoryFactory repositoryFactory = AbstractRepositoryFactory.getRepositoryByType(FAKE_REPOSITORY);
    private Repository clientRepository = repositoryFactory.getClientRepository();
    private Repository reservationRepository = repositoryFactory.getReservationRepository();
    private Repository purchaseRepository = repositoryFactory.getPurchaseRepository();
    private Repository productRepository = repositoryFactory.getProductRepository();

    private Reservation createReservation(String clientNumber) {
        Client client = (Client) clientRepository.load(clientNumber);
        Reservation reservation = new Reservation(client);
        reservationRepository.save(reservation);
        return reservation;
    }

    public void add(String clientNumber, String productNumber) {
        Client client = (Client) clientRepository.load(clientNumber);
        Reservation reservation = ReservationRepository.findOpenPer(client);
        if (reservation == null)
            reservation = createReservation(clientNumber);
        Product product = (Product) productRepository.load(productNumber);
        reservation.add(product);
        reservationRepository.save(reservation);
        productRepository.save(product);
    }

    public Offer calculateOffer(String clientNumber) {
        Client client = (Client) clientRepository.load(clientNumber);
        Reservation reservation = (Reservation) reservationRepository.load(clientNumber);
        if (reservation == null)
            throw new IllegalStateException("Client does not have opened reservation");
        return reservation.generateOffer();
    }


    public void confirm(String reservationNumber, String payerNumber){
        Reservation reservation = (Reservation) reservationRepository.load(reservationNumber);
        Client payer = (Client) clientRepository.load(payerNumber);
        confirm(payer, reservation);
    }

    private void confirm(Client payer, Reservation reservation){
        Offer offer = reservation.generateOffer();
        if (payer.canAfford(offer.getTotalCost())){
            payer.charge(offer.getTotalCost(), "For reservation number: " + reservation.getNumber());

            Purchase purchase = new Purchase(payer, offer.getItems());

            reservation.close();

            purchaseRepository.save(purchase);
            clientRepository.save(payer);
        }
        else{
            throw new IllegalStateException("Client can not afford " + offer.getTotalCost());
        }
    }

    public void confirm(String clientNr){
        Client client = (Client) clientRepository.load(clientNr);
        Reservation reservation = reservationRepository.findOpenedPer(client);
        if (reservation == null)
            throw new IllegalStateException("client does not have opened reservation");
        confirm(client, reservation);
    }
}
