package pl.com.bottega.photostock.sales.infrastructure.repositories;

import pl.com.bottega.photostock.sales.model.exceptions.DataAccessException;
import pl.com.bottega.photostock.sales.model.products.*;
import pl.com.bottega.photostock.sales.model.users.Client;
import pl.com.bottega.photostock.sales.model.usertool.Reservation;

import java.sql.*;
import java.util.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static pl.com.bottega.photostock.sales.model.products.AbstractProduct.ProductType.valueOf;
import static pl.com.bottega.photostock.sales.model.products.ProductData.getProductData;

public class JdbcProductRepository implements Repository<Product> {

    private final String url;
    private final String login;
    private final String password;

    public JdbcProductRepository(String url, String login, String pwd) {
        this.url = url;
        this.login = login;
        this.password = pwd;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, login, password);
    }


    @Override
    public Product load(String productNumber) {
        try (Connection connection = getConnection()) {
            return loadProductByNumber(connection, productNumber);
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        }
    }

    private Product loadProductByNumber(Connection connection, String productNumber) throws SQLException {
        ResultSet productDataResultSet = getProductDataResultSet(connection, productNumber);
        ResultSet productTagsResultSet = getProductTagsResultSet(connection, productNumber);
        return productDataResultSet.next() ? ProductFactory.getProductInstance(getProductData(productDataResultSet, productTagsResultSet)) : null;
    }

    private ResultSet getProductDataResultSet(Connection connection, String productNumber) throws SQLException {
        String sqlSelectQuery = "SELECT name,number,priceCents,priceCurrency,available,UPPER(type) type FROM products WHERE number = ?";
        PreparedStatement statement = connection.prepareStatement(sqlSelectQuery);
        statement.setString(1, productNumber);
        ResultSet result = statement.executeQuery();
        return result;
    }

    private ResultSet getProductTagsResultSet(Connection connection, String productNumber) throws SQLException {
        String sqlSelectQuery = "SELECT tags.name name, tags.id id FROM Tags JOIN productstags ON Tags.id = productstags.tagID JOIN Products ON Products.id = productstags.productid WHERE Products.number = ?";
        PreparedStatement statement = connection.prepareStatement(sqlSelectQuery);
        statement.setString(1, productNumber);
        ResultSet result = statement.executeQuery();
        return result;
    }


    private void validateProduct(Product product) {
        checkArgument(product != null, "Product is null");
        checkState(!isExists(product), "Product already saved");
    }

    private boolean isExists(Product product) {
        return product.equals(load(product.getNumber()));
    }

    @Override
    public void save(Product product) {
        validateProduct(product);
        try (Connection connection = getConnection()) {
            saveOrUpdateProduct(connection, product);
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        }
    }

    private void saveOrUpdateProduct(Connection connection, Product product) throws Exception {
        Product loadedProduct = load(product.getNumber());
        if (loadedProduct == null)
            saveProduct(connection, product);
        else
            updateProduct(connection, product, loadedProduct);
    }

    private void saveProduct(Connection connection, Product product) throws Exception {
        saveProductWithoutTags(connection, product);
        saveTags(connection, product);
    }

    private void saveProductWithoutTags(Connection connection, Product product) throws SQLException {
        String sqlInsertQuery = "INSERT INTO products (name,number,available,priceCents,priceCurrency,type) VALUES(?,?,?,?,?,?);";
        PreparedStatement statement = connection.prepareStatement(sqlInsertQuery);

        statement.setString(1, product.getName());
        statement.setString(2, product.getNumber());
        statement.setBoolean(3, product.isAvailable());
        statement.setDouble(4, product.getPrice());
        statement.setString(5, product.getCurrency());
        statement.setString(6, product.getType());

        statement.execute();
    }

    private boolean isCorrectTags(Product product) {
        return product.getTags() != null && product.getTags().length != 0;
    }

    private void saveTags(Connection connection, Product product) throws Exception {
        if (!isCorrectTags(product)) return;
        Set<String> tagsToSave = getTagsForSave(connection, product);
        insertTags(connection, product, tagsToSave);
    }


    private Set<String> getTagsForSave(Connection connection, Product product) throws Exception {
        Set<String> alreadySavedTagsSet = getAlreadySavedTagsSet(connection, product);
        Set<String> newTagsSet = getNewTagsSet(alreadySavedTagsSet, product.getTags());
        return newTagsSet;
    }

    private void insertTags(Connection connection, Product product, Set<String> tagsToSave) throws Exception {
        insertTagsIntoTagsTable(connection, tagsToSave);
        linkTags(connection, product);
    }

    private void insertTagsIntoTagsTable(Connection connection, Set<String> tagsToSave) throws Exception {
        for (String tag : tagsToSave) {
            insertTagIntoTags(connection, tag);
        }
    }


    private void linkTags(Connection connection, Product product) throws Exception {
        int productId = getProductId(connection, product);
        Map<String, Integer> tagsNameAndIdSet = getTagsIdsAndNamesSet(connection, product);
        for (Integer tagId : tagsNameAndIdSet.values())
            insertTagIntoProductsTags(connection, productId, tagId);
    }

    private Map<String, Integer> getTagsIdsAndNamesSet(Connection connection, Product product) throws Exception {
        ResultSet alreadySavedTagsResultSet = getAlreadySavedTagsResultSet(connection, product.getTags());
        Map<String, Integer> tagsIdSet = new HashMap<>();
        while (alreadySavedTagsResultSet.next())
            tagsIdSet.put(alreadySavedTagsResultSet.getString("name"), alreadySavedTagsResultSet.getInt("id"));
        return tagsIdSet;
    }



    private void updateProduct(Connection connection, Product product, Product loadedProduct) throws Exception {
        if (product.isEqualsTags(loadedProduct))
            updateProductWithoutTags(connection, product);
        else
            updateProductWithTags(connection, product);
    }

    private void updateProductWithoutTags(Connection connection, Product updatingProduct) throws SQLException {
        String sqlUpdateQuery = "UPDATE products SET name = ?, number = ?, available = ?, priceCents = ?, priceCurrency = ?, type = ? WHERE number = ?";

        PreparedStatement statement = connection.prepareStatement(sqlUpdateQuery);

        statement.setString(1, updatingProduct.getName());
        statement.setString(2, updatingProduct.getNumber());
        statement.setBoolean(3, updatingProduct.isAvailable());
        statement.setDouble(4, updatingProduct.getPrice());
        statement.setString(5, updatingProduct.getCurrency());
        statement.setString(6, updatingProduct.getType());
        statement.setString(7, updatingProduct.getNumber());

        statement.execute();
    }

    private void updateProductWithTags(Connection connection, Product product) throws Exception {
        updateTags(connection, product);
        if (!product.equals(load(product.getNumber())))
            saveOrUpdateProduct(connection, product);
    }


    private Set<String> getTagsForRemove(Connection connection, Product product) throws Exception {
        Set<String> alreadySavedTagsSet = getAllProductTags(connection, product.getNumber());
        Set<String> excessTagsSet = getExcessTags(product, alreadySavedTagsSet);
        return excessTagsSet;
    }

    private Set<String> getExcessTags(Product product, Set<String> alreadySavedTagsSet) {
        Set<String> actualTags = new HashSet<>(Arrays.asList(product.getTags()));
        Set<String> result = new HashSet<>();

        for (String tag : alreadySavedTagsSet) {
            if (!actualTags.contains(tag))
                result.add(tag);
        }
        return result;
    }

    private Set<String> getAllProductTags(Connection connection, String productNumber) throws SQLException {
        Set<String> result = new HashSet<>();
        ResultSet resultSet = getProductTagsResultSet(connection, productNumber);
        while (resultSet.next())
            result.add(resultSet.getString("name"));
        return result;
    }

    private Set<String> getNewTagsSet(Set<String> alreadySavedTagsSet, String[] productTags) {
        Set<String> result = new HashSet<>();
        for (String tag : productTags)
            if (!alreadySavedTagsSet.contains(tag))
                result.add(tag);
        return result;
    }

    private Set<String> getAlreadySavedTagsSet(Connection connection, Product product) throws Exception {
        ResultSet alreadySavedTags = getAlreadySavedTagsResultSet(connection, product.getTags());
        Set<String> existingTags = new HashSet<>();
        while (alreadySavedTags.next())
            existingTags.add(alreadySavedTags.getString("name"));
        return existingTags;
    }


    private ResultSet getAlreadySavedTagsResultSet(Connection connection, String[] productTags) throws Exception {
        String sqlSelectQuery = "SELECT id, name FROM Tags WHERE name IN (" + getQuestionMarkedString(productTags.length) + ")";
        PreparedStatement statement = connection.prepareStatement(sqlSelectQuery);
        fillTagsToStatement(productTags, statement);
        ResultSet alreadySavedTagsSet = statement.executeQuery();
        return alreadySavedTagsSet;
    }

    private String getQuestionMarkedString(int tagsCount) {
        String[] questionMarks = new String[tagsCount];
        Arrays.fill(questionMarks, 0, tagsCount, "?");
        return String.join(",", questionMarks);
    }

    private void fillTagsToStatement(String[] tags, PreparedStatement statement) throws SQLException {
        for (int i = 1; i <= tags.length; i++) {
            statement.setString(i, tags[i - 1]);
        }
    }


    private void updateTags(Connection connection, Product product) throws Exception {
        int productId = getProductId(connection, product);
        if (productId == -1) return;
        saveTags(connection, product, productId);
        removeTags(connection, product);
    }


    private void removeTags(Connection connection, Product product) throws Exception {
        Set<String> toRemoveTags = getTagsForRemove(connection, product);
        for (String tag : toRemoveTags) {
            removeTag(connection, tag);
        }
    }

    private void removeTag(Connection connection, String tag) throws SQLException {
        int tagId = getTagId(connection, tag);
        removeTagFromProductsTags(connection, tagId);
        removeTagFromTags(connection, tagId);
    }

    private void removeTagFromTags(Connection connection, Integer tagId) throws SQLException {
        String sqlDeleteQuery = "DELETE FROM Tags WHERE id = ? ;";
        PreparedStatement statement = connection.prepareStatement(sqlDeleteQuery);
        statement.setInt(1, tagId);
        statement.execute();
    }

    private void removeTagFromProductsTags(Connection connection, Integer tagId) throws SQLException {
        String sqlDeleteQuery = "DELETE FROM ProductsTags WHERE tagId = ? ;";
        PreparedStatement statement = connection.prepareStatement(sqlDeleteQuery);
        statement.setInt(1, tagId);
        statement.execute();
    }


    private void saveTags(Connection connection, Product product, int productId) throws Exception {
        Set<String> toSaveTags = getTagsForSave(connection, product);
        for (String tag : toSaveTags) {
            insertTag(connection, tag, productId);
        }
    }

    private int getTagId(Connection connection, String tag) throws SQLException {
        String sqlSelectQuery = "SELECT id FROM tags WHERE name = ?";

        PreparedStatement statement = connection.prepareStatement(sqlSelectQuery);
        statement.setString(1, tag);
        ResultSet resultSet = statement.executeQuery();

        return resultSet.next() ? resultSet.getInt("id") : -1;
    }

    private void insertTag(Connection connection, String tag, int productId) throws Exception {
        if (!isAlreadyExists(connection, tag)) {
            insertTagIntoTags(connection, tag);
            int tagId = getTagId(connection, tag);
            insertTagIntoProductsTags(connection, productId, tagId);
        }
    }

    private boolean isAlreadyExists(Connection connection, String tag) throws SQLException {
        String sqlSelectQuery = "SELECT id FROM tags WHERE name = ?";
        PreparedStatement statement = connection.prepareStatement(sqlSelectQuery);
        statement.setString(1, tag);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    }

    private void insertTagIntoTags(Connection connection, String tag) throws Exception {
        String sqlInsertQuery = "INSERT INTO Tags (name) VALUES (?)";
        PreparedStatement statement = connection.prepareStatement(sqlInsertQuery);
        statement.setString(1, tag);
        statement.executeUpdate();
    }

    private int getProductId(Connection connection, Product product) throws SQLException {
        String sqlSelectQuery = "SELECT id FROM products WHERE number = ?";

        PreparedStatement statement = connection.prepareStatement(sqlSelectQuery);
        statement.setString(1, product.getNumber());
        ResultSet resultSet = statement.executeQuery();

        return resultSet.next() ? resultSet.getInt("id") : -1;
    }

    private void insertTagIntoProductsTags(Connection connection, Integer productId, Integer tagId) throws Exception {
        if (!isLinkAlreadyExists(connection, productId, tagId)) {
            String sqlInsertQuery = "INSERT INTO ProductsTags (productId, tagId) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sqlInsertQuery);
            statement.setInt(1, productId);
            statement.setInt(2, tagId);
            statement.executeUpdate();
        }
    }

    private boolean isLinkAlreadyExists(Connection connection, Integer productId, Integer tagId) throws SQLException {
        String sqlInsertQuery = "SELECT * FROM ProductsTags WHERE productid = ? AND tagid = ?";
        PreparedStatement statement = connection.prepareStatement(sqlInsertQuery);
        statement.setInt(1, productId);
        statement.setInt(2, tagId);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    }


    @Override
    public List<Product> find(String number) {
        return null;
    }


}
