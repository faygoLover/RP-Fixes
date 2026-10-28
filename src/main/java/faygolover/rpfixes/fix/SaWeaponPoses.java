package faygolover.rpfixes.fix;

import com.ogaba.sa_combat.combat.weapon.CompoundBowItem;
import com.ogaba.sa_combat.combat.weapon.NailGunItem;
import com.ogaba.sa_combat.combat.weapon.TacticalCrossbowItem;
import faygolover.rpfixes.FixesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * Survivor's Arsenal 1.0.0, клиент: поза рук с оружием мода у игроков в виде от 3-го лица.
 * <ul>
 *   <li>Тактический и самодельный арбалет ({@code TacticalCrossbowItem}), гвоздомёт ({@code NailGunItem}): у мода
 *       позы нет, их держали как обычный предмет. Теперь у всех (свой и чужие) — «оружие перед собой» двумя руками,
 *       как заряженный ванильный арбалет ({@code CROSSBOW_HOLD}), в любой руке; на время удара — как обычно.</li>
 *   <li>Блочный лук ({@code CompoundBowItem}): мод ставит «натягиваю» только своему игроку. Чужим — по пакету сервера
 *       ({@link SaBowSync}), пока натягивает или целится, и ещё полсекунды после (у мода столько длится выстрел).
 *       Свой — как делает мод.</li>
 * </ul>
 * Вызывается вставкой в {@code PlayerRenderer.getArmPose}.
 */
public final class SaWeaponPoses {
    private SaWeaponPoses() {}

    private static final long LINGER_TICKS = 10;
    /** id сущности → до какого тика мира держать позу лука ({@code Long.MAX_VALUE} — пока не придёт «отпустил»). */
    private static final Map<Integer, Long> BOW = new HashMap<>();

    public static void setBow(int entityId, boolean active) {
        var mc = Minecraft.getInstance();
        if (mc.level == null) return;
        if (active) BOW.put(entityId, Long.MAX_VALUE);
        else if (BOW.containsKey(entityId)) BOW.put(entityId, mc.level.getGameTime() + LINGER_TICKS);
        if (BOW.size() > 64) BOW.values().removeIf(t -> t < mc.level.getGameTime());
    }

    public static void reset() {
        BOW.clear();
    }

    /** Поза для руки или {@code null} — решает ванилла. */
    public static HumanoidModel.ArmPose pose(AbstractClientPlayer player, InteractionHand hand) {
        Item item = player.getItemInHand(hand).getItem();
        if (item instanceof TacticalCrossbowItem || item instanceof NailGunItem) {
            if (!player.swinging && FixesConfig.SA_CROSSBOW_POSE.get()) return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            return null;
        }
        if (item instanceof CompoundBowItem && hand == InteractionHand.MAIN_HAND && player != Minecraft.getInstance().player) {
            Long until = BOW.get(player.getId());
            if (until != null && until >= player.level().getGameTime() && FixesConfig.SA_BOW_POSE_SYNC.get())
                return HumanoidModel.ArmPose.BOW_AND_ARROW;
        }
        return null;
    }
}
