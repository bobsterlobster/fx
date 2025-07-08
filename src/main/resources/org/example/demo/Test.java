package org.example.demo;

import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel; // Для инициализации JavaFX в тестах
import javafx.scene.control.TableView;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JdbcPostgresJavaFxTest {

    private JdbcPostgresJavaFx app;
    private TableView<DataItem> tableView;

    private static final String TEST_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String TEST_USER = "postgres";
    private static final String TEST_PASSWORD = "postgres";

    @BeforeAll
    public void setup() throws SQLException {
        // Инициализация JavaFX (нужно для тестирования JavaFX классов)
        new JFXPanel();

        app = new JdbcPostgresJavaFx();

        // Создаем тестовую таблицу movies (если нет)
        try (Connection conn = DriverManager.getConnection(TEST_URL, TEST_USER, TEST_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS movies (id VARCHAR PRIMARY KEY, name VARCHAR, description TEXT, genre VARCHAR)");
            stmt.execute("DELETE FROM movies"); // Очистка перед тестом
        }

        tableView = new TableView<>();
    }

    @Test
    public void testAddDataToDatabase() throws SQLException {
        app.addDataToDatabase("movies", "test_id", "Test Movie", "A test description", "Test Genre");

        try (Connection conn = DriverManager.getConnection(TEST_URL, TEST_USER, TEST_PASSWORD);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM movies WHERE id = ?")) {
            ps.setString(1, "test_id");
            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next());
            assertEquals("Test Movie", rs.getString("name"));
        }
    }

    @Test
    public void testLoadDataFromDatabase() {
        // Добавим вручную запись в таблицу для загрузки
        try (Connection conn = DriverManager.getConnection(TEST_URL, TEST_USER, TEST_PASSWORD);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO movies (id, name, description, genre) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, "load_id");
            ps.setString(2, "Load Movie");
            ps.setString(3, "Load description");
            ps.setString(4, "Load genre");
            ps.executeUpdate();
        } catch (SQLException e) {
            fail("Setup failed: " + e.getMessage());
        }

        app.loadDataFromDatabase("movies", tableView);

        ObservableList<DataItem> items = tableView.getItems();
        assertFalse(items.isEmpty());
        boolean found = items.stream().anyMatch(i -> i.idProperty().get().equals("load_id"));
        assertTrue(found);
    }

    @Test
    public void testDeleteSelectedRowFromDatabase() {
        // Добавляем запись для удаления
        try (Connection conn = DriverManager.getConnection(TEST_URL, TEST_USER, TEST_PASSWORD);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO movies (id, name, description, genre) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, "delete_id");
            ps.setString(2, "Delete Movie");
            ps.setString(3, "Delete description");
            ps.setString(4, "Delete genre");
            ps.executeUpdate();
        } catch (SQLException e) {
            fail("Setup failed: " + e.getMessage());
        }

        // Загружаем данные в таблицу
        app.loadDataFromDatabase("movies", tableView);

        // Выбираем нужный элемент
        DataItem itemToDelete = tableView.getItems().stream()
                .filter(i -> i.idProperty().get().equals("delete_id"))
                .findFirst().orElse(null);
        assertNotNull(itemToDelete);

        tableView.getSelectionModel().select(itemToDelete);

        app.deleteSelectedRowFromDatabase("movies", tableView);

        // Проверяем, что запись удалена из БД
        try (Connection conn = DriverManager.getConnection(TEST_URL, TEST_USER, TEST_PASSWORD);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM movies WHERE id = ?")) {
            ps.setString(1, "delete_id");
            ResultSet rs = ps.executeQuery();
            assertFalse(rs.next());
        } catch (SQLException e) {
            fail("Verification failed: " + e.getMessage());
        }
    }

    @Test
    public void testDataItemProperties() {
        DataItem item = new DataItem("id1", "Name1", "Desc1", "Genre1");
        assertEquals("id1", item.idProperty().get());
        assertEquals("Name1", item.nameProperty().get());
        assertEquals("Desc1", item.descriptionProperty().get());
        assertEquals("Genre1", item.genreProperty().get());

        // Проверка возможности изменения
        item.idProperty().set("id2");
        assertEquals("id2", item.idProperty().get());
    }

    @AfterAll
    public void cleanup() throws SQLException {
        // Очистка тестовых данных
        try (Connection conn = DriverManager.getConnection(TEST_URL, TEST_USER, TEST_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM movies");
        }
    }
}
