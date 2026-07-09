package com.g4vrk.promocodes.database.connector.factory;

import com.g4vrk.promocodes.database.connector.DatabaseConnector;
import org.jetbrains.annotations.NotNull;

public interface DatabaseConnectorFactory {

    @NotNull DatabaseConnector create(@NotNull String poolName);

}
