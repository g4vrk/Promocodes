package com.g4vrk.promocodes.placeholder;

import com.g4vrk.fastTextFormatter.placeholder.PlaceholderMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class PrefixedPlaceholderFunction implements PlaceholderFunction {

    private final Map<String, String> replacements = new Object2ObjectOpenHashMap<>();
    private final PlaceholderMap placeholderMap;

    public PrefixedPlaceholderFunction(@NotNull Map<String, String> replacements) {
        this.replacements.putAll(replacements);
        this.placeholderMap = new PlaceholderMap(replacements);
    }

    public PrefixedPlaceholderFunction() {
        this.placeholderMap = new PlaceholderMap();
    }

    public void addReplacement(@NotNull String key, @NotNull String value) {
        this.replacements.put(key, value);
    }

    public void addPrefixedReplacement(@NotNull String key, @NotNull String value) {
        addReplacement(getPrefix().strip() + key, value);
    }

    @Override
    public @NotNull Component apply(
            final @NotNull Component component
    ) {
        Component result = component;

        if (!component.contains(Component.text(getPrefix().strip()))) return result;

        for (final Map.Entry<String, String> entry : replacements.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();

            result = result.replaceText(builder ->
                    builder.matchLiteral("{" + key + "}")
                            .replacement(value)
            );
        }

        return result;
    }

    @Override
    public @NotNull String apply(
            final @NotNull String string
    ) {
        return string.contains(getPrefix().strip()) ? placeholderMap.apply(string, null) : string;
    }

    protected abstract @NotNull String getPrefix();
}
