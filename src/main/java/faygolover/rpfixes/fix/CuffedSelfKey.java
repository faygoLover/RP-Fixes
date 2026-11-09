package faygolover.rpfixes.fix;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.RestrainableCapability;
import com.lazrproductions.cuffed.items.base.AbstractRestraintKeyItem;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import faygolover.rpfixes.FixesConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Cuffed: ключ работает только по другому игроку, поэтому скованный по ногам не мог снять путы сам, хотя руки свободны.
 * ПКМ подходящим ключом снимает путы со своих ног; связанные руки — нет, их снимает другой.
 */
public final class CuffedSelfKey {
    private CuffedSelfKey() {}

    public static void onRightClickItem(PlayerInteractEvent.RightClickItem e) {
        if (!(e.getEntity() instanceof ServerPlayer sp) || !FixesConfig.CUFFED_SELF_KEY.get()) return;
        if (!(e.getItemStack().getItem() instanceof AbstractRestraintKeyItem)) return;
        if (!(CuffedAPI.Capabilities.getRestrainableCapability(sp) instanceof RestrainableCapability cap)) return;
        if (cap.armsRestrained() || !cap.legsRestrained() || cap.getLegRestraint().getKeyItem() != e.getItemStack().getItem()) return;
        if (!cap.TryUnequipRestraint(sp, sp, RestraintType.Leg)) return;
        e.setCanceled(true);
        e.setCancellationResult(InteractionResult.SUCCESS);
    }
}
