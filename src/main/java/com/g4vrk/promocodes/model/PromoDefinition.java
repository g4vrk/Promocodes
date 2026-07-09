package com.g4vrk.promocodes.model;

import com.g4vrk.functionalActions.list.ExecutableActionList;
import com.g4vrk.promocodes.command.data.CommandData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ClassCanBeRecord")
@AllArgsConstructor @Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PromoDefinition {

    @NotNull String id;
    long initialUsages;
    @NotNull ExecutableActionList<? super Audience> actions;

    boolean usingDedicatedCommand;
    @NotNull CommandData dedicatedCommandData;

}
