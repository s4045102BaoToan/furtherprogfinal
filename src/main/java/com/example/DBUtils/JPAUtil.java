package com.example.DBUtils;

import jakarta.persistence.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class JPAUtil {
    private static final EntityManagerFactory emf = buildEntityManagerFactory();

    private static EntityManagerFactory buildEntityManagerFactory() {
        // Dotenv dotenv = Dotenv.load();

        String dbUrl = "jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:5432/postgres";
        String dbUser = "postgres.pbikeijwfnzcnzgzdtiy";
        String dbPassword = "nW5btVQ5GMxG0GR3";
        //FINISH SETTING UP DATEBASE CLASSES
        if (dbUrl == null || dbUser == null || dbPassword == null) {
            throw new RuntimeException("Missing environment variables for DB connection!");
        }

        Map<String, String> configOverrides = new HashMap<>();
        configOverrides.put("jakarta.persistence.jdbc.url", dbUrl);
        configOverrides.put("jakarta.persistence.jdbc.user", dbUser);
        configOverrides.put("jakarta.persistence.jdbc.password", dbPassword);
        configOverrides.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");

        // Optional Hibernate configs (customize based on your needs)
        configOverrides.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configOverrides.put("hibernate.show_sql", "true");

        return Persistence.createEntityManagerFactory("my-persistence-unit", configOverrides);
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void shutdown() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
