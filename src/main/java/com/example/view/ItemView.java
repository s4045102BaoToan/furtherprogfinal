package com.example.view;

import com.example.controller.*;
import com.example.model.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ItemView {
    private static TableView<Item> tableView;
    private static ObservableList<Item> itemList = FXCollections.observableArrayList();
    private final FilteredList<Item> filteredItems = new FilteredList<>(itemList, p -> true);
    private final SortedList<Item> sortedItems = new SortedList<>(filteredItems);
    
    

    public Tab getView() {
        tableView = new TableView<>();
        ItemController itemController = new ItemController();
        
        // Columns
        TableColumn<Item, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));

        TableColumn<Item, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));

        TableColumn<Item, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getPrice()).asObject());

        tableView.getColumns().addAll(idCol, nameCol, priceCol);

        tableView.setItems(sortedItems); // Bind sorted + filtered list
        sortedItems.comparatorProperty().bind(tableView.comparatorProperty());

        //Searchbar
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or price");
        
        // // Sort Choice
        // ComboBox<String> sortBox = new ComboBox<>();
        // sortBox.getItems().addAll("Name Asc", "Name Desc", "Price Asc", "Price Desc");
        // sortBox.getSelectionModel().selectFirst();
        

        // Filter logic
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.trim().toLowerCase();
            filteredItems.setPredicate(item -> {
                // Match name
                boolean matchesName = item.getName().toLowerCase().contains(filter);

                // Match price - format it to 2 decimal 
                String formattedPrice = String.format("%.3f", item.getPrice());
                boolean matchesPrice = formattedPrice.contains(filter);

                return matchesName || matchesPrice;
            });
        });
        // Double click column name in UI to sort asc, desc
        // // Sort logic 
        // sortBox.setOnAction(e -> {
        //     switch (sortBox.getValue()) {
        //         case "Name Asc" -> tableView.getSortOrder().setAll(nameCol);
        //         case "Name Desc" -> {
        //             nameCol.setSortType(TableColumn.SortType.DESCENDING);
        //             tableView.getSortOrder().setAll(nameCol);
        //         }
        //         case "Price Asc" -> tableView.getSortOrder().setAll(priceCol);
        //         case "Price Desc" -> {
        //             priceCol.setSortType(TableColumn.SortType.DESCENDING);
        //             tableView.getSortOrder().setAll(priceCol);
        //         }
        //     }
        // });

        // Form
        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        //Buttons
        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");

        HBox form = new HBox(10, nameField, priceField, addButton, updateButton, deleteButton);
        HBox search = new HBox(10, searchField);
        VBox layout = new VBox(10,search, tableView, form);
        layout.setStyle("-fx-padding: 10");

        // Load all items on startup
        loadItems();

        // Add
        addButton.setOnAction(e -> {
            try{
                Item item = new Item();
                item.setName(nameField.getText());
                double price = Double.parseDouble(priceField.getText().trim());
                item.setPrice(price);
                itemController.addItem(item);
                System.out.println("Adding item");
                nameField.setText(null);    
                priceField.setText(null);
                loadItems();
            } catch (Exception exception){
                showAlert("Error while adding item", exception.getMessage());
            }
            
        });

        // Update
        updateButton.setOnAction(e -> {
            Item selected = tableView.getSelectionModel().getSelectedItem();
            try{
                selected.setName(nameField.getText());
                double price = Double.parseDouble(priceField.getText().trim());
                selected.setPrice(price);
                itemController.updateItem(selected);
                System.out.println("Updating item");
                nameField.setText(null);    
                priceField.setText(null);
                loadItems();
            } catch (Exception exception){
                showAlert("Error while updating item", exception.getMessage());
            }
            
        });

        // Delete
        deleteButton.setOnAction(e -> {
            Item selected = tableView.getSelectionModel().getSelectedItem();
            try{
                itemController.deleteItem(selected.getId());
                System.out.println("Deleting customer");
                loadItems();
            } catch (Exception exception){
                showAlert("Error while deleting item", exception.getMessage());
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                nameField.setText(newSel.getName());
                priceField.setText(newSel.getPrice().toString());
                addButton.setDisable(true);
            }
        });
        tableView.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                tableView.getSelectionModel().clearSelection();
                addButton.setDisable(false);
                nameField.setText(null);
                priceField.setText(null);
            }
        });
        Tab tab = new Tab("Item", layout);
        return tab;
    }

    private static void loadItems() {
        ItemController itemController = new ItemController();
        itemList.setAll(itemController.getAllItems());
    }

    private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
    }
}
