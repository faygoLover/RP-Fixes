# SCP: Overtime 2.1 — процедуры каждого тика (просмотр 26.10)

Исправлено в 1.5.0 (`fixes.scpo_tick_patches`): ChaseTheme (N² /stopsound + пакет переменных каждый тик → только при конце погони;
сброс «loop» — одна задача вместо новой каждый тик), RemoveTeam178/RemoveNVG/RemoveMalO (команды → Java), MobTickSculpture
(`function scpo:tick` — только при живых SCP-173/023/650/096/689), ProcedureHunter (ищет только SCP-689).

Не исправлено, кандидаты (не обсуждались):
- **Spawn966Procedure**: игрок с ПНВ в инвентаре → каждый тик `MapVariables.syncData` = пакет SavedDataSyncMessage ВСЕМ игрокам. Чинить: слать только при смене spawncondition966 false→true.
- **HazmatUseProcedure**: в полном костюме каждый тик addEffect(…, 10 тиков) → пакет обновления эффекта игроку каждый тик.
- **PlayerWDBEffectTriggerProcedure**: при gamerule WHEN_DAY_BREAKS эффект на 20 тиков каждый тик → пакет эффекта каждый тик каждому.
- GiveAdvancementsItems / SCP1548AchievementGiver / Effected939 / CleanSecretion / Spawn966: проверки достижений каждый тик (CPU, без трафика).
- BreathingMask, EquipCan, EquipPizza, MyteryMeatCooldown: звуки раз в N тиков или случайно — мелочь.
- GasMask/Glasses3d/NVG Overlay — клиентские.
