package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Immersive Paintings: картины и граффити — инструмент ГМа. У мода есть только право на загрузку своих картинок
 * ({@code uploadPermissionLevel}), а ставить, менять и ломать может любой. Здесь:
 * <ul>
 *   <li>ставить (предметы мода) и открывать редактор (ПКМ по картине) — только с правами ГМа ({@code paintings_gm_level});</li>
 *   <li>картины неуязвимы — сломать может только игрок в творческом режиме, взрывы и стрелы их не берут;</li>
 *   <li>блок, на котором висит картина, без прав ГМа не ломается (иначе картина отвалится).</li>
 * </ul>
 * Свой класс мода не нужен: картины узнаём по пространству имён {@code immersive_paintings}.
 */
public final class PaintingsGmOnly {
    private PaintingsGmOnly() {}

    private static final String NS = "immersive_paintings";
    private static final Component DENIED = Component.literal("Картины и граффити ставит и меняет только ГМ");

    private static Boolean loaded;

    private static boolean on() {
        if (loaded == null) loaded = ModList.get().isLoaded(NS);
        return loaded && FixesConfig.PAINTINGS_GM_ONLY.get();
    }

    private static boolean ours(ResourceLocation id) { return id != null && NS.equals(id.getNamespace()); }

    private static boolean isPainting(Entity e) { return ours(ForgeRegistries.ENTITY_TYPES.getKey(e.getType())); }

    private static boolean isPaintingItem(ItemStack s) { return !s.isEmpty() && ours(ForgeRegistries.ITEMS.getKey(s.getItem())); }

    private static boolean isGm(Player p) { return p.hasPermissions(FixesConfig.PAINTINGS_GM_LEVEL.get()); }

    private static void deny(Player p) {
        if (!p.level().isClientSide) p.displayClientMessage(DENIED, true);
    }

    /** Неуязвимость ставим при каждой загрузке: выключили заплатку — с картин она так же снимется. */
    public static void onJoin(EntityJoinLevelEvent e) {
        if (e.getLevel().isClientSide || !isPainting(e.getEntity())) return;
        if (loaded == null) loaded = ModList.get().isLoaded(NS);
        e.getEntity().setInvulnerable(loaded && FixesConfig.PAINTINGS_GM_ONLY.get());
    }

    public static void onInteract(PlayerInteractEvent.EntityInteract e) {
        if (on() && isPainting(e.getTarget()) && !isGm(e.getEntity())) {
            e.setCanceled(true);
            e.setCancellationResult(InteractionResult.FAIL);
            deny(e.getEntity());
        }
    }

    public static void onInteractSpecific(PlayerInteractEvent.EntityInteractSpecific e) {
        if (on() && isPainting(e.getTarget()) && !isGm(e.getEntity())) {
            e.setCanceled(true);
            e.setCancellationResult(InteractionResult.FAIL);
        }
    }

    /** Удар по картине без творческого режима всё равно ничего не сделает — гасим его сразу, без взмаха по серверу. */
    public static void onAttack(AttackEntityEvent e) {
        if (on() && isPainting(e.getTarget()) && !e.getEntity().isCreative()) e.setCanceled(true);
    }

    /** Предметы мода (картина, граффити) — ставить нельзя; двери, сундуки и прочее с ними в руке открываются как обычно. */
    public static void onUseOnBlock(PlayerInteractEvent.RightClickBlock e) {
        if (on() && isPaintingItem(e.getItemStack()) && !isGm(e.getEntity())) {
            e.setUseItem(Event.Result.DENY);
            deny(e.getEntity());
        }
    }

    public static void onBreak(BlockEvent.BreakEvent e) {
        Player p = e.getPlayer();
        if (p == null || !on() || isGm(p) || !(e.getLevel() instanceof net.minecraft.world.level.Level level)) return;
        AABB box = new AABB(e.getPos()).inflate(0.1);
        if (!level.getEntities((Entity) null, box, PaintingsGmOnly::isPainting).isEmpty()) {
            e.setCanceled(true);
            deny(p);
        }
    }
}
