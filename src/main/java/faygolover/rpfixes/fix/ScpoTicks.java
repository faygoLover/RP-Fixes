package faygolover.rpfixes.fix;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.scpo.ScpoMod;
import net.scpo.entity.SCP106Entity;
import net.scpo.entity.SCP689Entity;
import net.scpo.init.ScpoModItems;
import net.scpo.init.ScpoModMobEffects;
import net.scpo.network.ScpoModVariables;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * SCP: Overtime 2.1 (MCreator): процедуры, которые каждый тик выполняли команды или слали пакеты всем. Поведение
 * то же, но проверки — на Java, а отправка — только когда что-то меняется. Вызывается из миксинов mixin.scpo
 * (класс грузится только при установленном scpo).
 */
public final class ScpoTicks {
    private ScpoTicks() {}

    private static final ResourceLocation CHASE = new ResourceLocation("scpo", "scp_106_chase");

    // ---------- ChaseThemeProcedure ----------

    /** Игроки, у которых уже стоит в очереди сброс «loop» (мод ставил новый сброс каждый тик — тысячи задач). */
    private static final Set<Entity> LOOP_RESET = Collections.newSetFromMap(new WeakHashMap<>());

    /**
     * Музыка погони SCP-106. Было: каждый тик у каждого игрока без SCP-106 в 55 блоках — chasetheme=false, пакет
     * переменных игроку и команда {@code /stopsound @a record scpo:scp_106_chase} (N² пакетов в тик). Теперь сброс,
     * пакет и остановка звука у всех — только когда chasetheme был true.
     */
    public static void chase(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) return;
        ScpoModVariables.PlayerVariables vars = entity.getCapability(ScpoModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(null);
        Vec3 pos = new Vec3(x, y, z);
        if (vars != null && vars.chasetheme) {
            CompoundTag pd = entity.getPersistentData();
            if (!pd.getBoolean("loop") && !world.getEntitiesOfClass(SCP106Entity.class, AABB.ofSize(pos, 15, 15, 15), e -> true).isEmpty()) {
                pd.putBoolean("loop", true);
                if (world instanceof Level level) {
                    SoundEvent s = ForgeRegistries.SOUND_EVENTS.getValue(CHASE);
                    if (!level.isClientSide()) level.playSound(null, BlockPos.containing(x, y, z), s, SoundSource.RECORDS, 0.5F, 1.0F);
                    else level.playLocalSound(x, y, z, s, SoundSource.RECORDS, 0.5F, 1.0F, false);
                }
            }
            // как у мода: «loop» снимается через 4500 тиков (225 с) — но одной задачей, а не новой каждый тик
            if (pd.getBoolean("loop") && !world.isClientSide() && LOOP_RESET.add(entity)) {
                ScpoMod.queueServerWork(4500, () -> {
                    LOOP_RESET.remove(entity);
                    entity.getPersistentData().putBoolean("loop", false);
                });
            }
        }
        if (vars != null && vars.chasetheme && world.getEntitiesOfClass(SCP106Entity.class, AABB.ofSize(pos, 55, 55, 55), e -> true).isEmpty()) {
            vars.chasetheme = false;
            vars.syncPlayerVariables(entity);
            if (world instanceof ServerLevel level) level.getServer().getPlayerList().broadcastAll(new ClientboundStopSoundPacket(CHASE, SoundSource.RECORDS));
        }
    }

    // ---------- RemoveTeam178 / RemoveNVG / RemoveMalO ----------

    /**
     * Было: каждый тик у каждого игрока без шлема — команда {@code /execute as @a[team=<team>,distance=0..2] run team leave @p}.
     * {@code @p} от позиции самого игрока — это он сам: если рядом (до 2 блоков, и он сам тоже) есть кто-то из команды,
     * игрок выходит из своей команды. Здесь то же без разбора команды.
     */
    public static void leaveTeamWithoutHelmet(Entity entity, Item helmet, String team) {
        if (!(entity instanceof ServerPlayer self)) return;
        if (self.getItemBySlot(EquipmentSlot.HEAD).getItem() == helmet) return;
        if (self.getTeam() == null) return;   // team leave для игрока без команды ничего не делает
        Scoreboard sb = self.getScoreboard();
        PlayerTeam t = sb.getPlayerTeam(team);
        if (t == null) return;
        Vec3 pos = self.position();
        for (ServerPlayer p : self.serverLevel().players()) {
            if (p.getTeam() == t && p.distanceToSqr(pos) <= 4.0) {
                sb.removePlayerFromTeam(self.getScoreboardName());
                return;
            }
        }
    }

