package com.g4vrk.promocodes.factory;

import com.g4vrk.promocodes.factory.definition.PromoDefinitionFactory;
import com.g4vrk.promocodes.model.PromoDefinition;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;

@RequiredArgsConstructor
public class PromoFactory implements YamlContentLoader<Collection<PromoDefinition>> {

    private final PromoDefinitionFactory definitionFactory;

    @Override
    public @NonNull Collection<PromoDefinition> from(@NotNull ConfigurationNode node) throws SerializationException {

        final Collection<PromoDefinition> result = new ObjectArrayList<>();

        for (final ConfigurationNode value : node.childrenMap().values()) result.add(definitionFactory.from(value));

        return result;
    }

}
