package faygolover.rpfixes.fix;

import com.mojang.blaze3d.platform.InputConstants;
import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.RpFixes;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModList;

/** Клиентская половина {@link EffortlessQuiet}: в сборке игрока снять клавиши Effortless Building. */
public final class EffortlessKeys {
    private EffortlessKeys() {}

    private static boolean keysDone;

    /** Клиент, первый тик: снять клавиши EB в сборке игрока. Игрок может назначить их снова — до следующего запуска. */
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (keysDone || e.phase != TickEvent.Phase.END) return;
        keysDone = true;
        if (!ModList.get().isLoaded("effortlessbuilding") || ModList.get().isLoaded("worldedit") || !FixesConfig.EFFORTLESS_QUIET.get()) return;
        try {
            Object arr = Class.forName("nl.requios.effortlessbuilding.ClientEvents").getField("keyBindings").get(null);
            if (!(arr instanceof KeyMapping[] keys)) return;
            boolean changed = false;
            for (KeyMapping k : keys) {
                if (k == null || k.isUnbound()) continue;
                k.setKey(InputConstants.UNKNOWN);
                changed = true;
            }
            if (changed) {
                KeyMapping.resetMapping();
                Minecraft.getInstance().options.save();
                RpFixes.LOGGER.info("RP Fixes: клавиши Effortless Building сняты (сборка игрока)");
            }
        } catch (Throwable t) {
            RpFixes.LOGGER.warn("RP Fixes: клавиши Effortless Building не снять: {}", t.toString());
        }
    }
}
