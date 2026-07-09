package com.g4vrk.promocodes.database.repository.impl;

import com.g4vrk.promocodes.database.holder.CachedConnectionHolder;
import com.g4vrk.promocodes.database.repository.AbstractRepository;
import com.g4vrk.promocodes.database.serialize.CollectionSerializer;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PromoRepository extends AbstractRepository {

    public PromoRepository(
            @NotNull CachedConnectionHolder connectionHolder,
            @NotNull String poolName
    ) {
        super(connectionHolder, poolName);
    }

    public @NotNull CompletableFuture<Void> initAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                createTables();
            } catch (final SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void createTables(
    ) throws SQLException {

        final String remainingUsagesSql = """
        CREATE TABLE IF NOT EXISTS promosRemainingUsages (
            id TEXT PRIMARY KEY NOT NULL,
            remainingUsages INTEGER NOT NULL
        )
        """;

        final String promosUsageSql = """
        CREATE TABLE IF NOT EXISTS promosUsage (
            uuid VARCHAR(36) PRIMARY KEY NOT NULL,
            usedPromos TEXT NOT NULL
        )
        """;

        try (
                final Connection connection = connect()
        ) {
            try (PreparedStatement statement = connection.prepareStatement(remainingUsagesSql)) {
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement(promosUsageSql)) {
                statement.executeUpdate();
            }
        }
    }

    public void savePromoIfAbsent(
            final @NotNull String promoId,
            final int initialUsages
    ) throws SQLException {

        final String sql = """
        INSERT OR IGNORE INTO promosRemainingUsages (id, remainingUsages)
        VALUES (?, ?)
        """;

        try (
                final Connection connection = connect();
                final PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, promoId);
            statement.setInt(2, initialUsages);

            statement.executeUpdate();
        }
    }

    public boolean usageLimitReached(
            final @NotNull String promoId
    ) throws SQLException {

        return getRemainingUsages(promoId) <= 0;

    }

    public int getRemainingUsages(
            final @NotNull String promoId
    ) throws SQLException {

        final String sql = """
        SELECT remainingUsages
        FROM promosRemainingUsages
        WHERE id = ?
        """;

        try (
                final Connection connection = connect();
                final PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, promoId);

            try (final ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return result.getInt("remainingUsages");

                }
            }
        }

        throw new SQLException(
                "Promo '" + promoId + "' does not exist."
        );
    }

    public void decreaseRemainingUsages(
            final @NotNull String promoId
    ) throws SQLException {

        final String sql = """
        UPDATE promosRemainingUsages
        SET remainingUsages = remainingUsages - 1
        WHERE id = ?
        AND remainingUsages > 0
        """;

        try (
                final Connection connection = connect();
                final PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, promoId);

            int updated = statement.executeUpdate();

            if (updated == 0) {
                throw new SQLException("Promo '" + promoId + "' not found.");
            }
        }
    }

    public void saveUserIsAbsent(
            final @NotNull UUID uuid
    ) throws SQLException {

        final String sql = """
        INSERT OR IGNORE INTO promosUsage (uuid, usedPromos)
        VALUES (?, ?)
        """;

        try (
                final Connection connection = connect();
                final PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, uuid.toString());
            statement.setString(2, "");

            statement.executeUpdate();
        }
    }

    public @NotNull Collection<String> getUsedPromos(
            final @NotNull UUID uuid
    ) throws SQLException {

        final String sql = """
        SELECT usedPromos
        FROM promosUsage
        WHERE uuid = ?
        """;

        try (
                final Connection connection = connect();
                final PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, uuid.toString());

            try (final ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    final String rawSet = result.getString("usedPromos");

                    return CollectionSerializer.deserialize(rawSet);

                }
            }
        }

        throw new SQLException(
                "User '" + uuid + "' does not exist."
        );
    }

    public boolean hasUsedPromo(
            final @NotNull UUID uuid,
            final @NotNull String promoId
    ) throws SQLException {
        return getUsedPromos(uuid).contains(promoId);
    }

    public void markAsUsed(
            final @NotNull UUID uuid,
            final @NotNull String promoId
    ) throws SQLException {

        final String sql = """
        UPDATE promosUsage
        SET usedPromos = ?
        WHERE uuid = ?
        """;

        try (
                final Connection connection = connect();
                final PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            final Collection<String> usedPromos = getUsedPromos(uuid);

            usedPromos.add(promoId);

            statement.setString(1, CollectionSerializer.serialize(usedPromos));
            statement.setString(2, uuid.toString());

            statement.executeUpdate();
        }

    }

    public @NotNull Map<String, Long> getPromosRemainingUsages(
    ) throws SQLException {

        final Map<String, Long> promosRemainingUsages = new Object2LongOpenHashMap<>();

        String sql = """
        SELECT id, remainingUsages
        FROM promosRemainingUsages
        """;

        try (
                final Connection connection = connect();
                final PreparedStatement statement = connection.prepareStatement(sql);
                final ResultSet result = statement.executeQuery()
        ) {

            while (result.next()) {

                final String promoId = result.getString("id");
                long remainingUsages = result.getInt("remainingUsages");

                promosRemainingUsages.put(promoId, remainingUsages);
            }
        }

        return promosRemainingUsages;
    }

    public @NotNull Map<UUID, Set<String>> getPromoUsages(
    ) throws SQLException {

        final Map<UUID, Set<String>> promosUsages = new Object2ObjectOpenHashMap<>();

        final String sql = """
        SELECT uuid, usedPromos
        FROM promosUsage
        """;

        try (
                final Connection connection = connect();
                final PreparedStatement statement = connection.prepareStatement(sql);
                final ResultSet result = statement.executeQuery()
        ) {

            while (result.next()) {

                final UUID uuid = UUID.fromString(result.getString("uuid"));
                final Set<String> usedPromos = new ObjectOpenHashSet<>(CollectionSerializer.deserialize(result.getString("usedPromos")));

                promosUsages.put(uuid, usedPromos);
            }
        }

        return promosUsages;
    }

}
