package com.g4vrk.promocodes.database.connector.factory.impl;

import com.g4vrk.promocodes.database.connector.DatabaseConnector;
import com.g4vrk.promocodes.database.connector.factory.DatabaseConnectorFactory;
import com.g4vrk.promocodes.database.connector.impl.SQLiteConnector;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class SQLiteConnectorFactory implements DatabaseConnectorFactory {

    private final File baseFile;

    public SQLiteConnectorFactory(
            @NotNull File baseFile
    ) {
        this.baseFile = baseFile;
    }

    @Override
    public @NotNull DatabaseConnector create(@NotNull String poolName) {
        return new SQLiteConnector(poolName, baseFile);
    }

}

