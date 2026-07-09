package com.g4vrk.promocodes.command.argument;

import com.g4vrk.functionalCommand.argument.types.StringArgument;
import com.g4vrk.promocodes.executor.PromoExecutor;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PromoArgument extends StringArgument {

    private final PromoExecutor promoExecutor;

    public PromoArgument(
            @NotNull String name,
            @NotNull PromoExecutor promoExecutor
    ) {
        super(name, StringArgumentType.word());
        this.promoExecutor = promoExecutor;

        executes(this::execute);
    }

    private int execute(
            final @NotNull CommandContext<CommandSender> context
    ) throws CommandSyntaxException {

        final Optional<String> promoId = parse(context);

        promoId.ifPresent(id -> promoExecutor.execute(context.getSource(), id));

        return Command.SINGLE_SUCCESS;
    }

}
