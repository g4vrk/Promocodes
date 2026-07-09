package com.g4vrk.promocodes;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class PermissionConstants {

    public final @NotNull String PERMISSION_PREFIX = "promocodes.";

    public final @NotNull String ADMIN = PERMISSION_PREFIX + "admin";

}
