package faygolover.rpfixes;

import com.mojang.logging.LogUtils;
import faygolover.rpfixes.fix.LeakFixes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

/**
 * RP Fixes — заплатки чужих модов сборки (ревью 11.10, п. 8; раньше жили в RP Core). Ничего не требует: каждая
 * заплатка срабатывает, только если её мод установлен, и отключается в {@code config/rpfixes-common.toml}.
 * Утечки — на сервере, эмоции с Fresh Animations — на клиенте: ставить и туда, и туда.
 */
@Mod(RpFixes.MODID)
public class RpFixes {
    public static final String MODID = "rpfixes";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RpFixes() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FixesConfig.SPEC);
        MinecraftForge.EVENT_BUS.addListener(LeakFixes::onLogout);
        MinecraftForge.EVENT_BUS.addListener(LeakFixes::onServerTick);
        // Клиент: эмоции (player-animation-lib) поверх Fresh Animations (EMF).
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist.isClient())
            net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus().addListener(
                    (net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent e) -> e.enqueueWork(faygolover.rpfixes.fix.EmoteFixes::init));
    }
}
