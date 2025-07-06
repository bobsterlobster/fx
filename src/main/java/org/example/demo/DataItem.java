package org.example.demo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DataItem {
    private final SimpleStringProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty description;
    private final SimpleStringProperty genre;

    public DataItem(String id, String name, String description, String genre) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.genre = new SimpleStringProperty(genre);
    }

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty genreProperty() {
        return genre;
    }
}
