package com.g4vrk.promocodes.command.data;

import lombok.Value;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value
public class CommandData {
    @NotNull String name;
    @NotNull String description;
    @NotNull List<String> aliases;
    @Nullable String permission;

    public void apply(@NotNull Command command) {
        command.setName(name);
        command.setDescription(description);
        command.setAliases(aliases);
        command.setPermission(permission);
    }
}
