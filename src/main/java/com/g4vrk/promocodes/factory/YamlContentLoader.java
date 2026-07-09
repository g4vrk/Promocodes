package com.g4vrk.promocodes.factory;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

@FunctionalInterface
public interface YamlContentLoader<T> {

    @NotNull T from(@NotNull ConfigurationNode node) throws SerializationException;

}
