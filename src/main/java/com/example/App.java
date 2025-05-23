package com.example;

import com.example.DBUtils.JPAUtil;
import com.example.model.DeliveryMan;
import com.example.view.CustomerView;
import com.example.view.DeliveryManView;
import com.example.view.ItemView;
import com.example.view.OrderView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Hello world!
 *
 */
public class App extends Application{
    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();

        Tab customerTab = new CustomerView().getView();

        Tab orderTab = new OrderView().getView();

        Tab itemTab = new ItemView().getView();

        Tab deliveryManTab = new DeliveryManView().getView();

        tabPane.getTabs().addAll(customerTab, itemTab, deliveryManTab, orderTab);

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Management System");

        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();     // Gracefully exit JavaFX
            System.exit(0);      // Force JVM to shut down to clean up threads
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        JPAUtil.shutdown(); // Close Hibernate resources
    }
}
