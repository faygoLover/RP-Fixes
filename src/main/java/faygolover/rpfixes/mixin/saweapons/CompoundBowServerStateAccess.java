package faygolover.rpfixes.mixin.saweapons;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

/** Survivor's Arsenal: таблица состояний блочного лука по игрокам (значения — {@link CompoundBowStateAccess}). */
@Mixin(targets = "com.ogaba.sa_combat.combat.weapon.CompoundBowServerState", remap = false)
public interface CompoundBowServerStateAccess {
    @Accessor("STATES")
    static Map<UUID, Object> rpfixes$states() {
        throw new AssertionError();
    }
}
