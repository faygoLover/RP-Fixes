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
        // Immersive Paintings: картины — только ГМу.
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.PaintingsGmOnly::onJoin);
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.PaintingsGmOnly::onInteract);
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.PaintingsGmOnly::onInteractSpecific);
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.PaintingsGmOnly::onAttack);
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.PaintingsGmOnly::onUseOnBlock);
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.PaintingsGmOnly::onBreak);
        // Custom NPCs: его пул задач не держит процесс после остановки сервера.
        faygolover.rpfixes.fix.CnpcScheduler.init();
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.CnpcScheduler::onServerStopped);
        // Terralith: без рекламы в чате при первом запуске мира.
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.TerralithIntro::onServerStarted);
        // Клиент: эмоции (player-animation-lib) поверх Fresh Animations (EMF).
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist.isClient()) {
            var modBus = net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();
            modBus.addListener((net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent e) -> e.enqueueWork(faygolover.rpfixes.fix.EmoteFixes::init));
            // LSO: термометр — элемент общего HUD RP Core (/rphud). Класс грузится только при обоих модах.
            modBus.addListener((net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent e) -> {
                if (net.minecraftforge.fml.ModList.get().isLoaded("rpcore") && net.minecraftforge.fml.ModList.get().isLoaded("legendarysurvivaloverhaul"))
                    faygolover.rpfixes.fix.LsoThermometerHud.init();
            });
            // Hold My Items: исключить предметы со своим видом в руке — когда все моды загружены.
            modBus.addListener((net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent e) -> e.enqueueWork(faygolover.rpfixes.fix.HmiAutoExclude::init));
            MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.HmiAutoExclude::onClientTick);
            MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.SkinDebug::onClientTick);
            // Effortless Building: в сборке игрока без клавиш.
            MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.EffortlessKeys::onClientTick);
            // A Good Place: списки блоков — по приходу тегов, а не на входе (падал и обрывал вход для других модов).
            MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.AGoodPlaceTags::onTags);
        }
    }
}
