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
    public static final ForgeConfigSpec.BooleanValue MCREATOR_SYNC_ON_CHANGE;
    public static final ForgeConfigSpec.BooleanValue MEK_EXPLOSIVES_TIMER_LOCAL;
    public static final ForgeConfigSpec.BooleanValue CSAUG_ON_CHANGE;
    public static final ForgeConfigSpec.BooleanValue ZEROCONTACT_STAMINA_ON_CHANGE;
    public static final ForgeConfigSpec.BooleanValue TEAM_REJOIN_NOOP;
    public static final ForgeConfigSpec.DoubleValue EMOTE_STOP_DISTANCE;
    public static final ForgeConfigSpec.BooleanValue EMOTE_RESYNC;
    public static final ForgeConfigSpec.IntValue EMOTE_RESYNC_INTERVAL;
    public static final ForgeConfigSpec.IntValue EMOTE_RESYNC_WINDOW;
    public static final ForgeConfigSpec.BooleanValue SA_CROSSBOW_POSE;
    public static final ForgeConfigSpec.BooleanValue SA_BOW_POSE_SYNC;
    public static final ForgeConfigSpec.BooleanValue IHS_CHAIR_FACING;
    public static final ForgeConfigSpec.BooleanValue SBW_IFF_NEEDED_ONLY;
    public static final ForgeConfigSpec.IntValue SECURITYCRAFT_CAMERA_SYNC_SECONDS;
    public static final ForgeConfigSpec.BooleanValue BUTCHERY_GRINDER_ON_CHANGE;
    public static final ForgeConfigSpec.BooleanValue CNPC_NO_PLAYER_SOUNDS;
    public static final ForgeConfigSpec.BooleanValue CLANGINGHOWL_QUIET;
    public static final ForgeConfigSpec.BooleanValue FARMCHARM_SATURATION_ON_CHANGE;
    public static final ForgeConfigSpec.BooleanValue CARRYON_LOST_PASSENGER;

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
        MCREATOR_SYNC_ON_CHANGE = B.comment("SCP: Overtime и Mekanism Explosives (MCreator), сервер: переменные игрока и мира слать, только когда они",
                        "изменились (процедуры шлют их каждый тик; при входе, возрождении и смене измерения мод шлёт сам).")
                .define("mcreator_sync_on_change", true);
        MEK_EXPLOSIVES_TIMER_LOCAL = B.comment("Mekanism Explosives, сервер: общий таймер взрывчатки тикает без рассылки всем игрокам",
                        "(было: каждый тик в каждом измерении — все переменные мода всем, ~110 пакетов/с на игрока). У клиента таймер тикает сам.")
                .define("mekanismexplosives_timer_local", true);
        CSAUG_ON_CHANGE = B.comment("[CS] Augmentations, сервер: пакет органов игроку и модификаторы CyberSpeed/CyberAttack*/CyberArmor/CyberHealth",
                        "менять, только когда они изменились (было: каждый тик пакет и «снять и добавить» — UpdateAttributes каждый тик).")
                .define("csaugmentations_on_change", true);
        ZEROCONTACT_STAMINA_ON_CHANGE = B.comment("ZeroContact, сервер: выносливость игроку — только когда её значение или «вкл/выкл» изменились",
                        "(было: каждый тик, и при выключенной выносливости ZeroContact тоже).")
                .define("zerocontact_stamina_on_change", true);
        TEAM_REJOIN_NOOP = B.comment("Ванилла: «добавить в команду» того, кто уже в ней, ничего не делает (было: вывод и повторный вход — два",
                        "пакета всем игрокам; SCP: Overtime делает так каждый тик для шлемов SCP-178 и ПНВ, Mal-O, существ SCP-178).")
                .define("team_rejoin_noop", true);
        EMOTE_STOP_DISTANCE = B.comment("Emotecraft, сервер: игрок отошёл дальше стольких блоков от места, где начал эмоцию, — сервер заканчивает её",
                        "и рассылает «стоп» всем (иначе при потерянном «стопе» другие видят его застывшим в эмоции, хотя он ходит). 0 — выкл.")
                .defineInRange("emote_stop_distance", 2.0, 0.0, 64.0);
        EMOTE_RESYNC = B.comment("Emotecraft, сервер: выравнивание. Если у игрока сейчас нет эмоции, а последняя кончилась недавно, раз в",
                        "emote_resync_interval тиков всем, кто его видит, снова уходит «стоп» этой эмоции (несколько байт): застрявшая у",
                        "кого-то эмоция (лёг, а сам ходит) снимается за это время. Играющим эмоцию ничего не досылается.")
                .define("emote_resync", true);
        EMOTE_RESYNC_INTERVAL = B.comment("Emotecraft, выравнивание: раз во сколько тиков слать «стоп» (кратно 10; 100 = 5 с).")
                .defineInRange("emote_resync_interval", 100, 10, 72000);
        EMOTE_RESYNC_WINDOW = B.comment("Emotecraft, выравнивание: сколько секунд после конца эмоции ещё слать её «стоп».")
                .defineInRange("emote_resync_window", 600, 1, 86400);
        SA_CROSSBOW_POSE = B.comment("Survivor's Arsenal, клиент: тактический и самодельный арбалет и гвоздомёт в руке — у всех игроков (и у себя",
                        "в виде от 3-го лица) поза «оружие перед собой» двумя руками, как заряженный ванильный арбалет. У мода позы нет.")
                .define("sa_crossbow_pose", true);
        SA_BOW_POSE_SYNC = B.comment("Survivor's Arsenal: блочный лук — другие видят, что игрок натягивает или целится (поза лука), а не лук у ног.",
                        "Сервер рассылает «натягивает / нет» своим пакетом; нужно на сервере и у игрока. Свой игрок — как у мода.")
                .define("sa_bow_pose_sync", true);
        IHS_CHAIR_FACING = B.comment("industrialhellscape: на стуле (офисные кресла) игрок при посадке поворачивается вперёд (от спинки), взгляд — не дальше",
                        "80° от стула (как стулья SovietEra). Было: садились лицом к спинке, у других тело оставалось, как при посадке.")
                .define("ihs_chair_facing", true);
        SBW_IFF_NEEDED_ONLY = B.comment("Superb Warfare, сервер: положения союзников (PlayerInfoSync, EntityRelationSync) — только тем, у кого",
                        "тактическая карта включена (enable_tactical_map) и терминал надет. Было: каждый тик каждому про каждого, N² пакетов.")
                .define("sbw_iff_needed_only", true);
        SECURITYCRAFT_CAMERA_SYNC_SECONDS = B.comment("SecurityCraft, сервер: вращающуюся камеру сверять с игроками раз во столько секунд (клиент крутит её сам).",
                        "0 — как у мода: весь блок каждый тик.")
                .defineInRange("securitycraft_camera_sync_seconds", 5, 0, 600);
        BUTCHERY_GRINDER_ON_CHANGE = B.comment("Butchery, сервер: мясорубка шлёт обновление блока, только когда он изменился (было: дважды за тик, и пустая тоже).")
                .define("butchery_grinder_on_change", true);
        CNPC_NO_PLAYER_SOUNDS = B.comment("Custom NPCs, клиент: не слать серверу каждый свой звук (нужно только скриптам игрока на playSound, у нас их нет).")
                .define("customnpcs_no_player_sounds", true);
        CLANGINGHOWL_QUIET = B.comment("Clanging Howl: клиент шлёт «двигаюсь» только про себя и при смене (было: каждый тик про каждое видимое существо),",
                        "мобам с эффектами это считает сервер; счётчик «в воздухе» сервер не рассылает каждый тик — клиент считает его сам.")
                .define("clanginghowl_quiet", true);
        FARMCHARM_SATURATION_ON_CHANGE = B.comment("farm_and_charm, сервер: сытость животного — при смене и раз в 10 с, только тем, у кого загружен его чанк",
                        "(было: каждое животное каждый тик всем игрокам измерения).")
                .define("farm_and_charm_saturation_on_change", true);
        CARRYON_LOST_PASSENGER = B.comment("Carry On, сервер: несёт игрока, а пассажира уже нет (вышел, слез, телепортирован) — при ПКМ по сущности ношу",
                        "сбросить (было: NPE в tryStackEntity, и несущий «нёс пустоту», не мог ничего поднять).")
                .define("carryon_lost_passenger", true);
        B.pop();
        SPEC = B.build();
    }
}
