package org.example.demo;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcPostgresJavaFx extends Application {

    // Цвета
    private static final String DARK_GRAY = "#2d3436";       // фон приложения
    private static final String LIGHT_GRAY = "#636e72";      // фон списка, заголовков, левой части
    private static final String LIGHTER_GRAY = "#b2bec3";    // фон правой части
    private static final String TEAL = "#00cec9";            // акцент
    private static final String WHITE = "#ffffff";

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();

        // Стилизация вкладок (CSS подключайте отдельным файлом, как обсуждали)
        tabPane.setStyle("-fx-background-color: " + DARK_GRAY + ";"
                + "-fx-tab-min-width: 120;"
                + "-fx-tab-max-width: 180;"
                + "-fx-tab-min-height: 36;"
                + "-fx-tab-max-height: 36;"
        );
        // tabPane.getStylesheets().add(getClass().getResource("/tabs.css").toExternalForm());

        Tab moviesTab = createTab("Фильмы");
        Tab seriesTab = createTab("Сериалы");
        Tab gamesTab = createTab("Игры");
        Tab viewTab = createViewTab();

        tabPane.getTabs().addAll(moviesTab, seriesTab, gamesTab, viewTab);

        VBox vbox = new VBox(tabPane);
        vbox.setStyle("-fx-background-color: " + DARK_GRAY + ";");
        Scene scene = new Scene(vbox, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JDBC with PostgreSQL and JavaFX Example");
        primaryStage.show();
    }

    private Tab createTab(String title) {
        Tab tab = new Tab(title);
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-background-color: " + DARK_GRAY + ";");

        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField descriptionField = new TextField();
        TextField genreField = new TextField();
        Button addButton = new Button("Добавить");
        addButton.setStyle("-fx-background-color: " + TEAL + "; -fx-text-fill: " + WHITE + "; -fx-background-radius: 10;");

        Label label = new Label("ID:");
        label.setStyle("-fx-text-fill: " + WHITE + ";");
        grid.add(label, 0, 0);
        idField.setStyle("-fx-control-inner-background: " + DARK_GRAY + "; -fx-text-fill: " + WHITE + ";");
        grid.add(idField, 1, 0);

        label = new Label("Name:");
        label.setStyle("-fx-text-fill: " + WHITE + ";");
        grid.add(label, 0, 1);
        nameField.setStyle("-fx-control-inner-background: " + DARK_GRAY + "; -fx-text-fill: " + WHITE + ";");
        grid.add(nameField, 1, 1);

        label = new Label("Description:");
        label.setStyle("-fx-text-fill: " + WHITE + ";");
        grid.add(label, 0, 2);
        descriptionField.setStyle("-fx-control-inner-background: " + DARK_GRAY + "; -fx-text-fill: " + WHITE + ";");
        grid.add(descriptionField, 1, 2);

        label = new Label("Genre:");
        label.setStyle("-fx-text-fill: " + WHITE + ";");
        grid.add(label, 0, 3);
        genreField.setStyle("-fx-control-inner-background: " + DARK_GRAY + "; -fx-text-fill: " + WHITE + ";");
        grid.add(genreField, 1, 3);

        grid.add(addButton, 1, 4);

        addButton.setOnAction(event -> {
            String tableName = title.equals("Фильмы") ? "movies" : title.equals("Сериалы") ? "series" : "games";
            addDataToDatabase(tableName, idField.getText(), nameField.getText(), descriptionField.getText(), genreField.getText());
            idField.clear();
            nameField.clear();
            descriptionField.clear();
            genreField.clear();
        });

        tab.setContent(grid);
        return tab;
    }

    private Tab createViewTab() {
        Tab tab = new Tab("Просмотр");

        TableView<DataItem> tableView = new TableView<>();

        // Стилизация TableView
        tableView.setStyle("-fx-background-color: #b2bec3; -fx-text-fill: #2d3436;");

        // Стилизация строк
        tableView.setRowFactory(tv -> new TableRow<DataItem>() {
            @Override
            protected void updateItem(DataItem item, boolean empty) {
                super.updateItem(item, empty);
                setStyle("-fx-background-color: #636e72; -fx-text-fill: #ffffff;");
            }
        });

        // Стилизация заголовков
        TableColumn<DataItem, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        idColumn.setStyle("-fx-background-color: #636e72; -fx-text-fill: #ffffff;");

        TableColumn<DataItem, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameColumn.setStyle("-fx-background-color: #636e72; -fx-text-fill: #ffffff;");

        TableColumn<DataItem, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        descriptionColumn.setStyle("-fx-background-color: #636e72; -fx-text-fill: #ffffff;");

        TableColumn<DataItem, String> genreColumn = new TableColumn<>("Genre");
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().genreProperty());
        genreColumn.setStyle("-fx-background-color: #636e72; -fx-text-fill: #ffffff;");

        tableView.getColumns().addAll(idColumn, nameColumn, descriptionColumn, genreColumn);

        // Стилизация кнопок
        String LOAD_BUTTON_STYLE = "-fx-background-color: #b2bec3; -fx-text-fill: #222b2e; -fx-background-radius: 10;";
        String DELETE_BUTTON_STYLE = "-fx-background-color: #ff7675; -fx-text-fill: #222b2e; -fx-background-radius: 10;";

        // Кнопки для фильмов
        Button loadMoviesButton = new Button("Загрузить Фильмы");
        loadMoviesButton.setStyle(LOAD_BUTTON_STYLE);
        Button deleteMoviesButton = new Button("Удалить");
        deleteMoviesButton.setStyle(DELETE_BUTTON_STYLE);

        // Кнопки для сериалов
        Button loadSeriesButton = new Button("Загрузить Сериалы");
        loadSeriesButton.setStyle(LOAD_BUTTON_STYLE);
        Button deleteSeriesButton = new Button("Удалить");
        deleteSeriesButton.setStyle(DELETE_BUTTON_STYLE);

        // Кнопки для игр
        Button loadGamesButton = new Button("Загрузить Игры");
        loadGamesButton.setStyle(LOAD_BUTTON_STYLE);
        Button deleteGamesButton = new Button("Удалить");
        deleteGamesButton.setStyle(DELETE_BUTTON_STYLE);

        // Обработчики загрузки
        loadMoviesButton.setOnAction(event -> loadDataFromDatabase("movies", tableView));
        loadSeriesButton.setOnAction(event -> loadDataFromDatabase("series", tableView));
        loadGamesButton.setOnAction(event -> loadDataFromDatabase("games", tableView));

        // Обработчики удаления
        deleteMoviesButton.setOnAction(event -> deleteSelectedRowFromDatabase("movies", tableView));
        deleteSeriesButton.setOnAction(event -> deleteSelectedRowFromDatabase("series", tableView));
        deleteGamesButton.setOnAction(event -> deleteSelectedRowFromDatabase("games", tableView));

        // Кнопки в горизонтальных рядах
        HBox moviesBox = new HBox(5, loadMoviesButton, deleteMoviesButton);
        HBox seriesBox = new HBox(5, loadSeriesButton, deleteSeriesButton);
        HBox gamesBox = new HBox(5, loadGamesButton, deleteGamesButton);

        // Удаление лишнего пространства в таблице
        tableView.setFixedCellSize(30);
        tableView.prefHeightProperty().bind(
                tableView.fixedCellSizeProperty().multiply(javafx.beans.binding.Bindings.size(tableView.getItems()).add(1.01))
        );
        tableView.minHeightProperty().bind(tableView.prefHeightProperty());
        tableView.maxHeightProperty().bind(tableView.prefHeightProperty());
        VBox.setVgrow(tableView, Priority.NEVER);

        VBox vbox = new VBox(moviesBox, seriesBox, gamesBox, tableView);
        vbox.setSpacing(5);
        vbox.setStyle("-fx-background-color: #b2bec3;");
        tab.setContent(vbox);
        return tab;
    }

    // Метод для удаления строки из базы и таблицы
    private void deleteSelectedRowFromDatabase(String tableName, TableView<DataItem> tableView) {
        DataItem selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String id = selectedItem.idProperty().get();
            String sql = String.format("DELETE FROM %s WHERE id = '%s'", tableName, id);
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
                tableView.getItems().remove(selectedItem);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void loadDataFromDatabase(String tableName, TableView<DataItem> tableView) {
        List<DataItem> dataItems = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s", tableName);
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String genre = resultSet.getString("genre");
                dataItems.add(new DataItem(id, name, description, genre));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.getItems().setAll(dataItems);
    }

    private void addDataToDatabase(String tableName, String id, String name, String description, String genre) {
        String sql = String.format("INSERT INTO %s (id, name, description, genre) VALUES ('%s', '%s', '%s', '%s')",
                tableName, id, name, description, genre);
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// DataItem класс без изменений
