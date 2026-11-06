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
        FixesNet.init();
        MinecraftForge.EVENT_BUS.addListener(LeakFixes::onLogout);
        MinecraftForge.EVENT_BUS.addListener(LeakFixes::onServerTick);
        // Immersive Paintings: картины — только ГМу.
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.PaintingsGmOnly::onJoin);
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.PaintingsGmOnly::onInteract);
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.PaintingsGmOnly::onInteractSpecific);
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.PaintingsGmOnly::onAttack);
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.PaintingsGmOnly::onUseOnBlock);
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.PaintingsGmOnly::onBreak);
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.PerkLocks::onUseBlock);
        // Custom NPCs: его пул задач не держит процесс после остановки сервера.
        faygolover.rpfixes.fix.CnpcScheduler.init();
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.CnpcScheduler::onServerStopped);
        // Terralith: без рекламы в чате при первом запуске мира.
        MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.TerralithIntro::onServerStarted);
        // SCP: Overtime / CraftedCore / Walkers: меньше пакетов и команд каждый тик (классы грузятся только при своём моде).
        var mods = net.minecraftforge.fml.ModList.get();
        // Emotecraft: эмоция с потерянным «стопом» не висит у других вечно.
        if (mods.isLoaded("emotecraft")) MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.EmoteStale::onServerTick);
        // Survivor's Arsenal: поза блочного лука для других — сервер рассылает «натягивает / нет».
        if (mods.isLoaded("sa_combat")) {
            MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.SaBowSync::onStartTracking);
            MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.SaBowSync::onLogout);
            MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.SaBowSync::onClone);
        }
        if (mods.isLoaded("scpo")) {
            MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.ScpoTicks::onJoin);
            MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.ScpoTicks::onLeave);
        }
        if (mods.isLoaded("craftedcore")) {
            MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.CraftedCoreSync::onLogin);
            MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.CraftedCoreSync::onLogout);
            MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.CraftedCoreSync::onRespawn);
            MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.CraftedCoreSync::onDimension);
            MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.CraftedCoreSync::onStartTracking);
        }
        if (mods.isLoaded("walkers")) MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.WalkersCooldownSync::onDimension);
        // MCreator (SCP:O, Mekanism Explosives), [CS] Augmentations: память «последнего отправленного» сбрасывается при смене
        // измерения раньше, чем мод сам шлёт данные (HIGHEST).
        if (mods.isLoaded("scpo") || mods.isLoaded("mekanismexplosives"))
            MinecraftForge.EVENT_BUS.addListener(net.minecraftforge.eventbus.api.EventPriority.HIGHEST, faygolover.rpfixes.fix.McreatorSync::onDimension);
        if (mods.isLoaded("csaugmentations"))
            MinecraftForge.EVENT_BUS.addListener(net.minecraftforge.eventbus.api.EventPriority.HIGHEST, faygolover.rpfixes.fix.CsAugSync::onDimension);
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
            // Effortless Building: в сборке игрока без клавиш.
            MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.EffortlessKeys::onClientTick);
            // A Good Place: списки блоков — по приходу тегов, а не на входе (падал и обрывал вход для других модов).
            MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.AGoodPlaceTags::onTags);
            // Emotecraft: настройки серверу ещё раз, если флаг «сервер ведёт эмоции» после входа не пришёл.
            if (mods.isLoaded("emotecraft")) {
                MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.EmoteConfigRetry::onLogin);
                MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.EmoteConfigRetry::onLogout);
                MinecraftForge.EVENT_BUS.addListener(faygolover.rpfixes.fix.EmoteConfigRetry::onClientTick);
            }
            // Carry On: ноша от 1-го лица — раньше «Пустой руки» RP Core, которая отменяет событие руки.
            if (mods.isLoaded("carryon"))
                MinecraftForge.EVENT_BUS.addListener(net.minecraftforge.eventbus.api.EventPriority.HIGH, false,
                        net.minecraftforge.client.event.RenderHandEvent.class, faygolover.rpfixes.fix.CarryOnHand::onRenderHand);
            // Survivor's Arsenal: поза лука других — по пакетам, сброс при выходе и смене мира.
            if (mods.isLoaded("sa_combat")) {
                MinecraftForge.EVENT_BUS.addListener((net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut e) -> faygolover.rpfixes.fix.SaWeaponPoses.reset());
                MinecraftForge.EVENT_BUS.addListener((net.minecraftforge.client.event.ClientPlayerNetworkEvent.Clone e) -> faygolover.rpfixes.fix.SaWeaponPoses.reset());
            }
        }
    }
}
