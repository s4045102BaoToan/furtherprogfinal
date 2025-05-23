package com.example;

import com.example.DBUtils.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    private static volatile boolean ready = false;
    public static void main( String[] args ) {
        Thread backendThread = new Thread(() -> {
            // final EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
            // EntityManager em = emf.createEntityManager();
            // if (em != null && emf != null) {
            //     System.out.println("Successfully connected to database");
            // }
            System.out.println("Backend is running...");
            ready = true;
        });
        backendThread.setDaemon(true);
        backendThread.start();
        // Launch JavaFX application on the JavaFX Application Thread
        App.main(args); // calls Application.launch() Uncomment to run javafx
    }

    public static boolean backendReady() {
        return ready;
    }
    
}

