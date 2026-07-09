package com.g4vrk.promocodes.database.connector.impl;

import com.g4vrk.promocodes.database.connector.DatabaseConnector;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLiteConnector implements DatabaseConnector {

    private final HikariDataSource source;

    public SQLiteConnector(
            @NotNull String poolName,
            @NotNull File baseFile
    ) {
        final String url = "jdbc:sqlite:" + baseFile.getAbsolutePath();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setPoolName(poolName);
        config.setMaximumPoolSize(5);
        config.setConnectionTestQuery("SELECT 1");

        this.source = new HikariDataSource(config);
    }

    @Override
    public @NotNull Connection connect() throws SQLException {
        return source.getConnection();
    }

}
