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
 */
@Mod(RpFixes.MODID)
public class RpFixes {
    public static final String MODID = "rpfixes";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RpFixes() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FixesConfig.SPEC);
        MinecraftForge.EVENT_BUS.addListener(LeakFixes::onLogout);
        MinecraftForge.EVENT_BUS.addListener(LeakFixes::onServerTick);
    }
}
