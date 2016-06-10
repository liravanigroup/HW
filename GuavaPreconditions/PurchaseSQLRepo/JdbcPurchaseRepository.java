package pl.com.bottega.photostock.sales.infrastructure.repositories;

import pl.com.bottega.photostock.sales.model.deal.Money;
import pl.com.bottega.photostock.sales.model.deal.Purchase;
import pl.com.bottega.photostock.sales.model.exceptions.DataAccessException;
import pl.com.bottega.photostock.sales.model.products.Product;
import pl.com.bottega.photostock.sales.model.users.Client;
import pl.com.bottega.photostock.sales.model.users.ClientFactory;
import pl.com.bottega.photostock.sales.model.users.ClientStatus;
import pl.com.bottega.photostock.sales.model.usertool.Reservation;

import java.sql.*;
import java.util.*;
import java.util.Date;

import static java.sql.Date.valueOf;
import static pl.com.bottega.photostock.sales.infrastructure.repositories.RepositoryType.SQL_REPOSITORY;

/**
 * Created by Amsterdam on 09.06.2016.
 */
public class JdbcPurchaseRepository implements Repository<Purchase> {

    private final String url;
    private final String login;
    private final String password;
    Repository<Product> productRepository = AbstractRepositoryFactory.getRepositoryByType(SQL_REPOSITORY).getProductRepository();


