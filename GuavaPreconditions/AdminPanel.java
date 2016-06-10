package pl.com.bottega.photostock.sales.api;

import com.google.common.base.Preconditions;
import pl.com.bottega.photostock.sales.infrastructure.repositories.AbstractRepositoryFactory;
import pl.com.bottega.photostock.sales.infrastructure.repositories.Repository;
import pl.com.bottega.photostock.sales.infrastructure.repositories.RepositoryFactory;
import pl.com.bottega.photostock.sales.model.deal.Money;
import pl.com.bottega.photostock.sales.model.products.AbstractProduct.ProductType;
import pl.com.bottega.photostock.sales.model.products.Product;
import pl.com.bottega.photostock.sales.model.products.ProductFactory;
import pl.com.bottega.photostock.sales.model.users.CanUseCredit;
import pl.com.bottega.photostock.sales.model.users.Client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static pl.com.bottega.photostock.sales.infrastructure.repositories.RepositoryType.FAKE_REPOSITORY;

/**
 * lublin-3-16-photostock
 * Sergii
 * 2016-05-13.
 */

public class AdminPanel {
    private static final Money INITIAL_CREDIT_LIMIT = new Money(200);

    private RepositoryFactory repositoryFactory = AbstractRepositoryFactory.getRepositoryByType(FAKE_REPOSITORY);
    private Repository<Client> clientRepository = repositoryFactory.getClientRepository();
    private Repository<Product> productRepository = repositoryFactory.getProductRepository();

    public String addProduct(ProductType productType, String name, String number, Money price, String... tags) {
        Product product = ProductFactory.getProductInstance(productType, name, number, price, true, tags);
        productRepository.save(product);
        return product.getNumber();
    }

    public void promoteClient(String clientNumber) {
        Client client = clientRepository.load(clientNumber);
        checkState(!client.isActive() && client.getPaymentStrategy() instanceof CanUseCredit, "Client is not active!");
        client.setPaymentStrategy(new CanUseCredit());
        changeCreditLimit(clientNumber, INITIAL_CREDIT_LIMIT);
        clientRepository.save(client);
    }

    private void changeCreditLimit(String clientNumber, Money creditLimit) {
        Client client = clientRepository.load(clientNumber);
        checkArgument(client.getPaymentStrategy() instanceof CanUseCredit, "%s is not able to use credit limit", clientNumber);
        if (client.isActive())
            client.setCreditLimit(creditLimit);
        clientRepository.save(client);
    }

    public void changeAvability(String productNumber, boolean available) {
        Product product = productRepository.load(productNumber);
        if (available)
            product.activate();
        else
            product.deactivate();
        productRepository.save(product);
    }
}
