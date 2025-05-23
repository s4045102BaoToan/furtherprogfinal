package com.example.view;

import com.example.DBUtils.JPAUtil;
import com.example.controller.CustomerController;
import com.example.model.Customer;

import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

public class CustomerView {
    private static TableView<Customer> tableView;
    private static ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private final FilteredList<Customer> filteredCustomers = new FilteredList<>(customerList, p -> true);
    private final SortedList<Customer> sortedCustomers = new SortedList<>(filteredCustomers);
    
    

    public Tab getView() {
        tableView = new TableView<>();
        CustomerController customerController = new CustomerController();
        
        // Columns
        TableColumn<Customer, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));

        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPhoneNumber()));

        TableColumn<Customer, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getAddress()));

        tableView.getColumns().addAll(idCol, nameCol, phoneCol, addressCol);

        tableView.setItems(sortedCustomers); // Bind sorted + filtered list
        sortedCustomers.comparatorProperty().bind(tableView.comparatorProperty());

        //Searchbar
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or phone number");
        
        // Sort Choice
        ComboBox<String> sortBox = new ComboBox<>();
        sortBox.getItems().addAll("Name Asc", "Name Desc", "Phone Asc", "Phone Desc");
        sortBox.getSelectionModel().selectFirst();
        

        // Filter logic
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.trim().toLowerCase();
            filteredCustomers.setPredicate(customer ->
                customer.getName().toLowerCase().contains(filter) ||
                customer.getPhoneNumber().toLowerCase().contains(filter)
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

        TextField addressField = new TextField();
        addressField.setPromptText("Address");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");

        //Buttons
        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");

        HBox form = new HBox(10, nameField, phoneField, addressField, addButton, updateButton, deleteButton);
        HBox search = new HBox(10, searchField, sortBox);
        VBox layout = new VBox(10,search, tableView, form);
        layout.setStyle("-fx-padding: 10");

        // Load all customers on startup
        loadCustomers();

        

        // Add
        addButton.setOnAction(e -> {
            try{
                Customer customer = new Customer();
                customer.setName(nameField.getText());
                customer.setAddress(addressField.getText());
                customer.setPhoneNumber(phoneField.getText());
                customerController.addCustomer(customer);
                System.out.println("Adding customer");
                nameField.setText(null);    
                addressField.setText(null);
                phoneField.setText(null);
                loadCustomers();
            } catch (Exception exception){
                showAlert("Error while adding customer", exception.getMessage());
            }
            
        });

        // Update
        updateButton.setOnAction(e -> {
            Customer selected = tableView.getSelectionModel().getSelectedItem();
            try{
                selected.setName(nameField.getText());
                selected.setPhoneNumber(phoneField.getText());
                selected.setAddress(addressField.getText());
                customerController.updateCustomer(selected);
                System.out.println("Updating customer");
                nameField.setText(null);
                addressField.setText(null);
                phoneField.setText(null);
                loadCustomers();
            } catch (Exception exception){
                showAlert("Error while updating customer", exception.getMessage());
            }
            
        });

        // Delete
        deleteButton.setOnAction(e -> {
            Customer selected = tableView.getSelectionModel().getSelectedItem();
            try{
                customerController.deleteCustomer(selected.getId());
                System.out.println("Deleting customer");
                loadCustomers();
            } catch (Exception exception){
                showAlert("Error while deleting customer", exception.getMessage());
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                nameField.setText(newSel.getName());
                phoneField.setText(newSel.getPhoneNumber());
                addressField.setText(newSel.getAddress());
                addButton.setDisable(true);
            }
        });
        tableView.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                tableView.getSelectionModel().clearSelection();
                addButton.setDisable(false);
                nameField.setText(null);
                addressField.setText(null);
                phoneField.setText(null);
            }
        });
        Tab tab = new Tab("Customer", layout);
        return tab;
    }

    private static void loadCustomers() {
        CustomerController customerController = new CustomerController();
        customerList.setAll(customerController.getAllCustomers());
    }

    private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
    }
}

