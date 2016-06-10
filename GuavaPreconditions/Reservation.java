package pl.com.bottega.photostock.sales.model.usertool;

import com.google.common.base.Preconditions;
import pl.com.bottega.photostock.sales.model.deal.Offer;
import pl.com.bottega.photostock.sales.model.exceptions.ProductNotAvailableException;
import pl.com.bottega.photostock.sales.model.products.Product;
import pl.com.bottega.photostock.sales.model.users.Client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;


public class Reservation {
    private String number = UUID.randomUUID().toString();
    private Client owner;
    private List<Product> products = new LinkedList<>();
    private Date date = new Date();
    private boolean closed;

    public Reservation(Client owner) {
        setOwner(owner);
    }

    public void add(Product product) {
        if (!closed) {
            validateProduct(product);
            checkArgument(!products.contains(product), "Product already exists");
            products.add(product);
        }
    }

    public void remove(Product product) {
        if (!closed) {
            validateOwner(owner);
            checkArgument(products.remove(product), "Product is not exists in reservation");
        }
    }

    public Offer generateOffer() {
        List<Product> result = new LinkedList<>();
        for (Product product : products) {
            if (product.isAvailable())
                result.add(product);
        }
        return new Offer(sortItems(result));
    }

    public int getItemsCount() {
        notNullValidate(products);
        return products.size();
    }

    private List<Product> sortItems(List<Product> products) {
        //Collections.sort(products, SortByPrice());
        return products;
    }

    private void validateOwner(Client owner) {
        notNullValidate(owner);
        checkState(owner.isActive(), "Forbidden! Client is not active!");
    }

    private void validateProduct(Product product) {
        validateOwner(owner);
        notNullValidate(product);
        checkState(product.isAvailable(), "Sorry this product %s is not available. Source %s", product.getNumber(), Reservation.class);
        checkArgument(!products.contains(product), "Product already contains");
    }

    private <T> void notNullValidate(T object) {
        checkArgument(object != null, "Forbidden! You tried to use null");
    }

    public Client getOwner() {
        return owner;
    }

    private void setOwner(Client owner) {
        validateOwner(owner);
        this.owner = owner;
    }

    public String getNumber() {
        return number;
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() {
        this.closed = true;
    }

    public String[] export() {
        String number = getNumber();
        String owner = getOwner().getNumber();
        String date = String.valueOf(this.date.getTime());
        String products = getProductsString();
        String closed = String.valueOf(isClosed());

        return new String[]{number, owner, date, products, closed};
    }

    private String getProductsString() {
        if (products.size() == 0)
            return "";
        StringBuilder builder = new StringBuilder();
        for (Product product : products) {
            builder.append(product.getNumber());
            builder.append("|");
        }
        return builder.toString();
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
