package com.g4vrk.promocodes.database.connector;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnector {

    @NotNull Connection connect() throws SQLException;

}
