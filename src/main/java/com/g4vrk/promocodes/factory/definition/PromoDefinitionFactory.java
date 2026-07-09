package com.g4vrk.promocodes.factory.definition;

import com.g4vrk.functionalActions.list.ExecutableActionList;
import com.g4vrk.functionalActions.parser.ActionParser;
import com.g4vrk.promocodes.command.data.CommandData;
import com.g4vrk.promocodes.factory.YamlContentLoader;
import com.g4vrk.promocodes.model.PromoDefinition;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class PromoDefinitionFactory implements YamlContentLoader<PromoDefinition> {

    private final ActionParser<Audience> actionParser;

    @Override
    public @NotNull PromoDefinition from(@NotNull ConfigurationNode node) throws SerializationException {

        final String id = String.valueOf(node.key());
        final long initialUsages = node.node("initial-usages").getLong();
        final ExecutableActionList<? super Audience> actions = actionParser.parseAll(node.node("actions").getList(String.class, new ObjectArrayList<>()));

        final boolean usingDedicatedCommand = node.node("dedicated-command", "enabled").getBoolean();
        final CommandData dedicatedCommandData = newCommandData(node.node("dedicated-command"));
        
        return new PromoDefinition(id, initialUsages, actions, usingDedicatedCommand, dedicatedCommandData);
    }
    
    public @NotNull CommandData newCommandData(
            final @NotNull ConfigurationNode node
    ) throws SerializationException {

        final String name = Objects.requireNonNull(node.node("label").getString(), "Command label cannot be null");
        final String description = node.node("description").getString("Not provided");
        final List<String> aliases = node.node("aliases").getList(String.class, new ObjectArrayList<>());
        final String permission = node.node("permission").getString();

        return new CommandData(name, description, aliases, permission);
    }

}
