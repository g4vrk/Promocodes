package com.g4vrk.promocodes.command;

import com.g4vrk.functionalCommand.AbstractCommand;
import com.g4vrk.promocodes.command.argument.PromoArgument;
import com.g4vrk.promocodes.command.data.CommandData;
import com.g4vrk.promocodes.executor.PromoExecutor;
import org.jetbrains.annotations.NotNull;

public class PromoCommand extends AbstractCommand {

    private final PromoExecutor executor;

    public PromoCommand(
            @NotNull CommandData commandData,
            @NotNull PromoExecutor executor
    ) {
        super(commandData.getName());
        this.executor = executor;

        commandData.apply(this);

        this.setup();
    }

    private void setup() {

        final PromoArgument argument = new PromoArgument("promoId", executor);

        then(argument);

    }

}
