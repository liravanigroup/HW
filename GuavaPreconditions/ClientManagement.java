package pl.com.bottega.photostock.sales.api;

import com.google.common.base.Preconditions;
import pl.com.bottega.photostock.sales.infrastructure.repositories.AbstractRepositoryFactory;
import pl.com.bottega.photostock.sales.infrastructure.repositories.Repository;
import pl.com.bottega.photostock.sales.infrastructure.repositories.RepositoryFactory;
import pl.com.bottega.photostock.sales.model.deal.Money;
import pl.com.bottega.photostock.sales.model.deal.Purchase;
import pl.com.bottega.photostock.sales.model.users.Client;
import pl.com.bottega.photostock.sales.model.usertool.Reservation;

import java.util.List;

import static pl.com.bottega.photostock.sales.infrastructure.repositories.RepositoryType.FAKE_REPOSITORY;

/**
 * lublin-3-16-photostock
 * Sergii
 * 2016-05-13.
 */

public class ClientManagement {

    private RepositoryFactory repositoryFactory = AbstractRepositoryFactory.getRepositoryByType(FAKE_REPOSITORY);
    private Repository clientRepository = repositoryFactory.getClientRepository();
    private Repository reservationRepository = repositoryFactory.getReservationRepository();
    private Repository purchaseRepository = repositoryFactory.getPurchaseRepository();

    public String register(String firstName, String secondName, String login, String email, String address) {
        Client result = new Client(firstName, secondName, address, new Money(0));
        clientRepository.save(result);
        return result.getNumber();
    }

    public List<Reservation> findReservations(String clientNumber) {
        validator(clientNumber);
        return reservationRepository.find(clientNumber);
    }

    public List<Purchase> findPurchases(String clientNumber) {
        validator(clientNumber);
        return purchaseRepository.find(clientNumber);
    }

    public void recharge(String clientNumber, Money addMoney) {
        Client client = (Client) clientRepository.load(clientNumber);
        client.recharge(addMoney);
        clientRepository.save(client);
    }

    private void validator(String clientNumber) {
        Preconditions.checkArgument(clientNumber != null, "Forbidden! Object is null");
    }

}
