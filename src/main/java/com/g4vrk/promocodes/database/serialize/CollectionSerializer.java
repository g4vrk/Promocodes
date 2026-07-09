package com.g4vrk.promocodes.database.serialize;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@UtilityClass
public class CollectionSerializer {

    public static @NotNull String serialize(
            final @NotNull Collection<String> raw
    ) {
        return String.join("|", raw);
    }

    public @NotNull Collection<String> deserialize(
            final @NotNull String raw
    ) {
        if (raw.trim().isEmpty()) {
            return new ObjectOpenHashSet<>();
        }

        return new ObjectOpenHashSet<>(raw.split("\\|"));
    }

}
