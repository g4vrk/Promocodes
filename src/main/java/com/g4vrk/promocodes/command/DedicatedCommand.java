package com.g4vrk.promocodes.command;

import com.g4vrk.functionalCommand.AbstractCommand;
import com.g4vrk.promocodes.command.data.CommandData;
import com.g4vrk.promocodes.executor.PromoExecutor;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DedicatedCommand extends AbstractCommand {

    private final String linkedPromoId;
    private final PromoExecutor promoExecutor;

    public DedicatedCommand(
            @NotNull CommandData commandData,
            @NotNull String linkedPromoId,
            @NotNull PromoExecutor promoExecutor
    ) {
        super(commandData.getName());
        this.linkedPromoId = linkedPromoId;
        this.promoExecutor = promoExecutor;

        commandData.apply(this);

        executes(this::execute);
    }

    private int execute(
            final @NotNull CommandContext<CommandSender> context
    ) {
        final Audience executor = context.getSource();

        promoExecutor.execute(executor, linkedPromoId, true);

        return Command.SINGLE_SUCCESS;
    }
}
