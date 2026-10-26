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
    public static final ForgeConfigSpec.BooleanValue TERRALITH_NO_INTRO;
    public static final ForgeConfigSpec.BooleanValue EFFORTLESS_QUIET;
    public static final ForgeConfigSpec.BooleanValue DEBUG_SKIN;
    public static final ForgeConfigSpec.BooleanValue DEBUG_ALBUM;
    public static final ForgeConfigSpec.BooleanValue CNPC_KEEP_PLAYER_SKIN;
    public static final ForgeConfigSpec.BooleanValue CNPC_SCHEDULER_SHUTDOWN;
    public static final ForgeConfigSpec.BooleanValue FIGURA_INVENTORY_SCISSOR;
    public static final ForgeConfigSpec.BooleanValue ANOMALY_OWN_POST_EFFECT;
    public static final ForgeConfigSpec.BooleanValue PLAYER_DEBUG_LIMITS;
    public static final ForgeConfigSpec.BooleanValue BREACHING_KEEP_LIGHTS;
    public static final ForgeConfigSpec.BooleanValue TIPS_PAUSE_ONLY;
    public static final ForgeConfigSpec.BooleanValue AGP_TAGS_AFTER_JOIN;
    public static final ForgeConfigSpec.BooleanValue LSO_THERMOMETER_HUD;
    public static final ForgeConfigSpec.BooleanValue SA_FLASHLIGHT_OCCLUSION;
    public static final ForgeConfigSpec.BooleanValue SA_FLASHLIGHT_SOFT;
    public static final ForgeConfigSpec.BooleanValue SCPO_TICKS;
    public static final ForgeConfigSpec.BooleanValue CRAFTEDCORE_SYNC_ON_CHANGE;
    public static final ForgeConfigSpec.BooleanValue WALKERS_COOLDOWN_ON_CHANGE;
    public static final ForgeConfigSpec.BooleanValue REMORPHED_SYNC_ON_CHANGE;

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
        TERRALITH_NO_INTRO = B.comment("Terralith: не писать в чат приветствие с рекламой при первом запуске мира.")
                .define("terralith_no_intro", true);
        EFFORTLESS_QUIET = B.comment("Effortless Building: вне творческого режима его сообщения не показываются; в сборке игрока",
                        "(без WorldEdit) все его клавиши при запуске снимаются.")
                .define("effortless_quiet", true);
        DEBUG_SKIN = B.comment("Диагностика, клиент: первые 2 минуты после входа писать в лог смену своего скина и пакеты списка игроков про себя.")
                .define("debug_skin", false);
        DEBUG_ALBUM = B.comment("Диагностика: клики в альбоме Exposure (клиент и сервер) — в лог.")
                .define("debug_album", false);
        CNPC_KEEP_PLAYER_SKIN = B.comment("Custom NPCs, клиент: не затирать настоящий скин игрока (Ely.by) стандартным. Скин, заданный ГМом через CNPC, работает.")
                .define("cnpc_keep_player_skin", true);
        CNPC_SCHEDULER_SHUTDOWN = B.comment("Custom NPCs, сервер: закрывать его пул задач после остановки сервера (иначе процесс висел на выключении).",
                        "Потоки пула фоновые всегда.")
                .define("cnpc_scheduler_shutdown", true);
        FIGURA_INVENTORY_SCISSOR = B.comment("Figura, клиент: игрок в окне инвентаря обрезается по ванильному окошку (рамка Figura 0.1.5 стоит",
                        "как в творческом инвентаре и срезала голову по уровню глаз в обычном).")
                .define("figura_inventory_scissor", true);
        ANOMALY_OWN_POST_EFFECT = B.comment("Anomaly Rephased, клиент: снимать только свой постэффект (phosphor). Иначе он каждый тик снимал",
                        "любой — постэффект RP Medicine (боль, серость) пересобирался 20 раз в секунду, рывки.")
                .define("anomaly_own_post_effect", true);
        PLAYER_DEBUG_LIMITS = B.comment("Клиент: игрокам без op 2 на сервере нельзя хитбоксы (F3+B), границы чанков (F3+G), расширенные подсказки (F3+H),",
                        "субтитры и правую колонку F3 (блок, жидкость, сущность под прицелом). Выключается при входе, включить не дают.")
                .define("player_debug_limits", true);
        BREACHING_KEEP_LIGHTS = B.comment("TaCZ Tactical Breaching, сервер: не трогать чужие блоки света (minecraft:light). Мод при каждом входе игрока",
                        "стирал ВСЕ блоки света в радиусе ~22 блоков (±4 по высоте), а луч фонарика дубинки забирал себе и потом убирал",
                        "расставленные. С исправлением фонарик ставит и убирает только свои блоки в пустом воздухе.")
                .define("breaching_keep_lights", true);
        TIPS_PAUSE_ONLY = B.comment("Tips, клиент: цветные подсказки мода только на паузе (в углу) и на экране смерти. На экранах загрузки,",
                        "подключения и отключения подсказки показывает меню сборки (FancyMenu), без второго слоя поверх.")
                .define("tips_pause_only", true);
        SA_FLASHLIGHT_OCCLUSION = B.comment("Survivor's Arsenal, клиент: фонарь не светит сквозь стены. Фонарь, которого камера не видит, не рисуется;",
                        "дальность луча обрезается по стене, в которую он упёрся. Свой фонарь от первого лица не меняется.")
                .define("sa_flashlight_occlusion", true);
        SA_FLASHLIGHT_SOFT = B.comment("Survivor's Arsenal, клиент: когда FL Render светит фонарём по-настоящему (блоки и сущности в луче освещены),",
                        "экранный круг фонаря только подчёркивает пятно (усиление 0,55 вместо 6,4, без подъёма чёрного). Без FL Render",
                        "и с шейдерпаком круг как у мода.")
                .define("sa_flashlight_soft", true);
        AGP_TAGS_AFTER_JOIN = B.comment("A Good Place, клиент: списки блоков для анимаций установки мод читал в момент входа на сервер, когда теги",
                        "от сервера ещё не пришли. При первом входе после запуска игры он падал (Missing tag minecraft:flowers), и событие",
                        "входа не доходило до модов после него: JEI, голосовой чат, JourneyMap, TaCZ и др. — игрока приходилось кикать.",
                        "С исправлением списки читаются, когда теги пришли.")
                .define("agp_tags_after_join", true);
        LSO_THERMOMETER_HUD = B.comment("Legendary Survival Overhaul + RP Core, клиент: термометр LSO (значок температуры или градусник) — элемент",
                        "общего HUD: двигать, масштабировать и скрывать в /rphud. Сдвиги из конфига LSO тогда не действуют.",
                        "Применяется после перезапуска игры.")
                .define("lso_thermometer_hud", true);
        SCPO_TICKS = B.comment("SCP: Overtime, сервер: его процедуры каждого тика без команд и лишних пакетов. Музыка погони SCP-106 —",
                        "сброс и /stopsound всем только когда погоня кончилась (было: каждый тик от каждого игрока, N² пакетов);",
                        "выход из команд 178/966/1471 без шлема или эффекта — проверкой на Java, а не командой; function scpo:tick —",
                        "только когда на сервере есть SCP-173/023/650/096/689; «охотник во тьме» ищет только SCP-689. Поведение то же.")
                .define("scpo_tick_patches", true);
        CRAFTEDCORE_SYNC_ON_CHANGE = B.comment("CraftedCore (морфы Walkers/RemorphED), сервер: данные игрока слать ему и остальным, только когда они изменились",
                        "(было: каждый тик себе и всем в мире). При входе, возрождении, смене мира и когда игрока начинают видеть — полностью.")
                .define("craftedcore_sync_on_change", true);
        WALKERS_COOLDOWN_ON_CHANGE = B.comment("Walkers, сервер: откат способности морфа слать игроку, только когда он изменился (было: каждый тик).")
                .define("walkers_cooldown_on_change", true);
        REMORPHED_SYNC_ON_CHANGE = B.comment("RemorphED, сервер: список открытых морфов слать игроку, только когда он изменился (было: каждый тик, 20 пакетов в секунду).")
                .define("remorphed_sync_on_change", true);
        B.pop();
        SPEC = B.build();
    }
}
