package pl.com.bottega.photostock.sales.api;

import com.google.common.base.Preconditions;
import pl.com.bottega.photostock.sales.infrastructure.repositories.AbstractRepositoryFactory;
import pl.com.bottega.photostock.sales.infrastructure.repositories.Repository;
import pl.com.bottega.photostock.sales.infrastructure.repositories.RepositoryFactory;
import pl.com.bottega.photostock.sales.model.products.Picture;
import pl.com.bottega.photostock.sales.model.products.Product;
import pl.com.bottega.photostock.sales.model.users.Client;
import pl.com.bottega.photostock.sales.model.usertool.LightBox;
import pl.com.bottega.photostock.sales.model.usertool.Reservation;

import static com.google.common.base.Preconditions.checkArgument;
import static pl.com.bottega.photostock.sales.infrastructure.repositories.RepositoryType.FAKE_REPOSITORY;

/**
 * Created by Admin on 23.04.2016.
 */
public class LightBoxManagement {

    private RepositoryFactory repositoryFactory = AbstractRepositoryFactory.getRepositoryByType(FAKE_REPOSITORY);
    private Repository<Client> clientRepository = repositoryFactory.getClientRepository();
    private Repository<Reservation> reservationRepository = repositoryFactory.getReservationRepository();
    private Repository<Product> productRepository = repositoryFactory.getProductRepository();
    private Repository<LightBox> lightBoxRepository = repositoryFactory.getLightBoxRepository();
    private PurchaseProcess purchaseProcess = new PurchaseProcess();


    public String createLightBox(String clientNumber, String name) {
        Client client = clientRepository.load(clientNumber);
        LightBox lightBox = new LightBox(client, name);
        lightBoxRepository.save(lightBox);
        return lightBox.getNumber();
    }

    public void addToLightBox(String lightBoxNumber, String productNumber) {
        Product product = productRepository.load(productNumber);
        checkArgument(product instanceof Picture, "Entry number does not belong to picture");
        LightBox lightBox = lightBoxRepository.load(lightBoxNumber);
        lightBox.add((Picture) product);
        lightBoxRepository.save(lightBox);
    }

    public void addAllToReservation(String lightBoxNumber) {
        LightBox lightBox = lightBoxRepository.load(lightBoxNumber);
        String clientNr = lightBox.getOwner().getNumber();
        for (Picture picture : lightBox.getPictures())
            purchaseProcess.add(clientNr, picture.getNumber());
        lightBoxRepository.save(lightBox);
    }

    public void addToReservation(String lightBoxNumber, String pictureId) {
        LightBox lightBox = lightBoxRepository.load(lightBoxNumber);
        String clientNr = lightBox.getOwner().getNumber();
        for (Picture picture : lightBox.getPictures()) {
            if (picture.getNumber().equals(pictureId)) {
                purchaseProcess.add(clientNr, picture.getNumber());
                lightBoxRepository.save(lightBox);
                return;
            }
        }
        throw new IllegalArgumentException(lightBoxNumber + " does not contain " + pictureId);
    }

    public void share(String lightBoxNumber, String clientNumber) {
        Client coOwner = clientRepository.load(clientNumber);
        LightBox lightBox = lightBoxRepository.load(lightBoxNumber);
        Client owner = lightBox.getOwner();
        checkArgument(owner.getCompany().equals(coOwner.getCompany()), "Client with nr: %s is from other company", clientNumber);
        lightBox.setCoOwner(coOwner);
        lightBoxRepository.save(lightBox);
    }

    public void reserve(String lightBoxNumber, String reservationNumber, String... pictureNumber) {
        LightBox lightBox = lightBoxRepository.load(lightBoxNumber);
        Reservation reservation = reservationRepository.load(reservationNumber);
        for (String number : pictureNumber) {
            Picture result = lightBox.getPictureByNumber(number);
            reservation.add(result);
            result.reservePer(lightBox.getOwner());
            productRepository.save(result);
        }
        reservationRepository.save(reservation);
    }

    public String clone(String lightBoxNumber) {
        LightBox lightBox = lightBoxRepository.load(lightBoxNumber);
        LightBox result = lightBox.clone();
        lightBoxRepository.save(result);
        return result.getNumber();
    }
}
