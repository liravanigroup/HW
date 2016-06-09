package pl.com.bottega.photostock.sales.infrastructure.repositories;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.com.bottega.photostock.sales.model.deal.Money;
import pl.com.bottega.photostock.sales.model.products.Picture;
import pl.com.bottega.photostock.sales.model.products.Product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


public class JdbcProductRepositoryTest {
    private Repository<Product> repo;

    @Before
    public void setUp() throws Exception {
        // given
        Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:stockdb", "SA", "");
        dropTables(connection);
        createProductsTable(connection);
        createTagsTable(connection);
        insertTestProduct(connection);
        connection.close();

        // when
        repo = new JdbcProductRepository("jdbc:hsqldb:mem:stockdb", "SA", "");
    }

    private void dropTables(Connection connection) throws SQLException {
        connection.createStatement().executeUpdate("DROP TABLE ProductsTags IF EXISTS;");
        connection.createStatement().executeUpdate("DROP TABLE Tags IF EXISTS;");
        connection.createStatement().executeUpdate("DROP TABLE Products IF EXISTS;");
    }

    private void createTagsTable(Connection connection) throws Exception {

        connection.createStatement().executeUpdate("CREATE TABLE Tags (\n" +
                "  id INTEGER IDENTITY PRIMARY KEY,\n" +
                "  name VARCHAR(255) NOT NULL\n" +
                ");");

        connection.createStatement().executeUpdate("CREATE TABLE ProductsTags (\n" +
                "  productId INTEGER FOREIGN KEY REFERENCES Products(id),\n" +
                "  tagId INTEGER FOREIGN KEY REFERENCES Tags(id),\n" +
                "  PRIMARY KEY (productId, tagId)\n" +
                ");");
    }

    @Test
    public void shouldLoadProductByNumber() throws SQLException {
        // when
        Product product = repo.load("nr1");

        // then
        assertEquals("nr1", product.getNumber());
        assertEquals(Picture.class, product.getClass());
    }

    @Test
    public void shouldReturnNullWhenProductDoesNotExists() throws SQLException {
        // when
        Product product2 = repo.load("nr14");

        //then
        Assert.assertNull(product2);
    }

    private void insertTestProduct(Connection connection) throws SQLException {
        connection.createStatement().executeUpdate("INSERT INTO Products (number, name, available, priceCents, priceCurrency, length, type) VALUES ('nr1','Mazda 6', true, 250, 'USD', NULL, 'Picture');");
    }

    private void createProductsTable(Connection connection) throws SQLException {
        connection.createStatement().executeUpdate("CREATE TABLE Products (\n" +
                "  id INTEGER IDENTITY PRIMARY KEY,\n" +
                "  name VARCHAR(255) NOT NULL,\n" +
                "  number VARCHAR(20) NOT NULL,\n" +
                "  priceCents DOUBLE DEFAULT 0 NOT NULL,\n" +
                "  priceCurrency CHAR(3) DEFAULT 'PLN' NOT NULL,\n" +
                "  available BOOLEAN DEFAULT true NOT NULL,\n" +
                "  type VARCHAR(20) NOT NULL,\n" +
                "  length BIGINT\n" +
                ");");
    }

    @Test
    public void shouldSaveProduct() {
        // given
        Product saved = new Picture("Name", "nr122", Money.FIVE_PL, true, "Tag1");
        repo.save(saved);

        //When
        Product loadedProduct = repo.load(saved.getNumber());

        //then
        Assert.assertEquals(saved.getNumber(), loadedProduct.getNumber());
    }

    @Test
    public void shouldSaveProductWithTags() {
        // given
        Product saved = new Picture("Name", "nr122", Money.FIVE_PL, true, "Tag1", "Tag3");

        //When
        repo.save(saved);

        //then
        Product loadedProduct = repo.load(saved.getNumber());

        assertArrayEquals(saved.getTags(), loadedProduct.getTags());
    }

    @Test
    public void shouldUpdateProductWhenTagsCountIsMore(){
        // given
        Product savedPicture = new Picture("Name", "nr122", Money.FIVE_PL, true, "Tag1", "Tag3");
        Product updatePicture = new Picture("Name", "nr122", Money.FIVE_PL, true, "Tag3", "Tag1", "Tag2", "Tag4");

        //When
        repo.save(savedPicture);
        repo.save(updatePicture);

        //then
        Product loadedProduct = repo.load(savedPicture.getNumber());

        assertEquals(updatePicture,loadedProduct);
    }

    @Test
    public void shouldUpdateProductWhenTagsCountTsLess(){
        // given
        Product savedPicture = new Picture("Name", "nr122", Money.FIVE_PL, true, "Tag3", "Tag1", "Tag2", "Tag4");
        Product updatePicture = new Picture("Name", "nr122", Money.FIVE_PL, true, "Tag1", "Tag3");

        //When
        repo.save(savedPicture);
        repo.save(updatePicture);

        //then
        Product loadedProduct = repo.load(savedPicture.getNumber());

        assertEquals(updatePicture,loadedProduct);
    }


    @Test
    public void shouldUpdateProductWithShackedTags(){
        // given
        Product savedPicture = new Picture("Name1", "nr12", Money.FIVE_PL, true, "Tag1", "Tag3");
        Product updatePicture = new Picture("Name2", "nr12", Money.FIVE_PL, true, "Tag3", "Tag1");

        //When
        repo.save(savedPicture);
        repo.save(updatePicture);

        //then
        Product loadedProduct = repo.load(savedPicture.getNumber());

        assertEquals("Name2", loadedProduct.getName());
        assertEquals(updatePicture,loadedProduct);
    }

    @Test
    public void shouldUpdateProductWithoutTags(){
        // given
        Product savedPicture = new Picture("Name1", "nr12", Money.FIVE_PL, true);
        Product updatePicture = new Picture("Name2", "nr12", Money.FIVE_PL, true, "Tag3", "Tag1");

        //When
        repo.save(savedPicture);
        repo.save(updatePicture);

        //then
        Product loadedProduct = repo.load(savedPicture.getNumber());

        assertEquals(updatePicture,loadedProduct);
    }

    @Test
    public void shouldUpdateProductWithoutTags2(){
        // given
        Product savedPicture = new Picture("Name1", "nr12", Money.FIVE_PL, true);
        Product updatePicture = new Picture("Name2", "nr12", Money.FIVE_PL, true);

        //When
        repo.save(savedPicture);
        repo.save(updatePicture);

        //then
        Product loadedProduct = repo.load(savedPicture.getNumber());

        assertEquals(updatePicture,loadedProduct);
    }

}
