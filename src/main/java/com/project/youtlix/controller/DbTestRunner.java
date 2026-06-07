package com.project.youtlix.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DbTestRunner implements CommandLineRunner {

    private final DataSource dataSource;

    public DbTestRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("DB OK: " + conn.getMetaData().getDatabaseProductName());
        }
    }
}