    public JdbcPurchaseRepository(String url, String login, String password) {
        this.url = url;
        this.login = login;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, login, password);
    }

    @Override
    public Purchase load(String purchaseNumber) {
        String sqlSelectQuery = "SELECT id, clientId, createDate FROM purchases WHERE number = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sqlSelectQuery);
            statement.setString(1, purchaseNumber);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {

                List<Product> products = getItems(connection, resultSet.getInt("id"));
                Date date = resultSet.getDate("createDate");
                Client client = getClientById(connection, resultSet.getInt("clientId"));

                Purchase result = new Purchase(client, products);
                result.setTime(date);
                result.setNumber(purchaseNumber);
                return result;
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private Client getClientById(Connection connection, int clientId) throws SQLException {
        String sqlSelectQuery = "" +
                "SELECT id, name, number, address, amountCents, amountCurrency, active, UPPER(statuses.status_name) AS status " +
                "FROM ClientsStatuses " +
                "JOIN statuses ON statuses.status_id = clientsStatuses.status_Id " +
                "JOIN clients ON clients.id = clientsStatuses.client_id WHERE id = ?;";
        PreparedStatement statement = connection.prepareStatement(sqlSelectQuery);
        statement.setInt(1, clientId);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return ClientFactory.getClientInstance(
                    resultSet.getString("number"),
                    resultSet.getString("name"),
                    resultSet.getString("address"),
                    new Money(resultSet.getDouble("amountCents"), resultSet.getString("amountCurrency")),
                    ClientStatus.valueOf(resultSet.getString("status")),
                    resultSet.getBoolean("active")
            );
        }
        return null;
    }

    private List<Product> getItems(Connection connection, int purchaseId) throws SQLException {
        ResultSet purchaseItems = getPurchaseItemsResultSet(purchaseId);
        List<Integer> itemsIDs = getItemsIDs(purchaseItems);
        return getProductsByIDs(connection, itemsIDs);
    }

    private List<Product> getProductsByIDs(Connection connection, List<Integer> itemsIDs) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sqlSelectQuery = "SELECT number FROM products WHERE id IN (" + getQuestionMarkedString(itemsIDs.size()) + ")";
        PreparedStatement statement = connection.prepareStatement(sqlSelectQuery);
        fillIDsToStatement(itemsIDs, statement);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Product product = productRepository.load(resultSet.getString("number"));
            products.add(product);
        }
        return products;
    }

    private String getQuestionMarkedString(int itemsCount) {
        String[] questionMarks = new String[itemsCount];
        Arrays.fill(questionMarks, 0, itemsCount, "?");
        return String.join(",", questionMarks);
    }

    private void fillIDsToStatement(List<Integer> itemsIDs, PreparedStatement statement) throws SQLException {
        for (int i = 1; i <= itemsIDs.size(); i++)
            statement.setInt(i, itemsIDs.get(i - 1));
    }

    private ResultSet getPurchaseItemsResultSet(int purchaseId) throws SQLException {
        String sqlSelectQuery = "SELECT productId FROM purchasesProducts WHERE purchaseId = ?;";
        PreparedStatement statement = getConnection().prepareStatement(sqlSelectQuery);
        statement.setInt(1, purchaseId);
        return statement.executeQuery();
    }

    private List<Integer> getItemsIDs(ResultSet purchaseItems) throws SQLException {
        List<Integer> items = new ArrayList<>();
        while (purchaseItems.next())
            items.add(purchaseItems.getInt("productId"));
        return items;
    }

    @Override
    public void save(Purchase purchase) {
        Purchase loadedPurchase = load(purchase.getNumber());
        if (loadedPurchase == null) {
            try (Connection connection = getConnection()) {
                insertIntoPurchases(connection, purchase);
                linkProductsAndPurcase(purchase, connection);
            } catch (SQLException e) {
                throw new DataAccessException(e);
            }
        }
    }

    private void linkProductsAndPurcase(Purchase purchase, Connection connection) throws SQLException {
        Set<Integer> ids = getItemsIDs(connection, purchase);
        int purchaseID = getPurchaseID(connection, purchase);

        for (int productID : ids)
            insertIntoPurchasesProducts(connection, purchaseID, productID);
    }

    private int getPurchaseID(Connection connection, Purchase purchase) throws SQLException {
        String sqlSelectQuery = "SELECT id FROM Purchases WHERE number = ?";
        PreparedStatement statement = connection.prepareStatement(sqlSelectQuery);
        statement.setString(1, purchase.getNumber());
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next())
            return resultSet.getInt("id");
        return -1;
    }

    private void insertIntoPurchasesProducts(Connection connection, int purchaseID, int productID) throws SQLException {
        if (!isAlreadyExists(connection, purchaseID, productID)) {
            String sqlInsertQuery = "INSERT INTO PurchasesProducts (purchaseId, productId) VALUES(?,?);";
            PreparedStatement statement = connection.prepareStatement(sqlInsertQuery);
            statement.setInt(1, purchaseID);
            statement.setInt(2, productID);
            statement.executeUpdate();
        }
    }

    private boolean isAlreadyExists(Connection connection, int purchaseID, int productID) throws SQLException {
        String sqlInsertQuery = "SELECT * FROM PurchasesProducts WHERE purchaseId = ? AND  productId = ?;";
        PreparedStatement statement = connection.prepareStatement(sqlInsertQuery);
        statement.setInt(1, purchaseID);
        statement.setInt(2, productID);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    }

    private Set<Integer> getItemsIDs(Connection connection, Purchase purchase) throws SQLException {
        List<Product> products = purchase.getProducts();
        return getIDsProducts(connection, products);
    }

    private Set<Integer> getIDsProducts(Connection connection, List<Product> products) throws SQLException {
        String sqlSelectQuery = "SELECT id FROM Products WHERE number IN (" + getQuestionMarkedString(products.size()) + ")";
        PreparedStatement statement = connection.prepareStatement(sqlSelectQuery);
        fillNamesToStatement(products, statement);
        ResultSet resultSet = statement.executeQuery();
        return getIDsSet(resultSet);
    }

    private Set<Integer> getIDsSet(ResultSet resultSet) throws SQLException {
        Set<Integer> result = new HashSet<>();
        while (resultSet.next())
            result.add(resultSet.getInt("id"));
        return result;
    }

    private void fillNamesToStatement(List<Product> products, PreparedStatement statement) throws SQLException {
        for (int i = 1; i <= products.size(); i++)
            statement.setString(i, products.get(i - 1).getNumber());
    }

    private void insertIntoPurchases(Connection connection, Purchase purchase) throws SQLException {
        String sqlInsertQuery = "INSERT INTO Purchases (number, clientId, createDate) VALUES (?, ?, NOW());";
        PreparedStatement statement = connection.prepareStatement(sqlInsertQuery);
        int clientID = getClientIdByNumber(connection, purchase.getOwner().getNumber());
        statement.setString(1, purchase.getNumber());
        statement.setInt(2, clientID);
        statement.executeUpdate();
    }

    private int getClientIdByNumber(Connection connection, String number) throws SQLException {
        String sqlSelectQuery = "SELECT id FROM Clients WHERE number = ?";
        PreparedStatement statement = connection.prepareStatement(sqlSelectQuery);
        statement.setString(1, number);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next())
            return resultSet.getInt("id");
        return -1;
    }

    @Override
    public List<Purchase> find(String number) {
        return null;
    }

    @Override
    public Reservation findOpenedPer(Client client) {
        return null;
    }
}
