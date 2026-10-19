package faygolover.rpfixes.mixin.customnpcs;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.ScheduledExecutorService;

/** Пул задач Custom NPCs — для {@link faygolover.rpfixes.fix.CnpcScheduler}. */
@Pseudo
@Mixin(targets = "noppes.npcs.util.CustomNPCsScheduler", remap = false)
public interface CnpcSchedulerAccess {
    @Accessor("executor")
    static ScheduledExecutorService rpfixes$executor() {
        throw new AssertionError();
    }
}
