package faygolover.rpfixes.mixin.saweapons;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Survivor's Arsenal: состояние блочного лука одного игрока на сервере. */
@Mixin(targets = "com.ogaba.sa_combat.combat.weapon.CompoundBowServerState$State", remap = false)
public interface CompoundBowStateAccess {
    @Accessor("aiming")
    boolean rpfixes$aiming();

    @Accessor("chargeStartTick")
    long rpfixes$chargeStart();
}
