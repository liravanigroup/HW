package pl.com.bottega.photostock.sales.infrastructure.repositories;

import org.junit.Before;
import org.junit.Test;
import pl.com.bottega.photostock.sales.model.deal.Money;
import pl.com.bottega.photostock.sales.model.deal.Offer;
import pl.com.bottega.photostock.sales.model.deal.Purchase;
import pl.com.bottega.photostock.sales.model.products.Product;
import pl.com.bottega.photostock.sales.model.users.Client;
import pl.com.bottega.photostock.sales.model.users.ClientFactory;
import pl.com.bottega.photostock.sales.model.usertool.Reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static pl.com.bottega.photostock.sales.model.deal.Money.Currency.PLN;
import static pl.com.bottega.photostock.sales.model.deal.Money.Currency.USD;
import static pl.com.bottega.photostock.sales.model.users.ClientStatus.STANDARD;
import static pl.com.bottega.photostock.sales.model.users.ClientStatus.VIP;

/**
 * Created by Amsterdam on 09.06.2016.
 */
public class JdbcPurchaseRepositoryTest {
    private Repository<Product> productRepository;
    private Repository<Purchase> purchaseRepository;

    @Before
    public void setUp() throws Exception {
        // given
        Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:stockdb", "SA", "");
        dropTables(connection);
        createTables(connection);
        insertData(connection);
        connection.close();

        // when
        purchaseRepository = new JdbcPurchaseRepository("jdbc:hsqldb:mem:stockdb", "SA", "");
        productRepository = new JdbcProductRepository("jdbc:hsqldb:mem:stockdb", "SA", "");
    }

    private void dropTables(Connection connection) throws SQLException {
        connection.createStatement().executeUpdate("DROP TABLE ProductsTags IF EXISTS;");
        connection.createStatement().executeUpdate("DROP TABLE PurchasesProducts IF EXISTS;");
        connection.createStatement().executeUpdate("DROP TABLE ClientsStatuses IF EXISTS;");
        connection.createStatement().executeUpdate("DROP TABLE Tags IF EXISTS;");
        connection.createStatement().executeUpdate("DROP TABLE Statuses IF EXISTS;");
        connection.createStatement().executeUpdate("DROP TABLE Products IF EXISTS;");
        connection.createStatement().executeUpdate("DROP TABLE Purchases IF EXISTS;");
        connection.createStatement().executeUpdate("DROP TABLE Clients IF EXISTS;");
    }

    private void createTables(Connection connection) throws Exception {
        connection.createStatement().executeUpdate("CREATE TABLE Products (\n" +
                "  id INTEGER IDENTITY PRIMARY KEY,\n" +
                "  type VARCHAR(20) NOT NULL,\n" +
                "  name VARCHAR(255) NOT NULL,\n" +
                "  number VARCHAR(255) NOT NULL,\n" +
                "  available BOOLEAN DEFAULT true NOT NULL,\n" +
                "  priceCents DOUBLE DEFAULT 0 NOT NULL,\n" +
                "  priceCurrency CHAR(3) DEFAULT 'PLN' NOT NULL,\n" +
                "  length BIGINT\n" +
                ");" +
                "CREATE TABLE Tags (\n" +
                "  id INTEGER IDENTITY PRIMARY KEY,\n" +
                "  name VARCHAR(255) NOT NULL\n" +
                ");\n" +
                "CREATE TABLE ProductsTags (\n" +
                "  productId INTEGER FOREIGN KEY REFERENCES Products(id),\n" +
                "  tagId INTEGER FOREIGN KEY REFERENCES Tags(id),\n" +
                "  PRIMARY KEY (productId, tagId)\n" +
                ");" +
                "CREATE TABLE Clients (\n" +
                "  id IDENTITY PRIMARY KEY,\n" +
                "  name VARCHAR(255) NOT NULL,\n" +
                "  number VARCHAR(255) NOT NULL,\n" +
                "  address VARCHAR(255) NOT NULL ,\n" +
                "  amountCents INTEGER DEFAULT 0 NOT NULL,\n" +
                "  amountCurrency CHAR(3) DEFAULT 'PLN' NOT NULL,\n" +
                "  clientStatus INTEGER DEFAULT 0 NOT NULL,\n" +
                "  active BOOLEAN DEFAULT true NOT NULL\n" +
                ");" +
                "CREATE TABLE STATUSES (\n" +
                "    STATUS_NAME VARCHAR(20) NOT NULL,\n" +
                "    STATUS_ID INTEGER DEFAULT 0 NOT NULL,\n" +
                "    PRIMARY KEY (STATUS_NAME, STATUS_ID)" +
                ");" +
                "CREATE TABLE CLIENTSSTATUSES (\n" +
                "    CLIENT_ID INTEGER NOT NULL,\n" +
                "    STATUS_ID INTEGER NOT NULL," +
                "    PRIMARY KEY (CLIENT_ID, STATUS_ID)\n" +
                ");" +
                "CREATE TABLE Purchases (\n" +
                "  id INTEGER IDENTITY PRIMARY KEY,\n" +
                "  number VARCHAR(50) NOT NULL,\n" +
                "  clientId INTEGER FOREIGN KEY REFERENCES Clients(id),\n" +
                "  createDate TIMESTAMP NOT NULL\n" +
                ");" +
                "CREATE TABLE PurchasesProducts (\n" +
                "  purchaseId INTEGER FOREIGN KEY REFERENCES Purchases(id),\n" +
                "  productId INTEGER FOREIGN KEY REFERENCES Products(id),\n" +
                "  PRIMARY KEY (purchaseId, productId)\n" +
                ");");
    }

