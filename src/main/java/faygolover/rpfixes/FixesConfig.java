package faygolover.rpfixes;

import net.minecraftforge.common.ForgeConfigSpec;

/** Конфиг RP Fixes: {@code config/rpfixes-common.toml}. */
public final class FixesConfig {
    private FixesConfig() {}

    private static final ForgeConfigSpec.Builder B = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue FIX_TAA_LEAK;
    public static final ForgeConfigSpec.BooleanValue FIX_SANITY_LEAK;

    static {
        B.comment("Исправления чужих модов. Срабатывают, только если такой мод установлен.").push("fixes");
        FIX_TAA_LEAK = B.comment("Tacz Attribute Add (taa): не держать в памяти вышедших игроков и убранных стрелков",
                        "(ShooterContext хранит последнего стрелка каждого потока сервера).")
                .define("taa_shooter_leak", true);
        FIX_SANITY_LEAK = B.comment("Sanity: Descent Into Madness (sanitydim): убирать вышедших игроков из PLAYER_TO_SPAWN_TIMEOUT",
                        "(иначе каждый вход оставляет в памяти целого игрока до перезапуска).")
                .define("sanity_player_leak", true);
        B.pop();
        SPEC = B.build();
    }
}
