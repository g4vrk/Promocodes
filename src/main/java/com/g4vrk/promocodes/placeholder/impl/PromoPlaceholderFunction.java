package com.g4vrk.promocodes.placeholder.impl;

import com.g4vrk.promocodes.model.PromoDefinition;
import com.g4vrk.promocodes.placeholder.PrefixedPlaceholderFunction;
import com.g4vrk.promocodes.usage.PromoUsageManager;
import org.jetbrains.annotations.NotNull;

public class PromoPlaceholderFunction extends PrefixedPlaceholderFunction {

    public PromoPlaceholderFunction(
            @NotNull PromoDefinition promoDefinition,
            @NotNull PromoUsageManager promoUsageManager
    ) {
        final String id = promoDefinition.getId();

        addPrefixedReplacement("id", id);
        addPrefixedReplacement("max-usages", String.valueOf(promoDefinition.getInitialUsages()));
        addPrefixedReplacement("remaining-usages", String.valueOf(promoUsageManager.getRemainingUsages(id)));
    }

    @Override
    protected @NotNull String getPrefix() {
        return "promo-code.";
    }

}