    public static void team178(Entity entity) {
        leaveTeamWithoutHelmet(entity, ScpoModItems.SCP_178_HELMET.get(), "178");
    }

    public static void teamNvg(Entity entity) {
        leaveTeamWithoutHelmet(entity, ScpoModItems.NVG_HELMET.get(), "966");
    }

    /** Было: каждый тик у каждого игрока без эффекта Mal-O — команда {@code /team leave @s[team=1471]}. */
    public static void teamMalO(Entity entity) {
        if (!(entity instanceof ServerPlayer self)) return;
        if (self.hasEffect(ScpoModMobEffects.MAL_O_EFFECT.get())) return;
        PlayerTeam t = self.getTeam() instanceof PlayerTeam pt ? pt : null;
        if (t != null && t.getName().equals("1471")) self.getScoreboard().removePlayerFromTeam(self.getScoreboardName());
    }

    // ---------- ProcedureHunterProcedure ----------

    /**
     * «Охотник во тьме» (SCP-689): мод каждый тик собирал ВСЕ сущности в 25 блоках, сортировал и искал среди них SCP-689.
     * Здесь сразу ищутся только SCP-689, в том же порядке (ближние первыми), с тем же действием.
     */
    public static void hunter(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) return;
        if (!entity.getPersistentData().getBoolean("hunterinthedark")) return;
        if (entity instanceof LivingEntity le && le.hasEffect(ScpoModMobEffects.HUNTER_IN_THE_DARK.get())) return;
        Vec3 c = new Vec3(x, y, z);
        var list = world.getEntitiesOfClass(SCP689Entity.class, new AABB(c, c).inflate(25.0), e -> true);
        if (list.isEmpty()) return;
        list.sort(Comparator.comparingDouble(e -> e.distanceToSqr(c)));
        for (SCP689Entity scp : list) {
            entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC)), 9999.0F);
            scp.teleportTo(x, y, z);
        }
    }

    // ---------- MobTickSculptureProcedure ----------

    /** Типы, которые ищет scpo:tick (data/scpo/functions/tick.mcfunction). */
    private static final ResourceLocation[] TICK_TYPES = {
            new ResourceLocation("scpo", "scp_173"), new ResourceLocation("scpo", "scp_023"), new ResourceLocation("scpo", "scp_650"),
            new ResourceLocation("scpo", "scp_96"), new ResourceLocation("scpo", "scp_96_aggro"), new ResourceLocation("scpo", "scp_689")};
    private static Set<EntityType<?>> tickTypes;
    /** Живые на сервере сущности этих типов. */
    private static final Set<Entity> TICK_MOBS = Collections.newSetFromMap(new WeakHashMap<>());
    private static int lastCheck;

    private static Set<EntityType<?>> tickTypes() {
        if (tickTypes == null) {
            Set<EntityType<?>> s = new java.util.HashSet<>();
            for (ResourceLocation id : TICK_TYPES) {
                EntityType<?> t = ForgeRegistries.ENTITY_TYPES.getValue(id);
                if (t != null && ForgeRegistries.ENTITY_TYPES.getKey(t).equals(id)) s.add(t);
            }
            tickTypes = s;
        }
        return tickTypes;
    }

    public static void onJoin(EntityJoinLevelEvent e) {
        if (!e.getLevel().isClientSide() && tickTypes().contains(e.getEntity().getType())) TICK_MOBS.add(e.getEntity());
    }

    public static void onLeave(EntityLeaveLevelEvent e) {
        if (!e.getLevel().isClientSide()) TICK_MOBS.remove(e.getEntity());
    }

    /**
     * Было: каждый тик каждого мира — команда {@code function scpo:tick}: шесть {@code @e[type=…]} по всем мирам (каждый —
     * проход по всем сущностям сервера). Функция что-то делает, только если такие SCP есть, — без них она не запускается.
     * Раз в 10 с список сверяется с мирами (на случай пропущенного события).
     */
    public static boolean sculptureTickNeeded(LevelAccessor world) {
        if (!(world instanceof ServerLevel level)) return false;
        MinecraftServer server = level.getServer();
        int now = server.getTickCount();
        if (now - lastCheck >= 200 || now < lastCheck) {
            lastCheck = now;
            TICK_MOBS.removeIf(Entity::isRemoved);
            Set<EntityType<?>> types = tickTypes();
            for (ServerLevel l : server.getAllLevels())
                for (Entity ent : l.getAllEntities()) if (types.contains(ent.getType())) TICK_MOBS.add(ent);
        }
        return !TICK_MOBS.isEmpty();
    }
}
