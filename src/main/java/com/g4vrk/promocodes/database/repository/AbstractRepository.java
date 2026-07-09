package com.g4vrk.promocodes.database.repository;

import com.g4vrk.promocodes.database.holder.CachedConnectionHolder;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
public abstract class AbstractRepository {

    private final CachedConnectionHolder connectionHolder;
    private final String poolName;

    protected @NotNull Connection connect() throws SQLException {
        return connectionHolder.connect(poolName);
    }
}
