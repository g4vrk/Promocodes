package com.g4vrk.promocodes.task.runner.factory;

import com.g4vrk.promocodes.task.runner.TaskRunner;
import org.jetbrains.annotations.NotNull;

public interface TaskRunnerFactory {
    @NotNull TaskRunner create();
}
