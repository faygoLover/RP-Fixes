package faygolover.rpfixes.fix;

import com.ogaba.sa_combat.combat.weapon.CompoundBowItem;
import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.FixesNet;
import faygolover.rpfixes.RpFixes;
import faygolover.rpfixes.mixin.saweapons.CompoundBowServerStateAccess;
import faygolover.rpfixes.mixin.saweapons.CompoundBowStateAccess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Survivor's Arsenal 1.0.0, сервер: блочный лук ({@code CompoundBowItem}) ставит позу «натягиваю» только своему игроку
 * и по своему клиентскому состоянию — другие видят лук у ног. Сервер знает от клиента «натягивает / целится» (пакет
 * {@code CompoundBowStateC2SPacket} → {@code CompoundBowServerState}); здесь при каждом изменении рассылаем это видящим
 * игрока (маленький пакет RP Fixes), и тому, кто начал его видеть. Позу ставит {@link SaWeaponPoses}.
 */
public final class SaBowSync {
    private SaBowSync() {}

    private static final Set<UUID> ACTIVE = new HashSet<>();
    private static boolean broken;

    /** После любого изменения состояния лука у игрока (вызывает вставка в {@code CompoundBowServerState}). */
    public static void update(ServerPlayer sp) {
        if (sp == null || broken || !FixesConfig.SA_BOW_POSE_SYNC.get()) return;
        boolean active;
        try {
            Object st = CompoundBowServerStateAccess.rpfixes$states().get(sp.getUUID());
            active = st != null && (((CompoundBowStateAccess) st).rpfixes$aiming() || ((CompoundBowStateAccess) st).rpfixes$chargeStart() >= 0L);
        } catch (Throwable t) {
            broken = true;
            RpFixes.LOGGER.warn("RP Fixes: поза лука Survivor's Arsenal для других выключена: {}", t.toString());
            return;
        }
        if (active ? !ACTIVE.add(sp.getUUID()) : !ACTIVE.remove(sp.getUUID())) return;
        FixesNet.BowPose msg = new FixesNet.BowPose(sp.getId(), active);
        // Всем в этом мире, у кого есть канал: игроков мало, пакет в 3 байта; кто его не видит — пропустит.
        for (ServerPlayer other : sp.serverLevel().players())
            if (other != sp) FixesNet.send(other, msg);
    }

    /** Начал видеть игрока с блочным луком — его состояние сразу (и «отпустил»: старое могло остаться). */
    public static void onStartTracking(PlayerEvent.StartTracking e) {
        if (broken || !(e.getTarget() instanceof ServerPlayer target) || !(e.getEntity() instanceof ServerPlayer tracker)) return;
        if (!FixesConfig.SA_BOW_POSE_SYNC.get() || !(target.getMainHandItem().getItem() instanceof CompoundBowItem)) return;
        FixesNet.send(tracker, new FixesNet.BowPose(target.getId(), ACTIVE.contains(target.getUUID())));
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        ACTIVE.remove(e.getEntity().getUUID());
    }

    public static void onClone(PlayerEvent.Clone e) {
        ACTIVE.remove(e.getEntity().getUUID());
    }
}
