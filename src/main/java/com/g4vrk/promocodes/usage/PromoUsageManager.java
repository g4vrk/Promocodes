package com.g4vrk.promocodes.usage;

import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PromoUsageManager {

    private final Map<String, Long> promosRemainingUsages = new Object2LongOpenHashMap<>();
    private final Map<UUID, Set<String>> promosUsage = new Object2ObjectOpenHashMap<>();

    public PromoUsageManager(
            @NotNull Map<String, Long> promosRemainingUsages,
            @NotNull Map<UUID, Set<String>> promosUsage
    ) {
        this.promosRemainingUsages.putAll(promosRemainingUsages);
        this.promosUsage.putAll(promosUsage);
    }

    public long getRemainingUsages(
            final @NotNull String promoId
    ) {
        validate(promoId);

        return promosRemainingUsages.get(promoId);
    }

    public void decrementRemainingUsages(
            final @NotNull String promoId
    ) {
        validate(promoId);

        if (promosRemainingUsages.get(promoId) <= 0) {
            return;
        }

        promosRemainingUsages.put(promoId, promosRemainingUsages.get(promoId) - 1);
    }

    public boolean mayUse(
            final @NotNull String promoId
    ) {
        validate(promoId);

        return promosRemainingUsages.get(promoId) > 0L;
    }

    public void markAsUsed(
            final @NotNull UUID uuid,
            final @NotNull String promoId
    ) {
        promosUsage.computeIfAbsent(uuid, playerId -> createFastSet())
                .add(promoId);
    }

    public boolean usedAlready(
            final @NotNull UUID uuid,
            final @NotNull String promoId
    ) {
        return promosUsage.computeIfAbsent(uuid, playerId -> createFastSet())
                .contains(promoId);
    }

    private void validate(@NotNull String promoId) {
        if (!promosRemainingUsages.containsKey(promoId)) {
            throw new RuntimeException("Cannot find promo with id " + promoId);
        }
    }

    private @NotNull <T> Set<T> createFastSet() {
        return new ObjectOpenHashSet<>();
    }
}
