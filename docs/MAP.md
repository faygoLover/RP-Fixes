# RP Fixes — карта

Версия на момент написания: 1.5.9 (16.11.2026). Факты по 107 классам и всем миксинам с их целями — `docs/INDEX.md`.

## 1. Что делает и чего не делает

Заплатки **чужих** модов сборки сервера «Департамент S5» и правила этого сервера: утечки памяти, лишний сетевой
трафик (отправка «только при изменении» вместо каждого тика), конфликты анимаций, поломки входа на сервер,
поведение инструментов ведущего у игроков, мелкие исправления вида. Каждая заплатка — миксин в чужой класс плюс
выключатель в конфиге. Мод знает о трёх десятках чужих модов и работает при любом их составе.

За этим — не сюда:
- **свои моды чинятся в самих модах**, не здесь;
- производительность и защита сервера в целом — `flperf`; сетевые лимиты и соединение — `flnettweaks`;
- меню-терминал S5 — `Menu Concepts/fm/gen.py`, не миксины;
- переводы чужих модов — S5 Translations.

## 2. Как течёт работа

**Одна заплатка устроена так:**
1. миксин в пакете `mixin/<имя чужого мода>/` — только перехват и вызов;
2. логика — класс в `fix/` (чтобы миксин оставался тонким и логику можно было читать отдельно);
3. выключатель — поле в `FixesConfig` (`config/rpfixes-common.toml`), по умолчанию как нужно серверу S5;
4. миксин применяется, **только если чужой мод стоит**: `mixin/RpFixesMixinPlugin.shouldApplyMixin` смотрит на
   имя пакета миксина и проверяет наличие мода (`has("id мода")`);
5. все миксины перечислены в `resources/rpfixes.mixins.json`.

**Запуск.** `RpFixes` — вход: регистрирует конфиг, подписки заплаток без миксинов (события Forge), свой канал
`FixesNet`. Заплатки от утечек — `fix/LeakFixes`.

**Группы заплаток** (подробно — по описанию класса в `docs/INDEX.md`):
- **трафик «только при изменении»:** `McreatorSync`, `ScpoTicks` (SCP: Overtime), `CraftedCoreSync`, `CsAugSync`,
  `SbwSync`, `ZeroContactSync`, `FarmCharmSync`, `WalkersCooldownSync`, `BlockSyncOnChange`;
- **анимации и эмоции:** `EmoteFixes`, `EmoteStale`, `EmoteConfigRetry`, `LazyBends`, `CuffedEmotes`, `HmiAutoExclude`;
- **вход на сервер и миры:** `AGoodPlaceTags`, `TerralithIntro`, `WorldEditLazyStates`, `CnpcScheduler`;
- **правила сервера S5:** `PaintingsGmOnly`, `EffortlessQuiet` (+ `EffortlessKeys`), `PlayerDebugLimits`, `PerkLocks`;
- **вид и интерфейс:** `LsoThermometerHud`, `FiguraInventoryScissor`, `CarryOnHand`, `AnomalyPostEffect`,
  фонарь Survivor's Arsenal (`mixin/saflashlight`, с FL Render — `compat/FlRenderCompat`), `SaWeaponPoses`.

## 3. Карта пакетов

| Пакет | За что отвечает | С чего начать читать |
|---|---|---|
| `rpfixes` | вход, конфиг, свой канал | `RpFixes`, `FixesConfig`, `FixesNet` |
| `fix` | логика всех заплаток (34 класса), по одному классу на заплатку | класс с именем чужого мода или явления |
| `compat` | мягкая связь с FL Render | `FlRenderCompat` |
| `mixin` | плагин: применять ли миксин | `RpFixesMixinPlugin` |
| `mixin.<мод>` | миксины, сгруппированные по чужому моду: `agoodplace`, `anomaly`, `bendy`, `breaching`, `butchery`, `carryon`, `clanginghowl`, `craftedcore`, `csaug`, `cuffed`, `cufflocks`, `customnpcs`, `debuglimits`, `effortless`, `emotecraft`, `farmandcharm`, `figura`, `glowsticks`, `ihs`, `mcreator`, `remorphed`, `saflashlight`, `salocks`, `saweapons`, `sbw`, `scpo`, `securitycraft`, `simpleradio`, `terralith`, `tips`, `vanilla`, `walkers`, `worldedit`, `zerocontact` | имя пакета = чужой мод |

