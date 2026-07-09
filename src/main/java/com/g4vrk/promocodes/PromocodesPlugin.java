package com.g4vrk.promocodes;

import com.g4vrk.functionalActions.Action;
import com.g4vrk.functionalActions.defaults.impl.AudienceActionRegistry;
import com.g4vrk.functionalActions.list.ExecutableActionList;
import com.g4vrk.functionalActions.parser.ActionParser;
import com.g4vrk.functionalActions.parser.impl.SimpleActionParser;
import com.g4vrk.functionalActions.registry.ActionRegistries;
import com.g4vrk.functionalActions.registry.ActionRegistry;
import com.g4vrk.functionalActions.registry.impl.SimpleActionRegistry;
import com.g4vrk.functionalConfiguration.Config;
import com.g4vrk.functionalConfiguration.loader.YamlConfigLoader;
import com.g4vrk.promocodes.command.DedicatedCommand;
import com.g4vrk.promocodes.command.PromoCommand;
import com.g4vrk.promocodes.database.DatabaseConstants;
import com.g4vrk.promocodes.database.holder.CachedConnectionHolder;
import com.g4vrk.promocodes.database.loader.ConnectionHolderLoader;
import com.g4vrk.promocodes.database.repository.impl.PromoRepository;
import com.g4vrk.promocodes.executor.PromoExecutor;
import com.g4vrk.promocodes.factory.PromoFactory;
import com.g4vrk.promocodes.factory.definition.PromoDefinitionFactory;
import com.g4vrk.promocodes.model.PromoDefinition;
import com.g4vrk.promocodes.placeholder.factory.PromoPlaceholderFunctionFactory;
import com.g4vrk.promocodes.task.runner.TaskRunner;
import com.g4vrk.promocodes.task.runner.paper.factory.PaperTaskRunnerFactory;
import com.g4vrk.promocodes.usage.PromoUsageManager;
import com.g4vrk.promocodes.util.PluginUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.SneakyThrows;
import net.kyori.adventure.audience.Audience;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class PromocodesPlugin extends JavaPlugin {

    public static final String NAME = "Promocodes";

    private static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    private ActionParser<Audience> actionParser;

    private CachedConnectionHolder connectionHolder;

    public PromocodesPlugin() {
    }

    @Override
    public void onEnable() {

        final File pluginDir = getDataFolder();

        final ActionRegistry<Audience> actionRegistry = new SimpleActionRegistry<>();

        Collection<Action<? super Audience>> defaultActions = new AudienceActionRegistry().getActions();

        actionRegistry.registerAll(defaultActions);

        this.actionParser = new SimpleActionParser<>(actionRegistry);

        //noinspection ResultOfMethodCallIgnored
        pluginDir.mkdirs();

        final YamlConfigLoader yamlConfigLoader = new YamlConfigLoader();

        final File mainConfigFile = new File(pluginDir, "main-config.yml");

        if (!mainConfigFile.exists()) {
            super.saveResource("main-config.yml", false);
        }

        final Config mainConfig;
        try {
            mainConfig = yamlConfigLoader.from(mainConfigFile.toPath());
        } catch (final IOException ex) {
            throw new RuntimeException("An internal IO error occurred while creating yaml config", ex);
        }

        final ConnectionHolderLoader connectionHolderLoader = new ConnectionHolderLoader();

        this.connectionHolder = connectionHolderLoader.createService(
                new File(pluginDir, "promos-data.db")
        );

        final PromoRepository repository = new PromoRepository(
                connectionHolder,
                DatabaseConstants.MAIN_POOL_NAME
        );

        repository.initialize();

        final PromoDefinitionFactory definitionFactory = new PromoDefinitionFactory(actionParser);
        final PromoFactory promoFactory = new PromoFactory(definitionFactory);

        final Collection<PromoDefinition> promoList = new ObjectArrayList<>();
        final Collection<PromoDefinition> dedicatedPromoList = new ObjectArrayList<>();

        try {

            promoList.addAll(promoFactory.from(mainConfig.node("promos")));

            for (final PromoDefinition promo : promoList) {

                if (promo.isUsingDedicatedCommand()) {
                    dedicatedPromoList.add(promo);
                }

                repository.savePromoIfAbsent(promo.getId(), (int) promo.getInitialUsages());
            }

        } catch (final SerializationException | SQLException ex) {
            throw new RuntimeException("An error occurred while trying to save promos", ex);
        }

        final Map<String, PromoDefinition> promos = new Object2ObjectOpenHashMap<>();

        for (final PromoDefinition definition : promoList) {
            if (promos.put(definition.getId(), definition) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }

        final PromoUsageManager promoUsageManager;
        try {
            promoUsageManager = new PromoUsageManager(repository.getPromosRemainingUsages(), repository.getPromoUsages());
        } catch (final SQLException ex) {
            throw new RuntimeException("An error occurred while trying to access database", ex);
        }

        final TaskRunner taskRunner = new PaperTaskRunnerFactory(this).create();

        final ConfigurationNode actionsNode = mainConfig.node("actions");
        final PromoExecutor promoExecutor = new PromoExecutor(
                promoUsageManager,
                promos,
                taskRunner,
                repository,
                new PromoPlaceholderFunctionFactory(
                        promoUsageManager,
                        PluginUtil.containsPlugin("PlaceholderAPI")
                ),
                loadActions(actionsNode.node("unknown-promo-actions")),
                loadActions(actionsNode.node("promo-uses-dedicated-command")),
                loadActions(actionsNode.node("promo-already-activated")),
                loadActions(actionsNode.node("promo-limit-reached"))
        );

        for (final PromoDefinition promoDefinition : dedicatedPromoList) {
            final DedicatedCommand dedicatedCommand = new DedicatedCommand(
                    promoDefinition.getDedicatedCommandData(),
                    promoDefinition.getId(),
                    promoExecutor
            );

            dedicatedCommand.register(this, false);
        }

        final ConfigurationNode promoCommandsNode = mainConfig.node("promo-command");

        if (promoCommandsNode.node("enabled").getBoolean()) {
            try {
                final PromoCommand promoCommand = new PromoCommand(definitionFactory.newCommandData(promoCommandsNode), promoExecutor);

                promoCommand.register(this, false);
            } catch (final SerializationException ex) {
                throw new RuntimeException("A serialization error occurred while parsing actions", ex);
            }
        }

        LOGGER.info("Promocodes plugin successfully enabled.");
        LOGGER.info("Thanks for using!! ~g4vrk");
    }

    private @NotNull ExecutableActionList<? super Audience> loadActions(
            final @NotNull ConfigurationNode node
    ) {
        try {
            final List<String> rawList = node.getList(String.class, new ObjectArrayList<>());

            return actionParser.parseAll(rawList);
        } catch (final SerializationException ex) {
            throw new RuntimeException("A serialization error occurred while parsing actions", ex);
        }
    }

    @SneakyThrows
    @Override
    public void onDisable() {
        if (this.connectionHolder != null) {
            connectionHolder.disconnectAll();
        }

        LOGGER.info("Promocodes plugin successfully disabled.");
    }

}
