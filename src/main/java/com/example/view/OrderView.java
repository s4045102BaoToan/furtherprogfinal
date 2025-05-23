package com.example.view;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import javafx.util.*;
import com.example.controller.*;
import com.example.model.*;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class OrderView {
    private static TableView<Order> tableView;
    private static ObservableList<Order> orderList = FXCollections.observableArrayList();
    private final FilteredList<Order> filteredOrders = new FilteredList<>(orderList, p -> true);
    private final SortedList<Order> sortedOrders = new SortedList<>(filteredOrders);
    private Double totalCost; //Defined at line 128
    

    public Tab getView() {
        tableView = new TableView<>();
        OrderController orderController = new OrderController();        
        // Columns
        TableColumn<Order, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));

        TableColumn<Order, LocalDate> dateCol = new TableColumn<>("Date Created");
        dateCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getDateOfCreation()));

        TableColumn<Order, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCustomer().getName() + "(" + c.getValue().getCustomer().getPhoneNumber() +")"));

        TableColumn<Order, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getTotal()).asObject());

        //Uni directional, doesnt need this
        // TableColumn<Order, String> itemCol = new TableColumn<>("Items");
        // itemCol.setCellValueFactory(c -> {
        //     List<Item> items = c.getValue().getItems(); // Get the list of items from the order
        //     String itemNames = items.stream()
        //                             .map(Item::getName) // Assuming Item has getName()
        //                             .collect(Collectors.joining(", ")); // Join names with commas
        //     return new SimpleStringProperty(itemNames);
        // });

        TableColumn<Order, String> deliveryCol = new TableColumn<>("Delivery man");
        deliveryCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDeliveryMan().getName() + "(" + c.getValue().getDeliveryMan().getPhoneNumber() +")"));

        tableView.getColumns().addAll(idCol, dateCol, customerCol, totalCol, deliveryCol);

        tableView.setItems(sortedOrders); // Bind sorted + filtered list
        sortedOrders.comparatorProperty().bind(tableView.comparatorProperty());

        //Searchbar
        TextField searchField = new TextField();
        searchField.setPromptText("Search by id or date created");
        
        // // Sort Choice
        // ComboBox<String> sortBox = new ComboBox<>();
        // sortBox.getItems().addAll("Name Asc", "Name Desc", "Phone Asc", "Phone Desc");
        // sortBox.getSelectionModel().selectFirst();
        

        // Filter logic
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.trim().toLowerCase();
            filteredOrders.setPredicate(order -> {
                String idStr = String.valueOf(order.getId());
                String dateStr = order.getDateOfCreation().toString().toLowerCase(); // e.g. "2025-05-23"

                return idStr.contains(filter) || dateStr.contains(filter);
            });
        });

        // Form
        //Date
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());

        //Customer
        CustomerController customerController = new CustomerController();
        ObservableList<Customer> customers = FXCollections.observableArrayList(customerController.getAllCustomers());
        ComboBox<Customer> customerBox = new ComboBox<>(customers);
        customerBox.setPromptText("Select Customer");
        customerBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Customer customer, boolean empty) {
                super.updateItem(customer, empty);
                setText(empty || customer == null ? "" : customer.getName());
            }
        });
        customerBox.setButtonCell(customerBox.getCellFactory().call(null));

        //Item
        ItemController itemController = new ItemController();
        ObservableList<Item> items = FXCollections.observableArrayList(itemController.getAllItems());
        ListView<Item> itemList = new ListView<>(items);
        itemList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            itemList.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getName() + " - $" + item.getPrice());
                }
            });

        //Total
        TextField totalField = new TextField();
        totalField.setEditable(false); // Optional: make total read-only
        itemList.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Item>) change -> {
            totalCost = itemList.getSelectionModel().getSelectedItems()
                .stream()
                .mapToDouble(Item::getPrice)
                .sum();
            totalField.setText(String.format("%.2f", totalCost));
        });
        
        //Delivery guy
        DeliveryManController dManController = new DeliveryManController();
        ObservableList<DeliveryMan> dMen = FXCollections.observableArrayList(dManController.getAllDeliveryMan());
        ComboBox<DeliveryMan> deliverymanBox = new ComboBox<>(dMen);
        deliverymanBox.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void updateItem(DeliveryMan deliveryMan, boolean empty) {
                    super.updateItem(deliveryMan, empty);
                    setText(empty || deliveryMan == null ? "" : deliveryMan.getName());
                }
            });
        deliverymanBox.setButtonCell(deliverymanBox.getCellFactory().call(null));

        //Buttons
        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");

        HBox add = new HBox(10, datePicker, customerBox, itemList, totalField, deliverymanBox, addButton);
        HBox crud = new HBox(10, updateButton, deleteButton);
        HBox search = new HBox(10, searchField);
        VBox layout = new VBox(10,search, tableView, add, crud);
        layout.setStyle("-fx-padding: 10");

        // Load all customers on startup
        loadOrders();
        
        
            
        // Add
        addButton.setOnAction(e -> {
            try{
                Order order = new Order();
                order.setDateOfCreation(datePicker.getValue());
                order.setCustomer(customerBox.getValue());
                List<Item> selectedItems = new ArrayList<>(itemList.getSelectionModel().getSelectedItems());
                // order.setItems(selectedItems);
                order.setTotal(totalCost);
                order.setDeliveryMan(deliverymanBox.getValue());
                orderController.addOrder(order);
                orderController.addItemsToOrder(order, selectedItems);
                
                order.setDateOfCreation(null);
                order.setCustomer(null);
                order.setItems(null);
                order.setTotal(null);
                order.setDeliveryMan(null);
                loadOrders();
            } catch (Exception exception){
                showAlert("Error while adding customer", exception.getMessage());
            }
            
        });

        // Update
        updateButton.setOnAction(e -> {
            Order selected = tableView.getSelectionModel().getSelectedItem();
            try{
                loadOrders();
            } catch (Exception exception){
                showAlert("Error while updating customer", exception.getMessage());
            }
            
        });

        // Delete
        deleteButton.setOnAction(e -> {
            Order selected = tableView.getSelectionModel().getSelectedItem();
            try{
                orderController.deleteOrder(selected.getId());
                System.out.println("Deleting order");
                loadOrders();
            } catch (Exception exception){
                showAlert("Error while deleting customer", exception.getMessage());
            }
        });

        //Enter field on selection
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                datePicker.setValue(newSel.getDateOfCreation());
                addButton.setDisable(true); 
            }
        });
        //Exit from order selection
        tableView.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                tableView.getSelectionModel().clearSelection();
                addButton.setDisable(false);
            }
        });
        Tab tab = new Tab("Customer", layout);
        return tab;
    }

    private static void loadOrders() {
        OrderController orderController = new OrderController();
        orderList.setAll(orderController.getAllOrders());
    }

    private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
    }
}
