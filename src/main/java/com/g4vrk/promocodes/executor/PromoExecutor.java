package com.g4vrk.promocodes.executor;

import com.g4vrk.functionalActions.list.ExecutableActionList;
import com.g4vrk.promocodes.database.repository.impl.PromoRepository;
import com.g4vrk.promocodes.model.PromoDefinition;
import com.g4vrk.promocodes.placeholder.factory.PromoPlaceholderFunctionFactory;
import com.g4vrk.promocodes.task.runner.TaskRunner;
import com.g4vrk.promocodes.task.schedule.TickSchedule;
import com.g4vrk.promocodes.usage.PromoUsageManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PromoExecutor {

    private final TaskRunner taskRunner;

    private final PromoRepository promoRepository;
    private final PromoUsageManager usageManager;

    private final PromoPlaceholderFunctionFactory functionFactory;

    private final Map<String, PromoDefinition> promos = new Object2ObjectOpenHashMap<>();

    private final ExecutableActionList<? super Audience> unknownPromoActions;
    private final ExecutableActionList<? super Audience> promoUsesDedicatedCommandActions;
    private final ExecutableActionList<? super Audience> promoAlreadyActivatedActions;
    private final ExecutableActionList<? super Audience> promoLimitReachedActions;

    public PromoExecutor(
            @NotNull PromoUsageManager usageManager,
            @NotNull Map<String, PromoDefinition> promos,
            @NotNull TaskRunner taskRunner,
            @NotNull PromoRepository promoRepository,
            @NotNull PromoPlaceholderFunctionFactory functionFactory,
            @NotNull ExecutableActionList<? super Audience> unknownPromoActions,
            @NotNull ExecutableActionList<? super Audience> promoUsesDedicatedCommandActions,
            @NotNull ExecutableActionList<? super Audience> promoAlreadyActivatedActions,
            @NotNull ExecutableActionList<? super Audience> promoLimitReachedActions
    ) {
        this.taskRunner = taskRunner;
        this.usageManager = usageManager;
        this.promoRepository = promoRepository;
        this.functionFactory = functionFactory;
        this.unknownPromoActions = unknownPromoActions;
        this.promoUsesDedicatedCommandActions = promoUsesDedicatedCommandActions;
        this.promoAlreadyActivatedActions = promoAlreadyActivatedActions;
        this.promoLimitReachedActions = promoLimitReachedActions;

        this.promos.putAll(promos);
    }

    public void execute(
            final @NotNull Audience executor,
            final @NotNull String promoId
    ) {
        final PromoDefinition promo = promos.get(promoId);

        if (promo == null) {
            unknownPromoActions.run(executor);
            return;
        }

        if (promo.isUsingDedicatedCommand()) {
            promoUsesDedicatedCommandActions.run(executor, functionFactory.create(executor, promo));
            return;
        }

        if (!usageManager.mayUse(promoId)) {
            promoLimitReachedActions.run(executor, functionFactory.create(executor, promo));
            return;
        }


        final UUID uuid = getUniqueId(executor);
        if (usageManager.usedAlready(uuid, promoId)) {
            promoAlreadyActivatedActions.run(executor, functionFactory.create(executor, promo));
            return;
        }

        usageManager.decrementRemainingUsages(promoId);
        usageManager.markAsUsed(uuid, promoId);

        final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                promoRepository.saveUserIsAbsent(uuid);
                promoRepository.decreaseRemainingUsages(promoId);
                promoRepository.markAsUsed(uuid, promoId);
            } catch (final SQLException ex) {
                throw new RuntimeException("An SQL error occurred while accessing to database", ex);
            }
        });

        future.thenRun(() -> taskRunner.runTask(() -> promo.getActions().run(executor, functionFactory.create(executor, promo)), TickSchedule.instant()));
    }

    private @NotNull UUID getUniqueId(
            final @NotNull Audience target
    ) {
        // i'm using "0000-0000-0000-0000" as console uuid...
        return target instanceof Player player ? player.getUniqueId() : UUID.fromString("0000-0000-0000-0000");
    }
}
