package pl.com.bottega.photostock.sales.api;

import pl.com.bottega.photostock.sales.infrastructure.repositories.ProductRepository;
import pl.com.bottega.photostock.sales.model.deal.Money;
import pl.com.bottega.photostock.sales.model.products.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductsCatalog {
    ProductRepository productRepository = new ProductRepository();

    public List<Product> find(String name, Money from, Money to, String... tags) {
        List result = new ArrayList<>();
        try {
            result = productRepository.selectBy(name, from, to, tags);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
}
