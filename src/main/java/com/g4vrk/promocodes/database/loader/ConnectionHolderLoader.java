package com.g4vrk.promocodes.database.loader;

import com.g4vrk.promocodes.database.connector.factory.DatabaseConnectorFactory;
import com.g4vrk.promocodes.database.connector.factory.impl.SQLiteConnectorFactory;
import com.g4vrk.promocodes.database.holder.CachedConnectionHolder;
import com.g4vrk.promocodes.database.holder.impl.SimpleConnectionHolder;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ConnectionHolderLoader {

    public @NotNull CachedConnectionHolder createService(
            final @NotNull File baseFile
    ) {
        final DatabaseConnectorFactory connectorFactory = new SQLiteConnectorFactory(baseFile);

        return new SimpleConnectionHolder(connectorFactory);
    }

}