## 4. Хочу добавить… — рецепты

| Что | Где | Образец | Не забыть |
|---|---|---|---|
| заплатку чужого мода | 1) миксин в `mixin/<мод>/` (новый мод — новый пакет); 2) логика в `fix/<Имя>`; 3) выключатель в `FixesConfig`; 4) строка в `rpfixes.mixins.json`; 5) **ветка в `RpFixesMixinPlugin`** для нового пакета; 6) jar чужого мода в `libs/` и `compileOnly` в `build.gradle` | `mixin/farmandcharm` + `fix/FarmCharmSync` | чужой класс — `remap = false`; по возможности `require = 0`, чтобы обновление чужого мода не роняло запуск; проверить на версии мода из сборки |
| заплатку без миксина (по событию Forge) | класс в `fix/` + подписка в `RpFixes` | `fix/PaintingsGmOnly` | выключатель в `FixesConfig` |
| правило сервера S5 | так же, как заплатку; план — отдельный пакет правил `s5` (`_family/refactor`, T8.6) | `fix/PlayerDebugLimits` | ключи конфига не менять |
| пакет клиенту или серверу | `FixesNet`, в конец | — | — |
| связь с модом семейства | мягко, вложенным классом при `isLoaded` | `compat/FlRenderCompat` | не отражением |

## 5. Данные

Мод ничего не хранит ни у игрока, ни в мире. Состояние заплаток («что уже отправлено игроку») — в памяти,
сбрасывается на выходе игрока.

## 6. Связи

- От модов семейства не зависит. FL Render — мягко (`compat/FlRenderCompat`). RP Perks — по чертам
  (`fix/PerkLocks`), без его классов.
- Чужие моды — три десятка, все необязательны; jar для компиляции — в `libs/` (в git не идут).

## 7. Конфиги

`config/rpfixes-common.toml` — `FixesConfig` (тип COMMON): выключатель на каждую заплатку и её числа. На живом
сервере действует файл сервера плюс слой S5 Packs; ключи не переименовывать.

## 8. Сеть

Свой канал `FixesNet` (с 1.5.4) — для заплаток, которым нужно сообщение между клиентом и сервером.

## 9. Ловушки

- **Забыл ветку в `RpFixesMixinPlugin` — заплатка применится без проверки наличия мода** и Mixin будет ругаться
  на отсутствующий класс у тех, у кого мода нет.
- **Миксин с `require = 0` отключается молча**, если чужой мод обновился и цель исчезла: после обновления сборки
  проверять по логу, что заплатки на месте (план общего отчёта совместимости — `_family/refactor`, T8.4).
- **Заплатки привязаны к версиям чужих модов** (в описании класса обычно указано, к какой). Обновление мода в
  сборке — повод перечитать его заплатку.
- **Меню-терминал S5 чинится не здесь** (`Menu Concepts/fm/gen.py`).
- Классы MCreator-модов правятся двумя путями: здесь (логика отправки) и в `flperf` (`MCreatorTransformer`) — не дублировать.

## 10. Тесты

Игровых тестов нет. `python ../_family/family.py verify "RP Fixes"` — компиляция. Проверка — запуск сборки с
нужными модами (стенды `Pack Audit`).

## 11. Документы в docs/

| Файл | Состояние |
|---|---|
| `MAP.md`, `INDEX.md` | действуют |
| `scpo_ticks_2026-10-26.md` | действует: разбор процедур SCP: Overtime и что именно заглушено |

Замеры трафика и находки по сборке — `Pack Audit/` в рабочей папке.
