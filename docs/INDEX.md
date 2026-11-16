# RP Fixes — указатель кода

Создано `python _family/tools/mapgen.py`, версия мода 1.5.9-1.20.1-forge.
**Руками не править**: пересоздаётся скриптом. Смысл, потоки работы и рецепты — в `docs/MAP.md`.
Разбор по шаблонам, без компиляции: необычно оформленная регистрация может отсутствовать в списках.

Всего: 107 файлов, 4405 строк Java.

## 1. Пакеты

| Пакет | Файлов | Строк |
|---|---:|---:|
| `faygolover.rpfixes` | 3 | 398 |
| `faygolover.rpfixes.compat` | 1 | 31 |
| `faygolover.rpfixes.fix` | 34 | 1939 |
| `faygolover.rpfixes.mixin` | 1 | 64 |
| `faygolover.rpfixes.mixin.agoodplace` | 1 | 25 |
| `faygolover.rpfixes.mixin.anomaly` | 1 | 20 |
| `faygolover.rpfixes.mixin.bendy` | 2 | 67 |
| `faygolover.rpfixes.mixin.breaching` | 1 | 62 |
| `faygolover.rpfixes.mixin.butchery` | 1 | 23 |
| `faygolover.rpfixes.mixin.carryon` | 1 | 32 |
| `faygolover.rpfixes.mixin.clanginghowl` | 2 | 59 |
| `faygolover.rpfixes.mixin.craftedcore` | 1 | 30 |
| `faygolover.rpfixes.mixin.csaug` | 1 | 80 |
| `faygolover.rpfixes.mixin.cuffed` | 2 | 44 |
| `faygolover.rpfixes.mixin.cufflocks` | 1 | 32 |
| `faygolover.rpfixes.mixin.customnpcs` | 3 | 62 |
| `faygolover.rpfixes.mixin.debuglimits` | 5 | 92 |
| `faygolover.rpfixes.mixin.effortless` | 1 | 21 |
| `faygolover.rpfixes.mixin.emotecraft` | 3 | 100 |
| `faygolover.rpfixes.mixin.farmandcharm` | 1 | 34 |
| `faygolover.rpfixes.mixin.figura` | 1 | 23 |
| `faygolover.rpfixes.mixin.glowsticks` | 4 | 91 |
| `faygolover.rpfixes.mixin.ihs` | 1 | 67 |
| `faygolover.rpfixes.mixin.mcreator` | 6 | 129 |
| `faygolover.rpfixes.mixin.remorphed` | 1 | 23 |
| `faygolover.rpfixes.mixin.saflashlight` | 3 | 130 |
| `faygolover.rpfixes.mixin.salocks` | 1 | 28 |
| `faygolover.rpfixes.mixin.saweapons` | 4 | 100 |
| `faygolover.rpfixes.mixin.sbw` | 1 | 33 |
| `faygolover.rpfixes.mixin.scpo` | 8 | 251 |
| `faygolover.rpfixes.mixin.securitycraft` | 2 | 75 |
| `faygolover.rpfixes.mixin.simpleradio` | 1 | 21 |
| `faygolover.rpfixes.mixin.terralith` | 1 | 56 |
| `faygolover.rpfixes.mixin.tips` | 1 | 35 |
| `faygolover.rpfixes.mixin.vanilla` | 1 | 29 |
| `faygolover.rpfixes.mixin.walkers` | 1 | 18 |
| `faygolover.rpfixes.mixin.worldedit` | 2 | 48 |
| `faygolover.rpfixes.mixin.zerocontact` | 2 | 33 |

## 2. Классы

