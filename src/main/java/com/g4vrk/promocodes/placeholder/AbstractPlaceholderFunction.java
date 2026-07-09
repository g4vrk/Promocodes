package com.g4vrk.promocodes.placeholder;

import com.g4vrk.fastTextFormatter.placeholder.PlaceholderMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AbstractPlaceholderFunction implements PlaceholderFunction {

    private final Map<String, String> replacements;

    private PlaceholderMap placeholderMap;
    private boolean dirty = true;

    public AbstractPlaceholderFunction(
            final @NotNull Map<String, String> replacements
    ) {
        this.replacements = new Object2ObjectOpenHashMap<>(replacements);
    }

    public AbstractPlaceholderFunction() {
        this.replacements = new Object2ObjectOpenHashMap<>();
    }

    public void addReplacement(
            final @NotNull String key,
            final @NotNull String value
    ) {
        replacements.put(key, value);
        dirty = true;
    }

    public void addReplacement(
            final @NotNull String key,
            final Object value
    ) {
        addReplacement(key, String.valueOf(value));
    }

    public void removeReplacement(
            final @NotNull String key
    ) {
        replacements.remove(key);
        dirty = true;
    }

    public void clearReplacements() {
        replacements.clear();
        dirty = true;
    }

    public boolean hasReplacement(
            final @NotNull String key
    ) {
        return replacements.containsKey(key);
    }

    private @NotNull PlaceholderMap getPlaceholderMap() {
        if (!dirty && placeholderMap != null) {
            return placeholderMap;
        }

        placeholderMap = new PlaceholderMap(replacements);
        dirty = false;

        return placeholderMap;
    }



    @Override
    public @NotNull String apply(
            final @NotNull String string
    ) {
        if (replacements.isEmpty()) {
            return string;
        }

        return getPlaceholderMap().apply(string, null);
    }

    @Override
    public @NotNull Component apply(
            final @NotNull Component component
    ) {
        if (replacements.isEmpty()) {
            return component;
        }

        if (component instanceof TextComponent textComponent
                && textComponent.children().isEmpty()) {

            final String content = textComponent.content();
            final String replaced = apply(content);

            if (content.equals(replaced)) {
                return component;
            }

            return textComponent.content(replaced);
        }

        return component.replaceText(config ->
                config.match("\\{([^{}]+)}")
                        .replacement((match, builder) -> {
                            final String placeholder = match.group(1);

                            final String replacement = replacements.get(placeholder);

                            if (replacement == null) {
                                return builder.content(match.group()).build();
                            }

                            return builder.content(replacement).build();
                        })
        );
    }

    public @NotNull Map<String, String> getReplacements() {
        return new Object2ObjectOpenHashMap<>(replacements);
    }
}