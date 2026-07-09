package com.g4vrk.promocodes.placeholder.factory;

import com.g4vrk.promocodes.model.PromoDefinition;
import com.g4vrk.promocodes.placeholder.PlaceholderFunction;
import com.g4vrk.promocodes.placeholder.impl.PlaceholderAPIFunction;
import com.g4vrk.promocodes.placeholder.impl.PromoPlaceholderFunction;
import com.g4vrk.promocodes.usage.PromoUsageManager;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PromoPlaceholderFunctionFactory {

    private final PromoUsageManager usageManager;

    private final boolean hasPlaceholderAPI;

    public @NotNull PlaceholderFunction create(
            final @NotNull Audience audience,
            final @NotNull PromoDefinition definition
    ) {
        final PlaceholderFunction func = new PromoPlaceholderFunction(definition, usageManager);

        if (hasPlaceholderAPI && audience instanceof final Player player) {
            return func.with(new PlaceholderAPIFunction(player));
        }

        return func;
    }

}