### `faygolover.rpfixes`
- `FixesConfig`, 232 — Конфиг RP Fixes: config/rpfixes-common.toml.
- `FixesNet`, 63 — Свой сетевой канал RP Fixes (с 1.5.4).
- `RpFixes`, 103 — RP Fixes — заплатки чужих модов сборки (ревью 11.10, п.

### `faygolover.rpfixes.compat`
- `FlRenderCompat`, 31 — FL Render (необязательный): светит ли сейчас настоящий свет фонаря.

### `faygolover.rpfixes.fix`
- `AGoodPlaceTags`, 48 — A Good Place: списки блоков анимаций разбираются, когда клиент получил теги от сервера (на входе их ещё нет, см.
- `AnomalyPostEffect`, 26 — Anomaly Rephased (MCreator, PlayerCanSeeAnomalyProcedure): каждый тик игрока без своего эффекта «Тревога» снимает постэффект экрана — любой, не только свой phosphor.
- `BlockSyncOnChange`, 28 — Butchery, мясорубка: отпечаток последнего отправленного состояния блока (данные блока + состояние).
- `CarryOnHand`, 24 — Carry On, клиент: ношу от 1-го лица Carry On рисует в RenderHandEvent обычного приоритета, отменённое событие он не получает.
- `ClangingHowlClient`, 22 — Clanging Howl, клиент: «двигаюсь» серверу — только про себя, при смене и раз в 5 с на всякий случай.
- `CnpcPlayerSkins`, 47 — Custom NPCs и скины игроков (Ely.by).
- `CnpcScheduler`, 51 — Custom NPCs держит свой пул задач (CustomNPCsScheduler, Executors.newScheduledThreadPool(1)): его поток — не фоновый, и пул никто не закрывает.
- `CraftedCoreSync`, 84 — CraftedCore / Walkers (морфы): данные игрока уходят только при изменении, а не каждый тик.
- `CsAugSync`, 51 — [CS] Augmentations: пакет органов и модификаторы — только при изменении (см.
- `CuffedEmotes`, 47 — Эмоции (player-animation-lib: Emotecraft, RP Sports, Better Combat) на связанном игроке (Cuffed): анимация играет, но связанные части остаются как их держит Cuffed.
- `CuffedSelfKey`, 29 — Cuffed: ключ работает только по другому игроку, поэтому скованный по ногам не мог снять путы сам, хотя руки свободны.
- `EffortlessKeys`, 41 — Клиентская половина EffortlessQuiet: в сборке игрока снять клавиши Effortless Building.
- `EffortlessQuiet`, 23 — Effortless Building у игроков (20.10.2026): в survival он и так выключен (allowInSurvival=false), но писал в чат на Alt, Ctrl+Z и т.
- `EmoteConfigRetry`, 44 — Emotecraft, клиент: свои настройки клиент шлёт серверу, только когда до него дошли оба события — вход (LoggingIn) и регистрация канала.
- `EmoteFixes`, 57 — Эмоции Emotecraft и анимации RP Sports (player-animation-lib) с Fresh Animations (Entity Model Features): обе стороны двигают модель игрока, и EMF перекладывает части поверх эмоции — персонаж садится «раком», заваливается набок (н…
- `EmoteStale`, 106 — Emotecraft, сервер: зацикленная эмоция (лечь, сесть) считается играющей, пока от игрока не придёт «стоп».
- `FarmCharmSync`, 32 — farm_and_charm: сытость животного — при смене и раз в 10 с, только тем, у кого загружен его чанк.
- `FiguraInventoryScissor`, 28 — Figura 0.1.5 рисует игрока в окне инвентаря сама и обрезает его рамкой (x-15, y-39)…(x+17, y+4) — это окошко творческого инвентаря (там size = 20).
- `HmiAutoExclude`, 135 — Hold My Items рисует руку и предмет в 1-м лице по-своему и спорит с модами, у которых свой вид предмета в руке (оружие TaCZ/Superb Warfare/LesRaisins, предметы GeckoLib и т.п.).
- `LazyBends`, 9 — Часть тела, которую сейчас ставит эмоция (AnimationApplier.updatePart), — для направления сгиба в BendHelperMixin.
- `LeakFixes`, 72 — Заплатки от утечек памяти чужих модов (найдены по дампу кучи сервера «Департамент S5» 07.10.2026): Tacz Attribute Add 1.4.0 — ShooterContext.shooterContext (Map&lt;Thread, LivingEntity&gt;) держит последнего стрелка каждого потока…
- `LsoThermometerHud`, 94 — Legendary Survival Overhaul, клиент: термометр (значок температуры над хотбаром или градусник с цифрами, если термометр надет в Curios) — элемент общего HUD RP Core.
- `McreatorSync`, 55 — Моды MCreator (SCP: Overtime, Mekanism Explosives): переменные игрока (syncPlayerVariables) и мира (syncData — всем игрокам сервера или измерения) процедуры шлют каждый тик, даже если ничего не поменялось.
- `PaintingsGmOnly`, 99 — Immersive Paintings: картины и граффити — инструмент ГМа.
- `PerkLocks`, 63 — Особенности RP Perks в чужих модах (решения Алекса 06.11, RP Perks 2.8.1): «Взлом» — только он вскрывает замки отмычкой: Survivor's Arsenal (ящики, двери, навесные замки) и Cuffed (замки, двери камер и сейфы, наручники).
- `PlayerDebugLimits`, 50 — Игрокам без op 2 на сервере — без отладочных подсказок, которые дают лишнее знание: хитбоксы (F3+B, видны и невидимые сущности), расширенные подсказки предметов (F3+H), границы чанков (F3+G), субтитры (сторона источника звука за с…
- `SaBowSync`, 62 — Survivor's Arsenal 1.0.0, сервер: блочный лук (CompoundBowItem) ставит позу «натягиваю» только своему игроку и по своему клиентскому состоянию — другие видят лук у ног.
- `SaWeaponPoses`, 62 — Survivor's Arsenal 1.0.0, клиент: поза рук с оружием мода у игроков в виде от 3-го лица.
- `SbwSync`, 23 — Superb Warfare: положения и «свой/чужой» союзников нужны только тактической карте у того, на ком терминал.
- `ScpoTicks`, 246 — SCP: Overtime 2.1 (MCreator): процедуры, которые каждый тик выполняли команды или слали пакеты всем.
- `TerralithIntro`, 29 — Terralith при первом запуске мира пишет всем в чат рекламу (Discord, хостинг): функция terralith:setup из тега minecraft:load ставит terralith:toast, если у %USED в tr.first меньше 2.
- `WalkersCooldownSync`, 31 — Walkers: откат способности уходит игроку, только когда изменился (грузится только при установленном Walkers).
- `WorldEditLazyStates`, 95 — Ленивые состояния блоков WorldEdit.
- `ZeroContactSync`, 26 — ZeroContact: последнее отправленное игроку значение выносливости и её «вкл/выкл».

### `faygolover.rpfixes.mixin`
- `RpFixesMixinPlugin`, 64 — Вставки в чужие моды применяются, только если этот мод есть в сборке (иначе Mixin ругался бы на отсутствующие цели).

### `faygolover.rpfixes.mixin.agoodplace`
- `AGoodPlaceLoginMixin`, 25 — A Good Place 1.2.8, клиент: на входе на сервер мод разбирает свои списки блоков с тегами, а теги сервер шлёт позже.

### `faygolover.rpfixes.mixin.anomaly`
- `PlayerCanSeeAnomalyPostEffectMixin`, 20 — Anomaly Rephased снимает только свой постэффект — см.

### `faygolover.rpfixes.mixin.bendy`
- `AnimationApplierBendMixin`, 24 — Запомнить, какую часть тела сейчас ставит эмоция — чтобы знать, куда гнуть (см.
- `BendHelperMixin`, 43 — Сгибы player-animation-lib через bendy-lib — только там, где они возможны.

### `faygolover.rpfixes.mixin.breaching`
- `NightstickLightsMixin`, 62 — TaCZ Tactical Breaching 1.0.5: фонарик на дубинке (Nightstick) светит настоящими блоками minecraft:light.

### `faygolover.rpfixes.mixin.butchery`
- `MeatGrinderSyncMixin`, 23 — Butchery, мясорубка (MCreator): процедура каждого тика после каждой записи в блок зовёт sendBlockUpdated — пустая мясорубка дважды за тик шлёт весь блок всем, кто видит чанк.

### `faygolover.rpfixes.mixin.carryon`
- `LostPassengerMixin`, 32 — Carry On 2.1.2.7, PlacementHandler.tryStackEntity: у несущего игрока тип «несу игрока», а пассажира уже нет (несомый вышел, слез, телепортировался) — getFirstPassenger() null и NPE на каждом ПКМ по сущности (живой 01.11, 9 раз), а…

### `faygolover.rpfixes.mixin.clanginghowl`
- `AirTicksSyncMixin`, 22 — Clanging Howl, CHCapHelper.setTicksInAir: пока игрок не на земле (прыжок, падение, сидит на стуле, полёт ГМа), сервер каждый тик слал всю капу ему и всем рядом (436/с на живом 31.10).
- `MovingSyncMixin`, 37 — Clanging Howl, CHEvents.TickEvent: клиент каждый тик для КАЖДОЙ видимой живой сущности слал серверу CIsMovingPacket (~67/с от игрока).

### `faygolover.rpfixes.mixin.craftedcore`
- `PlayerDataSyncMixin`, 30 — CraftedCore 5.8.x: PlayerDataSynchronizer.sync зовётся каждый тик каждого игрока (ServerPlayerMixin.serverTick) и шлёт все его данные ему и всем игрокам мира — ~20 пакетов в секунду на пару игроков.

### `faygolover.rpfixes.mixin.csaug`
- `OrganDataMixin`, 80 — [CS] Augmentations 1.0.6.x: OrganData.updateOrganData() и organTick() идут каждый тик каждого игрока (ForgeEvents playerInventory): пакет органов себе (S2CSyncDataPacket) и «снять и снова добавить» модификаторы скорости, урона, ск…

### `faygolover.rpfixes.mixin.cuffed`
- `AnimationApplierCuffedMixin`, 19 — Связанную часть тела эмоция не двигает: остаётся поза Cuffed.
- `PlayerModelCuffedMixin`, 25 — Запомнить, чью модель сейчас ставят — для AnimationApplierCuffedMixin.

### `faygolover.rpfixes.mixin.cufflocks`
- `CuffedLockpickMixin`, 32 — Cuffed: взлом отмычкой (навесной замок, дверь камеры и сейф, наручники) — только с особенностью «Взлом».

### `faygolover.rpfixes.mixin.customnpcs`
- `CnpcSchedulerAccess` (interface), 18 — Пул задач Custom NPCs — для faygolover.rpfixes.fix.CnpcScheduler.
- `PlayerSoundMixin`, 23 — Custom NPCs, клиент: каждый звук, который играет у игрока (шаги, удары, музыка), уходил серверу SPacketPlayerSound — ради скриптового события игрока playSound (~60 пакетов/с от каждого).
- `SkinUtilMixin`, 21 — Custom NPCs не затирает настоящий скин игрока стандартным — см.

### `faygolover.rpfixes.mixin.debuglimits`
- `DebugRendererAccess` (interface), 13 — Включены ли границы чанков (поле закрыто).
- `DebugScreenTargetedMixin`, 21 — Правая колонка F3 у игроков без op 2 — только система (Java, память, процессор, экран), без блока, жидкости и сущности под прицелом — см.
- `KeyboardDebugKeysMixin`, 18 — F3+B, F3+G, F3+H у игроков без op 2 ничего не делают — см.
- `LocalPlayerPermissionMixin`, 18 — Сервер шлёт уровень прав при входе и при смене op — тогда один раз и выключаем — см.
- `SubtitlesOptionMixin`, 22 — Субтитры у игроков без op 2 не включаются (проверка только в момент переключения) — см.

### `faygolover.rpfixes.mixin.effortless`
- `EffortlessLogMixin`, 21 — Все сообщения Effortless Building игроку (и с клиента, и с сервера, и переводимые) идут через EffortlessBuilding.log(Player, String, boolean) — вне творческого режима глушим.

### `faygolover.rpfixes.mixin.emotecraft`
- `ClientRepeatMixin`, 26 — Emotecraft, клиент: ClientEmotePlay.clientRepeatLocalEmote — когда свой игрок играет эмоцию и рядом появляется другой, клиент сам шлёт ему эмоцию, если думает, что сервер эмоции не ведёт.
- `EmoteSendForPlayerMixin`, 59 — Emotecraft 2.2.7, сервер: когда один игрок начинает видеть другого, который сейчас играет эмоцию, мод шлёт ему эту эмоцию с текущего кадра.
- `EmoteServerNetworkAccess` (interface), 15 — Emotecraft 2.2.7, сервер: разослать пакет всем, кто видит игрока (кроме него).

### `faygolover.rpfixes.mixin.farmandcharm`
- `SaturationSyncMixin`, 34 — farm_and_charm, PacketHandler.sendSaturationSync: каждая корова, овца, свинья и курица каждый тик слала свою сытость ВСЕМ игрокам измерения (животные × игроки × 20; всплески у ферм).

### `faygolover.rpfixes.mixin.figura`
- `UIHelperInventoryScissorMixin`, 23 — Figura: рамка игрока в инвентаре выживания — как у ванилы — см.

### `faygolover.rpfixes.mixin.glowsticks`
- `CreativeGlowStickNoCollisionMixin`, 24 — Glow Sticks 7.5.1: творческая ХИС без коллизии, как и обычная (GlowStickNoCollisionMixin).
- `GlowStickLightMixin`, 22 — Glow Sticks 7.5.1: лежащая ХИС светит не ярче 8 (было 15), гаснет так же ровно — 15 ступеней мода сжаты в 8..1; под управлением редстоуна сила сигнала тоже сжимается до 8, без сигнала — 0.
- `GlowStickNoCollisionMixin`, 27 — Glow Sticks 7.5.1: лежащая ХИС без коллизии.
- `GlowStickTrailLightMixin`, 18 — Glow Sticks 7.5.1: невидимый блок света за летящей ХИС (и в воде) — 8 вместо 15.

### `faygolover.rpfixes.mixin.ihs`
- `SittableEntityMixin`, 67 — industrialhellscape 0.3.6A, сиденье стульев (SittableEntity, офисные кресла): сущность ставится с поворотом 0 и поворот седока никак не держит.

### `faygolover.rpfixes.mixin.mcreator`
- `MekMapVarsSyncMixin`, 20 — Mekanism Explosives, переменные сервера: рассылка syncData только при изменении (см.
- `MekTimersMixin`, 29 — Mekanism Explosives: TimersProcedure в каждом измерении каждый тик прибавляет GlobalTimer и рассылает все переменные мода всем игрокам (SavedDataSyncMessage, ~110 пакетов в секунду на игрока при трёх измерениях).
- `MekWorldVarsSyncMixin`, 20 — Mekanism Explosives, переменные измерения: рассылка syncData только при изменении (см.
- `ScpoMapVarsSyncMixin`, 20 — SCP: Overtime, переменные сервера: рассылка syncData только при изменении (см.
- `ScpoPlayerVarsSyncMixin`, 20 — SCP: Overtime: переменные игрока — пакет только при изменении (см.
- `ScpoWorldVarsSyncMixin`, 20 — SCP: Overtime, переменные измерения: рассылка syncData только при изменении (см.

### `faygolover.rpfixes.mixin.remorphed`
- `UnlockedSyncMixin`, 23 — RemorphED 4.x: список открытых морфов (remorphed:unlocked_sync) уходит игроку каждый тик (PlayerEntityMixin) — 20 пакетов в секунду на игрока.

### `faygolover.rpfixes.mixin.saflashlight`
- `FlashlightBeamOcclusionMixin`, 63 — Survivor's Arsenal (sa_combat) 1.0.0, клиент: фонарь светит экранным эффектом — осветляет каждый видимый пиксель в своём конусе (до 38 блоков), стен между фонарём и поверхностью он не знает.
- `FlashlightBeamRangeAccess` (interface), 15 — Дальность луча фонаря Survivor's Arsenal (final у мода) — для FlashlightBeamOcclusionMixin.
- `FlashlightShaderMixin`, 52 — Survivor's Arsenal (sa_combat) 1.0.0, клиент: экранный круг фонаря умножает уже тёмный кадр на 6,4 и добавляет подъём — в полной тьме цвета взять неоткуда (серое), светлое упирается в белый (люди белые).

### `faygolover.rpfixes.mixin.salocks`
- `SaLockpickMixin`, 28 — Survivor's Arsenal: взлом замка отмычкой (ящик с лутом, дверь, навесной замок) — только с особенностью «Взлом».

### `faygolover.rpfixes.mixin.saweapons`
- `CompoundBowServerStateAccess` (interface), 17 — Survivor's Arsenal: таблица состояний блочного лука по игрокам (значения — CompoundBowStateAccess).
- `CompoundBowServerStateMixin`, 43 — Survivor's Arsenal 1.0.0, сервер: после каждого изменения состояния блочного лука (начал / бросил натягивать, прицелился / опустил, выстрелил, сменил предмет) — SaBowSync рассылает «натягивает / нет» видящим игрока.
- `CompoundBowStateAccess` (interface), 15 — Survivor's Arsenal: состояние блочного лука одного игрока на сервере.
- `PlayerRendererArmPoseMixin`, 25 — Поза руки игрока с оружием Survivor's Arsenal (SaWeaponPoses).

### `faygolover.rpfixes.mixin.sbw`
- `IffSyncMixin`, 33 — Superb Warfare, IffItem.onServerTick: каждый тик каждому игроку про каждого из той же команды — PlayerInfoSync и EntityRelationSync (N² пакетов, 615 КБ/с при 16 игроках на живом 31.10).

### `faygolover.rpfixes.mixin.scpo`
- `ScpoChaseThemeMixin`, 24 — SCP: Overtime: музыка погони SCP-106 без N² /stopsound в тик (см.
- `ScpoCommandCacheMixin`, 62 — SCP: Overtime: процедуры тика выполняют команды строкой (частицы, tp, team join, effect...) — разбор строки каждый тик на каждую сущность.
- `ScpoEffectRefreshMixin`, 55 — SCP: Overtime: процедуры тика (игрок, сущность, эффект, предмет в руке/на голове) каждый тик заново дают короткий эффект — у игрока это пакет эффекта каждый тик (присед, костюм химзащиты, «Когда наступает день», ПНВ...).
- `ScpoHunterMixin`, 24 — SCP: Overtime: «охотник во тьме» ищет только SCP-689, а не все сущности в 25 блоках (см.
- `ScpoRemoveMalOMixin`, 22 — SCP: Overtime: выход из команды — Mal-O (команда 1471) — проверкой на Java вместо команды каждый тик (см.
- `ScpoRemoveNVGMixin`, 22 — SCP: Overtime: выход из команды — ПНВ (команда 966) — проверкой на Java вместо команды каждый тик (см.
- `ScpoRemoveTeam178Mixin`, 22 — SCP: Overtime: выход из команды — SCP-178 (команда 178) — проверкой на Java вместо команды каждый тик (см.
- `ScpoSculptureTickMixin`, 20 — SCP: Overtime: function scpo:tick (шесть @e по всем мирам) — только когда такие SCP есть на сервере.

### `faygolover.rpfixes.mixin.securitycraft`
- `CameraRotationSyncMixin`, 25 — SecurityCraft, камера: вращающаяся камера на сервере каждый тик зовёт sendBlockUpdated — весь блок (владелец, опции, модули) всем, кто видит чанк (780 пакетов/с, 341 КБ/с на живом 31.10).
- `FogRendererFrameFeedMixin`, 50 — SecurityCraft, камера в рамке (frame feed): пока он снимает вид с камеры, туман должен идти по дальности обзора рамки.

### `faygolover.rpfixes.mixin.simpleradio`
- `RadioTuneMixin`, 21 — Simple Radio: «Технофоб» (RP Perks) слушает и говорит, но частоту и модуляцию рации не меняет — сервер пакет пропускает.

### `faygolover.rpfixes.mixin.terralith`
- `WorldDimensionsMixin`, 56 — Terralith кладёт в датапак своё описание верхнего мира (data/minecraft/dimension/overworld.json), а WorldDimensions#bake берёт измерение из датапака раньше выбранного типа мира — суперплоский, «один биом» и отладочный мир превраща…

### `faygolover.rpfixes.mixin.tips`
- `TipRenderHandlerMixin`, 35 — Tips 12.1.9, клиент: свои цветные подсказки мод рисует поверх загрузки мира, подключения, отключения, сохранения, паузы и экрана смерти.

### `faygolover.rpfixes.mixin.vanilla`
- `TeamRejoinMixin`, 29 — Ванилла: «добавить в команду» того, кто уже в ней, выводит его из команды и добавляет снова — два пакета всем игрокам.

### `faygolover.rpfixes.mixin.walkers`
- `AbilityCooldownSyncMixin`, 18 — Walkers 5.8.x: откат способности шлётся игроку каждый тик (PlayerEntityTickMixin) — теперь только когда он изменился.

### `faygolover.rpfixes.mixin.worldedit`
- `WeBlockStateMixin`, 22 — Состояния одного типа блока построены (лениво, при первом обращении) — регистрируем их ID, как раньше делал ready().
- `WorldEditingCapabilityMixin`, 26 — Capability.WORLD_EDITING.ready() (анонимный класс Capability$4) при старте сервера строит и регистрирует все состояния всех блоков — в сборке их ~769 тыс., это ~3 ГБ кучи.

### `faygolover.rpfixes.mixin.zerocontact`
- `StaminaPacketAccess` (interface), 12 — ZeroContact: содержимое пакета выносливости — для сравнения с последним отправленным.
- `StaminaSyncMixin`, 21 — ZeroContact: PlayerStamina.staminaTick шлёт игроку выносливость каждый тик — и когда его выносливость выключена (staminaState = false, у нас своя RP Stamina).

Классов без описания: 0 из 107.

## 3. Подписки на события

| Событие | Кто слушает |
|---|---|
| `AttackEntityEvent` | `PaintingsGmOnly.onAttack` |
| `BlockEvent.BreakEvent` | `PaintingsGmOnly.onBreak` |
| `ClientPlayerNetworkEvent.LoggingIn` | `EmoteConfigRetry.onLogin` |
| `ClientPlayerNetworkEvent.LoggingOut` | `EmoteConfigRetry.onLogout` |
| `EntityJoinLevelEvent` | `PaintingsGmOnly.onJoin`, `ScpoTicks.onJoin` |
| `EntityLeaveLevelEvent` | `ScpoTicks.onLeave` |
| `PlayerEvent` | `CraftedCoreSync.forget` |
| `PlayerEvent.Clone` | `SaBowSync.onClone` |
| `PlayerEvent.PlayerChangedDimensionEvent` | `CraftedCoreSync.onDimension`, `CsAugSync.onDimension`, `McreatorSync.onDimension`, `WalkersCooldownSync.onDimension` |
| `PlayerEvent.PlayerLoggedInEvent` | `CraftedCoreSync.onLogin` |
| `PlayerEvent.PlayerLoggedOutEvent` | `CraftedCoreSync.onLogout`, `LeakFixes.onLogout`, `SaBowSync.onLogout` |
| `PlayerEvent.PlayerRespawnEvent` | `CraftedCoreSync.onRespawn` |
| `PlayerEvent.StartTracking` | `CraftedCoreSync.onStartTracking`, `SaBowSync.onStartTracking` |
| `PlayerInteractEvent.EntityInteract` | `PaintingsGmOnly.onInteract` |
| `PlayerInteractEvent.EntityInteractSpecific` | `PaintingsGmOnly.onInteractSpecific` |
| `PlayerInteractEvent.RightClickBlock` | `PaintingsGmOnly.onUseOnBlock`, `PerkLocks.onUseBlock` |
| `PlayerInteractEvent.RightClickItem` | `CuffedSelfKey.onRightClickItem` |
| `RenderGuiOverlayEvent.Pre` | `LsoThermometerHud.onOverlay` |
| `RenderHandEvent` | `CarryOnHand.onRenderHand` |
| `ServerStartedEvent` | `TerralithIntro.onServerStarted` |
| `ServerStoppedEvent` | `CnpcScheduler.onServerStopped` |
| `TagsUpdatedEvent` | `AGoodPlaceTags.onTags` |
| `TickEvent.ClientTickEvent` | `EffortlessKeys.onClientTick`, `EmoteConfigRetry.onClientTick`, `HmiAutoExclude.onClientTick` |
| `TickEvent.ServerTickEvent` | `EmoteStale.onServerTick`, `LeakFixes.onServerTick` |

## 4. Сеть

Канал: `faygolover/rpfixes/FixesNet.java`, имя `main`, версия протокола `не объявлена константой`.
- `BowPose` (направление не указано в регистрации)

Классы с обработчиками пакетов (1): `FixesNet`.

## 5. Команды

Нет.

## 6. Данные

- свои файлы на диске: `HmiAutoExclude`
- getPersistentData: `ScpoTicks` ×3

## 7. Конфиги

- common: `FixesConfig`, вызовов define 54

## 8. Что берёт из RP Core и что ему даёт

Методы `RpCoreAPI` (сколько вызовов): `hasTrait` 3.

## 9. Связи с другими модами

Проверки `isLoaded`: `a_good_place` (AGoodPlaceTags); `carryon` (RpFixes); `craftedcore` (RpFixes); `csaugmentations` (RpFixes); `cuffed` (RpFixes); `customnpcs` (CnpcScheduler); `effortlessbuilding` (EffortlessKeys); `emotecraft` (RpFixes); `entity_model_features` (EmoteFixes); `flrender` (FlRenderCompat); `holdmyitems` (HmiAutoExclude); `legendarysurvivaloverhaul` (LsoThermometerHud, RpFixes); `mekanismexplosives` (RpFixes); `playeranimator` (EmoteFixes); `rpcore` (LsoThermometerHud, RpFixes); `rpperks` (PerkLocks); `sa_combat` (RpFixes); `scpo` (RpFixes); `taa` (LeakFixes); `terralith` (TerralithIntro); `walkers` (RpFixes); `worldedit` (EffortlessKeys).

Отражение (`Class.forName`):
- `AGoodPlaceTags` → `nl.enjarai.a_good_place.pack.AnimationsManager`
- `CnpcPlayerSkins` → `noppes.npcs.controllers.PlayerSkinController`
- `EffortlessKeys` → `nl.requios.effortlessbuilding.ClientEvents`
- `EmoteFixes` → `dev.kosmx.playerAnim.api.IPlayer`
- `EmoteFixes` → `dev.kosmx.playerAnim.api.layered.AnimationStack`
- `EmoteFixes` → `traben.entity_model_features.EMFAnimationApi`
- `FogRendererFrameFeedMixin` → `net.geforcemods.securitycraft.entity.camera.FrameFeedHandler`
- `FogRendererFrameFeedMixin` → `net.geforcemods.securitycraft.blockentities.FrameBlockEntity`

Пакеты других модов семейства в коде: `flrender` — 1 файлов; `rpcore` — 2 файлов.

Миксины (68):
- `worldedit.WorldEditingCapabilityMixin` (mixins) → targets = "com.sk89q.worldedit.extension.platform.Capability$4", remap = false
- `worldedit.WeBlockStateMixin` (mixins) → BlockState.class, remap = false
- `terralith.WorldDimensionsMixin` (mixins) → WorldDimensions.class
- `customnpcs.CnpcSchedulerAccess` (mixins) → targets = "noppes.npcs.util.CustomNPCsScheduler", remap = false
- `effortless.EffortlessLogMixin` (mixins) → targets = "nl.requios.effortlessbuilding.EffortlessBuilding", remap = false
- `breaching.NightstickLightsMixin` (mixins) → targets = "com.victoriomods.taczballisticbreaching.NightstickFlashlightLightHandler", remap = false
- `scpo.ScpoChaseThemeMixin` (mixins) → targets = "net.scpo.procedures.ChaseThemeProcedure", remap = false
- `scpo.ScpoRemoveTeam178Mixin` (mixins) → targets = "net.scpo.procedures.RemoveTeam178Procedure", remap = false
- `scpo.ScpoRemoveNVGMixin` (mixins) → targets = "net.scpo.procedures.RemoveNVGProcedure", remap = false
- `scpo.ScpoRemoveMalOMixin` (mixins) → targets = "net.scpo.procedures.RemoveMalOProcedure", remap = false
- `scpo.ScpoSculptureTickMixin` (mixins) → targets = "net.scpo.procedures.MobTickSculptureProcedure", remap = false
- `scpo.ScpoHunterMixin` (mixins) → targets = "net.scpo.procedures.ProcedureHunterProcedure", remap = false
- `craftedcore.PlayerDataSyncMixin` (mixins) → targets = "tocraft.craftedcore.data.PlayerDataSynchronizer", remap = false
- `walkers.AbilityCooldownSyncMixin` (mixins) → targets = "tocraft.walkers.api.PlayerAbilities", remap = false
- `remorphed.UnlockedSyncMixin` (mixins) → targets = "tocraft.remorphed.Remorphed", remap = false
- `scpo.ScpoEffectRefreshMixin` (mixins) → targets = { "net.scpo.procedures.AmnesiaOnEffectActiveTickProcedure", "net.scpo.procedures.Attack093UncleanPro
- `scpo.ScpoCommandCacheMixin` (mixins) → targets = { "net.scpo.procedures.BabySCP939OnEntityTickUpdateProcedure", "net.scpo.procedures.BubbleBathOnEffe
- `mcreator.ScpoPlayerVarsSyncMixin` (mixins) → targets = "net.scpo.network.ScpoModVariables$PlayerVariables", remap = false
- `mcreator.ScpoMapVarsSyncMixin` (mixins) → targets = "net.scpo.network.ScpoModVariables$MapVariables", remap = false
- `mcreator.ScpoWorldVarsSyncMixin` (mixins) → targets = "net.scpo.network.ScpoModVariables$WorldVariables", remap = false
- `mcreator.MekMapVarsSyncMixin` (mixins) → targets = "net.mcreator.mekanismexplosives.network.MekanismexplosivesModVariables$MapVariables", remap = false
- `mcreator.MekWorldVarsSyncMixin` (mixins) → targets = "net.mcreator.mekanismexplosives.network.MekanismexplosivesModVariables$WorldVariables", remap = fal
- `mcreator.MekTimersMixin` (mixins) → targets = "net.mcreator.mekanismexplosives.procedures.TimersProcedure", remap = false
- `csaug.OrganDataMixin` (mixins) → targets = "net.corespring.csaugmentations.Capability.OrganCap$OrganData", remap = false
- `zerocontact.StaminaPacketAccess` (mixins) → targets = "net.zerocontact.network.NetworkHandler$SyncStaminaPacket", remap = false
- `zerocontact.StaminaSyncMixin` (mixins) → targets = "net.zerocontact.network.ModMessages", remap = false
- `vanilla.TeamRejoinMixin` (mixins) → Scoreboard.class
- `emotecraft.EmoteServerNetworkAccess` (mixins) → targets = "io.github.kosmx.emotes.forge.network.ServerNetwork", remap = false
- `saweapons.CompoundBowServerStateAccess` (mixins) → targets = "com.ogaba.sa_combat.combat.weapon.CompoundBowServerState", remap = false
- `saweapons.CompoundBowStateAccess` (mixins) → targets = "com.ogaba.sa_combat.combat.weapon.CompoundBowServerState$State", remap = false
- `saweapons.CompoundBowServerStateMixin` (mixins) → targets = "com.ogaba.sa_combat.combat.weapon.CompoundBowServerState", remap = false
- `ihs.SittableEntityMixin` (mixins) → SittableEntity.class
- `emotecraft.EmoteSendForPlayerMixin` (mixins) → targets = "io.github.kosmx.emotes.forge.network.ServerNetwork", remap = false
- `sbw.IffSyncMixin` (mixins) → targets = "com.atsuishio.superbwarfare.item.curio.IffItem$Companion", remap = false
- `securitycraft.CameraRotationSyncMixin` (mixins) → targets = "net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity", remap = false
- `butchery.MeatGrinderSyncMixin` (mixins) → targets = "net.mcreator.butchery.procedures.MeatgrinderrecipeprocedureProcedure", remap = false
- `clanginghowl.MovingSyncMixin` (mixins) → targets = "com.mongoose.clanginghowl.common.events.CHEvents", remap = false
- `clanginghowl.AirTicksSyncMixin` (mixins) → targets = "com.mongoose.clanginghowl.common.capabilities.CHCapHelper", remap = false
- `farmandcharm.SaturationSyncMixin` (mixins) → targets = "net.satisfy.farm_and_charm.core.network.PacketHandler", remap = false
- `carryon.LostPassengerMixin` (mixins) → PlacementHandler.class, remap = false
- `salocks.SaLockpickMixin` (mixins) → targets = "com.ogaba.sa_decor.common.loot.DecorLockpickManager", remap = false
- `cufflocks.CuffedLockpickMixin` (mixins) → CuffedAPI.Networking.class, remap = false
- `simpleradio.RadioTuneMixin` (mixins) → targets = "com.codinglitch.simpleradio.core.networking.SimpleRadioNetworking", remap = false
- `bendy.BendHelperMixin` (client) → BendHelper.class, remap = false
- `bendy.AnimationApplierBendMixin` (client) → AnimationApplier.class, remap = false
- `cuffed.PlayerModelCuffedMixin` (client) → PlayerModel.class
- `cuffed.AnimationApplierCuffedMixin` (client) → AnimationApplier.class, remap = false
- `securitycraft.FogRendererFrameFeedMixin` (client) → FogRenderer.class
- `customnpcs.SkinUtilMixin` (client) → targets = "noppes.npcs.client.SkinUtil", remap = false
- `figura.UIHelperInventoryScissorMixin` (client) → targets = "org.figuramc.figura.utils.ui.UIHelper", remap = false
- `anomaly.PlayerCanSeeAnomalyPostEffectMixin` (client) → targets = "net.mcreator.anomalyrephased.procedures.PlayerCanSeeAnomalyProcedure", remap = false
- `debuglimits.DebugRendererAccess` (client) → DebugRenderer.class
- `debuglimits.KeyboardDebugKeysMixin` (client) → KeyboardHandler.class
- `debuglimits.LocalPlayerPermissionMixin` (client) → LocalPlayer.class
- `debuglimits.SubtitlesOptionMixin` (client) → OptionInstance.class
- `debuglimits.DebugScreenTargetedMixin` (client) → DebugScreenOverlay.class
- `tips.TipRenderHandlerMixin` (client) → targets = "net.darkhax.tipsmod.impl.client.TipRenderHandler", remap = false
- `saflashlight.FlashlightBeamRangeAccess` (client) → FlashlightBeam.class, remap = false
- `saflashlight.FlashlightBeamOcclusionMixin` (client) → FlashlightBeam.class, remap = false
- `saflashlight.FlashlightShaderMixin` (client) → FlashlightShader.class, remap = false
- `glowsticks.GlowStickLightMixin` (client) → targets = "de.markusbordihn.glowsticks.block.glowstick.BlockStateManager", remap = false
- `glowsticks.GlowStickTrailLightMixin` (client) → targets = "de.markusbordihn.glowsticks.block.GlowStickBlockProperties", remap = false
- `glowsticks.GlowStickNoCollisionMixin` (client) → targets = "de.markusbordihn.glowsticks.block.GlowStickBlock"
- `glowsticks.CreativeGlowStickNoCollisionMixin` (client) → targets = "de.markusbordihn.glowsticks.block.CreativeGlowStickBlock"
- `agoodplace.AGoodPlaceLoginMixin` (client) → targets = "nl.enjarai.a_good_place.forge.AGoodPlaceImpl", remap = false
- `saweapons.PlayerRendererArmPoseMixin` (client) → PlayerRenderer.class
- `customnpcs.PlayerSoundMixin` (client) → targets = "noppes.npcs.client.ClientEventHandler", remap = false
- `emotecraft.ClientRepeatMixin` (client) → targets = "io.github.kosmx.emotes.main.network.ClientEmotePlay", remap = false

## 10. Зарегистрированные id

Нет или регистрируются не строковыми литералами.
