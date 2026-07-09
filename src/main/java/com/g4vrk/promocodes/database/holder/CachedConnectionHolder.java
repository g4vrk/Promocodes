package com.g4vrk.promocodes.database.holder;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public interface CachedConnectionHolder {

    @NotNull Connection connect(@NotNull String poolName) throws SQLException;

    void disconnectAll() throws SQLException;

}
