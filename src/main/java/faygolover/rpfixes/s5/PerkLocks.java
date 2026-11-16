package faygolover.rpfixes.s5;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Особенности RP Perks в чужих модах (решения Алекса 06.11, RP Perks 2.8.1):
 * <ul>
 *   <li>«Взлом» — только он вскрывает замки отмычкой: Survivor's Arsenal (ящики, двери, навесные замки) и Cuffed (замки,
 *       двери камер и сейфы, наручники). Вставки — {@code mixin.salocks}, {@code mixin.cufflocks};</li>
 *   <li>«Технофоб» — не перестраивает частоту рации Simple Radio ({@code mixin.simpleradio}), карта в считывателе
 *       Survivor's Arsenal проходит не с первого раза (здесь, событием).</li>
 * </ul>
 * Черты спрашиваем у RP Core по имени: без RP Perks их ни у кого нет — тогда и замки вскрыть некому, поэтому без Perks
 * заплатка не действует.
 */
public final class PerkLocks {
    private PerkLocks() {}

    private static final ResourceLocation HACKER = new ResourceLocation("rpcore", "hacker"),
            TECHNOPHOBE = new ResourceLocation("rpcore", "technophobe"),
            KEYCARD_READER = new ResourceLocation("sa_decor", "keycard_reader");

    private static Boolean perks;

    private static boolean on() {
        if (perks == null) perks = ModList.get().isLoaded("rpperks");
        return perks && FixesConfig.PERK_LOCKS.get();
    }

    /** Вскрывать замки нельзя (нет «Взлома»): сообщение и true. */
    public static boolean denyLockpick(Player p) {
        if (!on() || p.isCreative() || faygolover.rpcore.api.RpCoreAPI.hasTrait(p, HACKER)) return false;
        p.displayClientMessage(Component.literal("Вскрывать замки вы не умеете"), true);
        return true;
    }

    /** Перестраивать рацию нельзя (Технофоб): сообщение и true. */
    public static boolean denyRadioTune(ServerPlayer p) {
        if (!on() || !faygolover.rpcore.api.RpCoreAPI.hasTrait(p, TECHNOPHOBE)) return false;
        p.displayClientMessage(Component.literal("Вы не понимаете, как её перестроить"), true);
        return true;
    }

    /** Считыватель карт SA: у Технофоба карта часто не проходит. Только на сервере (случайность не совпала бы с клиентом). */
    public static void onUseBlock(PlayerInteractEvent.RightClickBlock e) {
        if (!(e.getEntity() instanceof ServerPlayer p) || !on()) return;
        if (!KEYCARD_READER.equals(ForgeRegistries.BLOCKS.getKey(e.getLevel().getBlockState(e.getPos()).getBlock()))) return;
        if (!faygolover.rpcore.api.RpCoreAPI.hasTrait(p, TECHNOPHOBE)) return;
        if (p.getRandom().nextDouble() >= FixesConfig.TECHNOPHOBE_KEYCARD_FAIL.get()) return;
        e.setCanceled(true);
        e.setCancellationResult(InteractionResult.FAIL);
        p.displayClientMessage(Component.literal("Карта не читается — может, не той стороной?"), true);
    }
}
