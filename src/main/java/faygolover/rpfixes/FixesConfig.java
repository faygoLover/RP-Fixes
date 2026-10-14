package faygolover.rpfixes;

import net.minecraftforge.common.ForgeConfigSpec;

/** Конфиг RP Fixes: {@code config/rpfixes-common.toml}. */
public final class FixesConfig {
    private FixesConfig() {}

    private static final ForgeConfigSpec.Builder B = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue FIX_TAA_LEAK;
    public static final ForgeConfigSpec.BooleanValue FIX_SANITY_LEAK;
    public static final ForgeConfigSpec.ConfigValue<String> EMF_EMOTE_MODE;

    static {
        B.comment("Исправления чужих модов. Срабатывают, только если такой мод установлен.").push("fixes");
        FIX_TAA_LEAK = B.comment("Tacz Attribute Add (taa): не держать в памяти вышедших игроков и убранных стрелков",
                        "(ShooterContext хранит последнего стрелка каждого потока сервера).")
                .define("taa_shooter_leak", true);
        FIX_SANITY_LEAK = B.comment("Sanity: Descent Into Madness (sanitydim): убирать вышедших игроков из PLAYER_TO_SPAWN_TIMEOUT",
                        "(иначе каждый вход оставляет в памяти целого игрока до перезапуска).")
                .define("sanity_player_leak", true);
        EMF_EMOTE_MODE = B.comment("Эмоции Emotecraft и анимации RP Sports с Fresh Animations (Entity Model Features), клиент:",
                        "vanilla — пока играет эмоция, игрок рисуется обычной моделью; pause — модель Fresh Animations, но её",
                        "анимации стоят; off — не трогать.")
                .define("emf_emote_mode", "vanilla", v -> v instanceof String s && (s.equals("vanilla") || s.equals("pause") || s.equals("off")));
        B.pop();
        SPEC = B.build();
    }
}
