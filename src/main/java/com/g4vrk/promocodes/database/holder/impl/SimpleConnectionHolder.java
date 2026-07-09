package com.g4vrk.promocodes.database.holder.impl;

import com.g4vrk.promocodes.database.connector.DatabaseConnector;
import com.g4vrk.promocodes.database.holder.CachedConnectionHolder;
import com.g4vrk.promocodes.database.connector.factory.DatabaseConnectorFactory;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleConnectionHolder implements CachedConnectionHolder {

    private final Map<String, DatabaseConnector> connectors = new ConcurrentHashMap<>();
    private final Set<Connection> activeConnections = ConcurrentHashMap.newKeySet();

    private final DatabaseConnectorFactory factory;

    public SimpleConnectionHolder(
            @NotNull DatabaseConnectorFactory factory
    ) {
        this.factory = factory;
    }

    @Override
    public @NotNull Connection connect(@NotNull String poolName) {
        try {
            final Connection connection = connectors.computeIfAbsent(poolName, factory::create).connect();

            activeConnections.add(connection);

            return connection;
        } catch (final SQLException ex) {
            throw new RuntimeException("An error occurred while connecting to database", ex);
        }
    }

    @Override
    public void disconnectAll() {
        activeConnections.forEach(connection -> {
            try {
                disconnect(connection);
            } catch (final SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        activeConnections.clear();
    }

    private void disconnect(
            final @NotNull Connection connection
    ) throws SQLException {
        connection.close();
    }
}
