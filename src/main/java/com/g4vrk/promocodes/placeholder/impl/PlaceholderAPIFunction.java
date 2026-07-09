package com.g4vrk.promocodes.placeholder.impl;

import com.g4vrk.promocodes.placeholder.AbstractPlaceholderFunction;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIFunction extends AbstractPlaceholderFunction {

    private final OfflinePlayer player;

    public PlaceholderAPIFunction(
            final @NotNull OfflinePlayer player
    ) {
        this.player = player;
    }

    @Override
    public @NotNull String apply(
            final @NotNull String string
    ) {
        final String replaced = super.apply(string);

        return PlaceholderAPI.setPlaceholders(
                player,
                replaced
        );
    }
}