package com.example.view;

import com.example.DBUtils.JPAUtil;
import com.example.controller.CustomerController;
import com.example.controller.DeliveryManController;
import com.example.model.Customer;
import com.example.model.DeliveryMan;

import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

public class DeliveryManView {
    private static TableView<DeliveryMan> tableView;
    private static ObservableList<DeliveryMan> deliveryManList = FXCollections.observableArrayList();
    private final FilteredList<DeliveryMan> filteredDeliveryManList = new FilteredList<>(deliveryManList, p -> true);
    private final SortedList<DeliveryMan> sortedDeliveryManList = new SortedList<>(filteredDeliveryManList);
    
    

    public Tab getView() {
        tableView = new TableView<>();
        DeliveryManController dmController = new DeliveryManController();
        
        // Columns
        TableColumn<DeliveryMan, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));

        TableColumn<DeliveryMan, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));

        TableColumn<DeliveryMan, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPhoneNumber()));

        tableView.getColumns().addAll(idCol, nameCol, phoneCol);

        tableView.setItems(sortedDeliveryManList); // Bind sorted + filtered list
        sortedDeliveryManList.comparatorProperty().bind(tableView.comparatorProperty());

        //Searchbar
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or phone number");
        
        // // Sort Choice
        // ComboBox<String> sortBox = new ComboBox<>();
        // sortBox.getItems().addAll("Name Asc", "Name Desc", "Phone Asc", "Phone Desc");
        // sortBox.getSelectionModel().selectFirst();
        

        // Filter logic
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.trim().toLowerCase();
            filteredDeliveryManList.setPredicate(deliveryMan ->
                deliveryMan.getName().toLowerCase().contains(filter) ||
                deliveryMan.getPhoneNumber().toLowerCase().contains(filter)
            );
        });
        // double click ui column name to sort desc asc.
        // // Sort logic
        // sortBox.setOnAction(e -> {
        //     switch (sortBox.getValue()) {
        //         case "Name Asc" -> tableView.getSortOrder().setAll(nameCol);
        //         case "Name Desc" -> {
        //             nameCol.setSortType(TableColumn.SortType.DESCENDING);
        //             tableView.getSortOrder().setAll(nameCol);
        //         }
        //         case "Phone Asc" -> tableView.getSortOrder().setAll(phoneCol);
        //         case "Phone Desc" -> {
        //             phoneCol.setSortType(TableColumn.SortType.DESCENDING);
        //             tableView.getSortOrder().setAll(phoneCol);
        //         }
        //     }
        // });

        // Form
        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");

        //Buttons
        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");

        HBox form = new HBox(10, nameField, phoneField, addButton, updateButton, deleteButton);
        HBox search = new HBox(10, searchField);
        VBox layout = new VBox(10,search, tableView, form);
        layout.setStyle("-fx-padding: 10");

        // Load all customers on startup
        loadDMan();

        

        // Add
        addButton.setOnAction(e -> {
            try{
                DeliveryMan dman = new DeliveryMan();
                dman.setName(nameField.getText());
                dman.setPhoneNumber(phoneField.getText());
                dmController.addDeliveryMan(dman);
                System.out.println("Adding new delivery man");
                nameField.setText(null);    
                phoneField.setText(null);
                loadDMan();
            } catch (Exception exception){
                showAlert("Error while adding new delivery man", exception.getMessage());
            }
            
        });

        // Update
        updateButton.setOnAction(e -> {
            DeliveryMan selected = tableView.getSelectionModel().getSelectedItem();
            try{
                selected.setName(nameField.getText());
                selected.setPhoneNumber(phoneField.getText());
                dmController.updateDeliveryMan(selected);
                System.out.println("Updating delivery man");
                nameField.setText(null);
                phoneField.setText(null);
                loadDMan();
            } catch (Exception exception){
                showAlert("Error while updating delivery man", exception.getMessage());
            }
            
        });

        // Delete
        deleteButton.setOnAction(e -> {
            DeliveryMan selected = tableView.getSelectionModel().getSelectedItem();
            try{
                dmController.deleteDeliveryMan(selected.getId());
                System.out.println("Deleting delivery man");
                loadDMan();
            } catch (Exception exception){
                showAlert("Error while deleting delivery man", exception.getMessage());
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                nameField.setText(newSel.getName());
                phoneField.setText(newSel.getPhoneNumber());
                addButton.setDisable(true);
            }
        });
        tableView.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                tableView.getSelectionModel().clearSelection();
                addButton.setDisable(false);
                nameField.setText(null);
                phoneField.setText(null);
            }
        });
        Tab tab = new Tab("DeliveryMan", layout);
        return tab;
    }

    private static void loadDMan() {
        DeliveryManController dManController = new DeliveryManController();
        deliveryManList.setAll(dManController.getAllDeliveryMan());
    }

    private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
    }
}
