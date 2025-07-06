package org.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcPostgresJavaFx extends Application {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();

        Tab moviesTab = createTab("Фильмы");
        Tab seriesTab = createTab("Сериалы");
        Tab gamesTab = createTab("Игры");
        Tab viewTab = createViewTab();

        tabPane.getTabs().addAll(moviesTab, seriesTab, gamesTab, viewTab);

        VBox vbox = new VBox(tabPane);
        Scene scene = new Scene(vbox, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JDBC with PostgreSQL and JavaFX Example");
        primaryStage.show();
    }

    private Tab createTab(String title) {
        Tab tab = new Tab(title);
        GridPane grid = new GridPane();

        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField descriptionField = new TextField();
        TextField genreField = new TextField();
        Button addButton = new Button("Добавить");

        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descriptionField, 1, 2);
        grid.add(new Label("Genre:"), 0, 3);
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

        TableColumn<DataItem, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());

        TableColumn<DataItem, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<DataItem, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        TableColumn<DataItem, String> genreColumn = new TableColumn<>("Genre");
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().genreProperty());

        tableView.getColumns().addAll(idColumn, nameColumn, descriptionColumn, genreColumn);

        Button loadMoviesButton = new Button("Загрузить Фильмы");
        loadMoviesButton.setOnAction(event -> loadDataFromDatabase("movies", tableView));

        Button loadSeriesButton = new Button("Загрузить Сериалы");
        loadSeriesButton.setOnAction(event -> loadDataFromDatabase("series", tableView));

        Button loadGamesButton = new Button("Загрузить Игры");
        loadGamesButton.setOnAction(event -> loadDataFromDatabase("games", tableView));

        VBox vbox = new VBox(loadMoviesButton, loadSeriesButton, loadGamesButton, tableView);
        tab.setContent(vbox);
        return tab;
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
        String sql = String.format("INSERT INTO %s (id, name, description, genre) VALUES (%s, '%s', '%s', '%s')",
                tableName, id, name, description, genre);
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
