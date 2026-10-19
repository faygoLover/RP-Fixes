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
    public static final ForgeConfigSpec.BooleanValue WORLDEDIT_LAZY_STATES;
    public static final ForgeConfigSpec.BooleanValue SAFE_BENDS;
    public static final ForgeConfigSpec.BooleanValue LAZY_BENDS;
    public static final ForgeConfigSpec.BooleanValue CUFFED_EMOTE_LOCK;
    public static final ForgeConfigSpec.BooleanValue HMI_AUTO_EXCLUDE;
    public static final ForgeConfigSpec.BooleanValue SECURITYCRAFT_FRAME_FOG;
    public static final ForgeConfigSpec.ConfigValue<java.util.List<? extends String>> HMI_EXCLUDE_MODS;
    public static final ForgeConfigSpec.BooleanValue PAINTINGS_GM_ONLY;
    public static final ForgeConfigSpec.IntValue PAINTINGS_GM_LEVEL;
    public static final ForgeConfigSpec.BooleanValue TERRALITH_KEEP_WORLD_TYPE;
    public static final ForgeConfigSpec.BooleanValue DEBUG_SKIN;

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
        WORLDEDIT_LAZY_STATES = B.comment("WorldEdit: не строить при старте сервера свои копии всех состояний всех блоков (в сборке ~769 тыс., ~3 ГБ),",
                        "а заводить их по типам блоков, когда WorldEdit их коснётся. Команды работают как раньше.")
                .define("worldedit_lazy_states", true);
        SAFE_BENDS = B.comment("bendy-lib + player-animation-lib, клиент: сгибать локти и колени только у кусков модели, которые это умеют",
                        "(у моделей EMF/Fresh Animations и оружейных модов сгибателя нет — без заплатки игра падает).")
                .define("safe_bends", true);
        LAZY_BENDS = B.comment("Клиент: кускам модели без сгибателя (руки и ноги EMF/Fresh Animations) повесить его при первой эмоции —",
                        "тогда и с FA+Player эмоции гнут локти и колени, а голова и глаза остаются от FA. Работает при safe_bends = true.")
                .define("lazy_bends", true);
        CUFFED_EMOTE_LOCK = B.comment("Cuffed + эмоции (player-animation-lib), клиент: связанные части тела эмоция не двигает —",
                        "руки в путах, ноги в путах, голова в колодке; в колодке-блоке эмоция не трогает ничего.")
                .define("cuffed_emote_lock", true);
        HMI_AUTO_EXCLUDE = B.comment("Hold My Items, клиент: не трогать предметы со своим видом в руке — свой отрисовщик, своя поза рук",
                        "или своя рука в 1-м лице (оружие, предметы GeckoLib...). Дополняет списки исключений HMI в памяти, его файл не меняется.")
                .define("hmi_auto_exclude", true);
        HMI_EXCLUDE_MODS = B.comment("Hold My Items: моды, которые исключить целиком (добавляются к excludedModIds из holdmyitems-client.toml).")
                .defineListAllowEmpty(java.util.List.of("hmi_exclude_mods"), () -> java.util.List.of("tacz", "superbwarfare", "lrtactical", "pointblank"),
                        o -> o instanceof String);
        SECURITYCRAFT_FRAME_FOG = B.comment("SecurityCraft, клиент: туман по дальности обзора камеры в рамке (его вставка не встаёт из-за Embeddium/Oculus).")
                .define("securitycraft_frame_fog", true);
        PAINTINGS_GM_ONLY = B.comment("Immersive Paintings, сервер: картины и граффити ставят и меняют только ГМы (права не ниже paintings_gm_level),",
                        "ломает только игрок в творческом режиме; блок, на котором висит картина, без прав ГМа не ломается.")
                .define("paintings_gm_only", true);
        PAINTINGS_GM_LEVEL = B.comment("Уровень прав (op), с которого игрок считается ГМом для картин.")
                .defineInRange("paintings_gm_level", 2, 0, 4);
        TERRALITH_KEEP_WORLD_TYPE = B.comment("Terralith: суперплоский, «один биом» и отладочный мир остаются такими, как выбраны",
                        "(его датапак подменял любой тип мира). Обычный, «большие биомы» и «расширенный» — Terralith как был.")
                .define("terralith_keep_world_type", true);
        DEBUG_SKIN = B.comment("Диагностика, клиент: первые 2 минуты после входа писать в лог смену своего скина и пакеты списка игроков про себя.")
                .define("debug_skin", false);
        B.pop();
        SPEC = B.build();
    }
}
