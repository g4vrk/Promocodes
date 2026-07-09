package com.g4vrk.promocodes.placeholder;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public abstract class PrefixedPlaceholderFunction extends AbstractPlaceholderFunction {

    public void addPrefixedReplacement(@NotNull String key, @NotNull String value) {
        addReplacement(getPrefix().strip() + key, value);
    }

    public @NotNull Component apply(
            final @NotNull Component component
    ) {
        if (!component.contains(Component.text(getPrefix().strip()))) return component;

        return super.apply(component);
    }

    @Override
    public @NotNull String apply(
            final @NotNull String string
    ) {
        return string.contains(getPrefix().strip()) ? super.apply(string) : string;
    }

    protected abstract @NotNull String getPrefix();
}