    private void insertData(Connection connection) throws SQLException {
        connection.createStatement().executeUpdate("" +
                "INSERT INTO Products (name, number, available, priceCents, priceCurrency, length, type) VALUES ('Mazda 3', 'nr1', true, 200, 'USD', NULL, 'Picture');\n" +
                "INSERT INTO Products (name, number, available, priceCents, priceCurrency, length, type) VALUES ('Mazda 6', 'nr2', true, 250, 'USD', NULL, 'Picture');\n" +
                "INSERT INTO Products (name, number, available, priceCents, priceCurrency, length, type) VALUES ('Mazda CX-5', 'nr3', true, 350, 'USD', NULL, 'Picture');\n" +
                "INSERT INTO Products (name, number, available, priceCents, priceCurrency, length, type) VALUES ('Mazda CX-7', 'nr4', false, 400, 'USD', NULL, 'Picture');\n" +
                "INSERT INTO Products (name, number, available, priceCents, priceCurrency, length, type) VALUES ('Mazda 2', 'nr5', true, 200, 'USD', NULL, 'Picture');\n" +
                "INSERT INTO Products (name, number, available, priceCents, priceCurrency, length, type) VALUES ('Mazda 5', 'nr6', false, 300, 'USD', NULL, 'Picture');\n" +
                "INSERT INTO Products (name, number, available, priceCents, priceCurrency, length, type) VALUES ('Skoda City Go', 'nr7', false, 150, 'USD', NULL, 'Picture');\n" +
                "INSERT INTO Products (name, number, available, priceCents, priceCurrency, length, type) VALUES ('Skoda Rapid', 'nr8', true, 200, 'USD', NULL, 'Picture');\n" +
                "INSERT INTO Products (name, number, available, priceCents, priceCurrency, length, type) VALUES ('Skoda Octavia', 'nr9', true, 300, 'USD', NULL, 'Picture');\n" +
                "INSERT INTO Products (name, number, available, priceCents, priceCurrency, length, type) VALUES ('Skoda Superb', 'nr10', true, 400, 'USD', NULL, 'Picture');\n" +
                "INSERT INTO Products (name, number, available, priceCents, priceCurrency, length, type) VALUES ('Skoda Roomster', 'nr11', false, 250, 'USD', NULL, 'Picture');\n" +
                "INSERT INTO Products (name, number, available, priceCents, priceCurrency, length, type) VALUES ('Metalica', 'nr12', true, 250, 'EUR', NULL, 'Video');\n" +
                "INSERT INTO Products (name, number, available, priceCents, priceCurrency, length, type) VALUES ('Black Sabath', 'nr13', true, 300, 'EUR', NULL, 'Video');\n" +
                "INSERT INTO Products (name, number, available, priceCents, priceCurrency, length, type) VALUES ('Rolling Stones', 'nr14', false, 400, 'EUR', NULL, 'Video');\n" +
                "INSERT INTO Products (name, number, available, priceCents, priceCurrency, length, type) VALUES ('U2', 'nr15', true, 600, 'PLN', NULL, 'Video');\n" +
                "INSERT INTO Products (name, number, available, priceCents, priceCurrency, length, type) VALUES ('ACDC', 'nr16', false, 550, 'PLN', NULL, 'Video');\n" +
                "INSERT INTO Tags (name) VALUES ('rock');\n" +
                "INSERT INTO Tags (name) VALUES ('retro');\n" +
                "INSERT INTO Tags (name) VALUES ('luxury');\n" +
                "INSERT INTO Tags (name) VALUES ('city');\n" +
                "INSERT INTO Tags (name) VALUES ('compact');\n" +
                "INSERT INTO Tags (name) VALUES ('luxury');\n" +
                "INSERT INTO Tags (name) VALUES ('cars');\n" +
                "INSERT INTO Tags (name) VALUES ('suv');\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (11, 0);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (12, 0);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (13, 0);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (14, 0);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (15, 0);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (12, 1);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (13, 1);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (0, 6);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (1, 6);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (2, 6);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (3, 6);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (4, 6);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (5, 6);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (6, 6);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (7, 6);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (8, 6);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (9, 6);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (10, 6);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (4, 3);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (6, 3);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (1, 2);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (9, 2);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (0, 4);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (7, 4);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (8, 4);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (2, 7);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (3, 7);\n" +
                "INSERT INTO ProductsTags (productId, tagId) VALUES (10, 7);" +
                "INSERT INTO Clients (name, number, address, amountCents, amountCurrency, active) VALUES ('Jan Nowak', 'nr1', 'ul. Koralowa 10', 10000, 'PLN', true);\n" +
                "INSERT INTO Clients (name, number, address, amountCents, amountCurrency, active) VALUES ('Janina Nowak', 'nr2', 'ul. Koralowa 10', 20000, 'PLN', true);\n" +
                "INSERT INTO Clients (name, number, address, amountCents, amountCurrency, active) VALUES ('Piotr Kowalski', 'nr3', 'ul. Jasna 1', 25000, 'PLN', true);\n" +
                "INSERT INTO Clients (name, number, address, amountCents, amountCurrency, active) VALUES ('Anna Sakowicz', 'nr4', 'ul. Wschodnia 13', 55000, 'USD', true);\n" +
                "INSERT INTO Clients (name, number, address, amountCents, amountCurrency, active) VALUES ('Zdzisław Papryka', 'nr5', 'ul. Niecała 12', 0, 'USD', false);\n" +
                "INSERT INTO Clients (name, number, address, amountCents, amountCurrency, active) VALUES ('Jerzy Rawski', 'nr6', 'ul. Północna 23/3', 15000, 'PLN', true);\n" +
                "INSERT INTO Clients (name, number, address, amountCents, amountCurrency, active) VALUES ('Maria Ronikier', 'nr7', 'ul. Złota 13/1', 35000, 'EUR', true);\n" +
                "INSERT INTO Clients (name, number, address, amountCents, amountCurrency, active) VALUES ('Agata Reszka', 'nr8', 'ul. Ułanów 17/30', 0, 'PLN', false);\n" +
                "INSERT INTO Clients (name, number, address, amountCents, amountCurrency, active) VALUES ('Ryszard Dębiński', 'nr9', 'ul. Kawaleryjska 44/2', 75000, 'EUR', true);" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr1', 0, '2016-01-12 10:00:00');\n" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr2', 0, '2016-03-12 15:00:00');\n" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr3', 0, '2016-05-12 22:10:00');\n" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr4', 0, '2016-05-14 20:05:00');\n" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr5', 1, '2016-01-02 10:00:00');\n" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr6', 1, '2016-03-10 15:00:00');\n" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr7', 3, '2016-05-01 01:04:40');\n" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr8', 4, '2016-04-01 02:00:00');\n" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr9', 4, '2016-04-11 01:04:40');\n" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr10', 4, '2016-05-01 14:44:40');\n" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr11', 6, '2016-02-10 11:00:00');\n" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr12', 7, '2016-05-11 10:00:00');\n" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr13', 8, '2016-02-01 15:00:00');\n" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr14', 8, '2016-02-11 12:00:00');\n" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr15', 8, '2016-05-10 13:00:00');\n" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr16', 8, '2016-05-11 14:00:00');\n" +
                "INSERT INTO Purchases (number, clientId, createDate) VALUES ('nr17', 8, '2016-05-12 15:00:00');" +
                "INSERT INTO Statuses (status_id, status_name) VALUES (0, 'standard');" +
                "INSERT INTO Statuses (status_id, status_name) VALUES (1, 'silver');" +
                "INSERT INTO Statuses (status_id, status_name) VALUES (2, 'gold');" +
                "INSERT INTO Statuses (status_id, status_name) VALUES (3, 'platinum');" +
                "INSERT INTO Statuses (status_id, status_name) VALUES (4, 'vip');" +
                "INSERT INTO ClientsStatuses (status_id, client_id) VALUES (1, 0);" +
                "INSERT INTO ClientsStatuses (status_id, client_id) VALUES (2, 0);" +
                "INSERT INTO ClientsStatuses (status_id, client_id) VALUES (3, 0);" +
                "INSERT INTO ClientsStatuses (status_id, client_id) VALUES (4, 0);" +
                "INSERT INTO ClientsStatuses (status_id, client_id) VALUES (5, 0);" +
                "INSERT INTO ClientsStatuses (status_id, client_id) VALUES (6, 0);" +
                "INSERT INTO ClientsStatuses (status_id, client_id) VALUES (7, 0);" +
                "INSERT INTO ClientsStatuses (status_id, client_id) VALUES (8, 0);" +
                "INSERT INTO ClientsStatuses (status_id, client_id) VALUES (9, 0);" +
                "INSERT INTO PurchasesProducts VALUES (0, 0);\n" +
                "INSERT INTO PurchasesProducts VALUES (0, 1);\n" +
                "INSERT INTO PurchasesProducts VALUES (0, 4);\n" +
                "INSERT INTO PurchasesProducts VALUES (0, 5);\n" +
                "INSERT INTO PurchasesProducts VALUES (1, 14);\n" +
                "INSERT INTO PurchasesProducts VALUES (1, 15);\n" +
                "INSERT INTO PurchasesProducts VALUES (2, 2);\n" +
                "INSERT INTO PurchasesProducts VALUES (2, 3);\n" +
                "INSERT INTO PurchasesProducts VALUES (2, 4);\n" +
                "INSERT INTO PurchasesProducts VALUES (3, 4);\n" +
                "INSERT INTO PurchasesProducts VALUES (4, 4);\n" +
                "INSERT INTO PurchasesProducts VALUES (5, 14);\n" +
                "INSERT INTO PurchasesProducts VALUES (5, 15);\n" +
                "INSERT INTO PurchasesProducts VALUES (5, 0);\n" +
                "INSERT INTO PurchasesProducts VALUES (5, 1);\n" +
                "INSERT INTO PurchasesProducts VALUES (5, 3);\n" +
                "INSERT INTO PurchasesProducts VALUES (5, 4);\n" +
                "INSERT INTO PurchasesProducts VALUES (5, 5);\n" +
                "INSERT INTO PurchasesProducts VALUES (6, 14);\n" +
                "INSERT INTO PurchasesProducts VALUES (6, 13);\n" +
                "INSERT INTO PurchasesProducts VALUES (7, 11);\n" +
                "INSERT INTO PurchasesProducts VALUES (8, 8);\n" +
                "INSERT INTO PurchasesProducts VALUES (8, 9);\n" +
                "INSERT INTO PurchasesProducts VALUES (8, 10);\n" +
                "INSERT INTO PurchasesProducts VALUES (9, 0);\n" +
                "INSERT INTO PurchasesProducts VALUES (9, 7);\n" +
                "INSERT INTO PurchasesProducts VALUES (10, 1);\n" +
                "INSERT INTO PurchasesProducts VALUES (11, 1);\n" +
                "INSERT INTO PurchasesProducts VALUES (12, 7);\n" +
                "INSERT INTO PurchasesProducts VALUES (12, 8);\n" +
                "INSERT INTO PurchasesProducts VALUES (12, 9);\n" +
                "INSERT INTO PurchasesProducts VALUES (13, 4);\n" +
                "INSERT INTO PurchasesProducts VALUES (13, 5);\n" +
                "INSERT INTO PurchasesProducts VALUES (14, 9);\n" +
                "INSERT INTO PurchasesProducts VALUES (15, 10);\n" +
                "INSERT INTO PurchasesProducts VALUES (16, 0);\n" +
                "INSERT INTO PurchasesProducts VALUES (16, 3);");
    }

    @Test
    public void shouldLoadPurchase() throws Exception {
        //given
        Client client = ClientFactory.getClientInstance("nr1", "Jan Nowak", "ul. Koralowa 10", new Money(10000, PLN), STANDARD, true);
        Money money = new Money(950, USD);

        //when
        Purchase loadedPurchase = purchaseRepository.load("nr1");

        //then
        assertEquals(money, loadedPurchase.getTotalCost());
        assertEquals(client, loadedPurchase.getOwner());
        assertEquals("nr1", loadedPurchase.getNumber());
    }

    @Test
    public void shouldSavePurchase() throws Exception {
        //given
        Client client = ClientFactory.getClientInstance("nr1", "Jan Nowak", "ul. Koralowa 10", new Money(10000, PLN), STANDARD, true);
        Product product = productRepository.load("nr1");
        Reservation reservation = new Reservation(client);
        reservation.add(product);
        Offer offer = reservation.generateOffer();
        Purchase savedPurchase = new Purchase(client, offer.getItems());

        //when
        purchaseRepository.save(savedPurchase);

        //then
        Purchase loadedPurchase = purchaseRepository.load(savedPurchase.getNumber());
        assertEquals(savedPurchase, loadedPurchase);
    }

}