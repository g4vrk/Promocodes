package com.g4vrk.promocodes.placeholder;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

public interface PlaceholderFunction extends UnaryOperator<String> {

    @Override
    @NotNull String apply(@NotNull String string);

    @NotNull Component apply(@NotNull Component component);

    default @NotNull PlaceholderFunction with(@NotNull PlaceholderFunction other) {
        return new PlaceholderFunction() {
            @Override
            public @NotNull String apply(@NotNull String string) {
                return other.apply(PlaceholderFunction.this.apply(string));
            }

            @Override
            public @NotNull Component apply(@NotNull Component component) {
                return other.apply(PlaceholderFunction.this.apply(component));
            }
        };
    }

    static @NotNull PlaceholderFunction compose(@NotNull PlaceholderFunction @NotNull ... functions) {
        return new PlaceholderFunction() {
            @Override
            public @NotNull String apply(@NotNull String string) {
                for (PlaceholderFunction function : functions) {
                    string = function.apply(string);
                }
                return string;
            }

            @Override
            public @NotNull Component apply(@NotNull Component component) {
                for (PlaceholderFunction function : functions) {
                    component = function.apply(component);
                }
                return component;
            }
        };
    }

}
