package pl.com.bottega.photostock.sales.infrastructure.repositories;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import pl.com.bottega.photostock.sales.model.deal.Money;
import pl.com.bottega.photostock.sales.model.exceptions.ProductNotAvailableException;
import pl.com.bottega.photostock.sales.model.products.Product;
import pl.com.bottega.photostock.sales.model.users.Client;
import pl.com.bottega.photostock.sales.model.usertool.Reservation;
import sun.plugin.javascript.navig.Array;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;


public class ProductRepository implements Repository<Product> {

    public static Map<String, Product> productsDataBase = new HashMap<>();

    @Override
    public Product load(String nr) {
        Product result = productsDataBase.get(nr);
        if (result == null)
            throw new ProductNotAvailableException("Product nr " + nr + "does not exist", nr, ProductRepository.class);
        return result;
    }

    @Override
    public void save(Product product) {
        productsDataBase.put(product.getNumber(), product);
    }



    public List<Product> selectBy(String name, Money from, Money to, String... tags) {
        List<Product> result;
        result = selectByPrice(from, to);
        result = selectByName(result, name);
        result = selectByTags(result, tags);
        if (result.size() == 0) return null;
        return result;
    }

    private List<Product> selectByPrice(List<Product> products, Money from, Money to) {
        if (from == null && to == null) return products;
        return selectPrice(products, from, to);
    }

    private List<Product> selectPrice(List<Product> products, Money from, Money to) {
        if (from == null) {
            return selectPrice(products, new Money(0, to.getCurrency()), to);
        } else if (to == null) {
            return Lists.newLinkedList(Iterables.filter(products, product -> product.calculatePrice().isGreaterOrEqualsThan(from)));
        } else {
            checkArgument(!from.isGreaterThan(to), "Wrong price diapason");
            return Lists.newLinkedList(Iterables.filter(products, product -> product.calculatePrice().isGreaterOrEqualsThan(from) && product.calculatePrice().isLessOrEqualsThan(to)));
        }
    }

    private List<Product> selectByPrice(Money from, Money to) {
        return selectByPrice(new ArrayList<>(productsDataBase.values()), from, to);
    }

    private List<Product> selectByName(List<Product> products, String name) {
        if (name == null) return products;
        return Lists.newLinkedList(Iterables.filter(products, product -> product.getName().contains(name)));
    }

    private List<Product> selectByTags(List<Product> products, String[] tags) {
        if (tags == null || tags.length == 0) return products;
        return Lists.newLinkedList(Iterables.filter(products, product -> isContainsTags(product.getTags(), tags)));
    }

    private boolean isContainsTags(String[] source, String[] findsTags) {
        for (String productTag : findsTags) {
            for (String tag : source) {
                if (productTag.contains(tag)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Product> selectByName(String name) {
        return selectByName(new ArrayList<>(productsDataBase.values()), name);
    }

    public List<Product> selectByTags(String[] tags) {
        return selectByTags(new ArrayList<Product>(productsDataBase.values()), tags);
    }
}
