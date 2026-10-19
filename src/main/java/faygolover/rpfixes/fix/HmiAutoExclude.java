package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.RpFixes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Hold My Items рисует руку и предмет в 1-м лице по-своему и спорит с модами, у которых свой вид предмета в руке
 * (оружие TaCZ/Superb Warfare/LesRaisins, предметы GeckoLib и т.п.). У HMI есть списки исключений
 * (excludedModIds/excludedItemIds в holdmyitems-client.toml) — дополняем их сами, только в памяти: файл не трогаем.
 * Исключаются предметы со своим отрисовщиком (getCustomRenderer), своей позой рук (getArmPose) или своей
 * трансформацией руки в 1-м лице (applyForgeHandTransform), и целиком моды из fixes.hmi_exclude_mods.
 * Forge кэширует значение конфига в ConfigValue.cachedValue и сбрасывает его при перезагрузке — тогда подкладываем снова.
 */
public final class HmiAutoExclude {
    private HmiAutoExclude() {}

    private static final String CONFIG = "de.bene2212.holdmyitems.config.HoldMyItemsClientConfig";
    private static Field cache;
    private static ForgeConfigSpec.ConfigValue<?> itemsValue, modsValue;
    private static List<String> autoItems;
    private static Object itemsInstalled, modsInstalled;

    /** После загрузки всех модов (клиент). */
    public static void init() {
        if (!FixesConfig.HMI_AUTO_EXCLUDE.get() || !ModList.get().isLoaded("holdmyitems")) return;
        try {
            Class<?> c = Class.forName(CONFIG);
            itemsValue = (ForgeConfigSpec.ConfigValue<?>) c.getField("ITEM_IDS_TO_EXCLUDE").get(null);
            modsValue = (ForgeConfigSpec.ConfigValue<?>) c.getField("MOD_IDS_TO_EXCLUDE").get(null);
            cache = ForgeConfigSpec.ConfigValue.class.getDeclaredField("cachedValue");
            cache.setAccessible(true);
        } catch (ReflectiveOperationException | RuntimeException e) {
            RpFixes.LOGGER.warn("RP Fixes: Hold My Items — заплатка исключений не встала: {}", e.toString());
            itemsValue = null;
            return;
        }
        autoItems = scan();
        RpFixes.LOGGER.info("RP Fixes: Hold My Items — авто-исключено предметов со своим рендером/позой: {} (пример: {}), модов: {}",
                autoItems.size(), autoItems.subList(0, Math.min(8, autoItems.size())), FixesConfig.HMI_EXCLUDE_MODS.get());
        apply();
    }

    /** Раз в секунду: если Forge сбросил кэш (перезагрузка конфига), подложить списки заново. */
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END && itemsValue != null && Minecraft.getInstance().level != null
                && Minecraft.getInstance().level.getGameTime() % 20 == 0) apply();
    }

    private static final String TAB = "\t", NL = "\n";
    private static final StringBuilder dump = new StringBuilder("# id" + TAB + "block|item" + TAB + "причина" + NL);

    private static List<String> scan() {
        List<String> out = new ArrayList<>();
        BlockEntityWithoutLevelRenderer vanilla = Minecraft.getInstance().getItemRenderer().getBlockEntityRenderer();
        for (Item item : ForgeRegistries.ITEMS) {
            try {
                IClientItemExtensions ext = IClientItemExtensions.of(item);
                if (ext == IClientItemExtensions.DEFAULT) continue;
                boolean custom = ext.getCustomRenderer() != vanilla
                        || overrides(ext, "getArmPose", LivingEntity.class, InteractionHand.class, ItemStack.class)
                        || overrides(ext, "applyForgeHandTransform", com.mojang.blaze3d.vertex.PoseStack.class,
                        net.minecraft.client.player.LocalPlayer.class, net.minecraft.world.entity.HumanoidArm.class, ItemStack.class,
                        float.class, float.class, float.class);
                if (custom) {
                    ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
                    if (id != null) {
                        out.add(id.toString());
                        String why = (ext.getCustomRenderer() != vanilla ? "renderer " : "")
                                + (overrides(ext, "getArmPose", LivingEntity.class, InteractionHand.class, ItemStack.class) ? "armpose " : "")
                                + (overrides(ext, "applyForgeHandTransform", com.mojang.blaze3d.vertex.PoseStack.class,
                                net.minecraft.client.player.LocalPlayer.class, net.minecraft.world.entity.HumanoidArm.class, ItemStack.class,
                                float.class, float.class, float.class) ? "handtransform" : "");
                        dump.append(id).append(TAB).append(item instanceof net.minecraft.world.item.BlockItem ? "block" : "item")
                                .append(TAB).append(why.trim()).append(NL);
                    }
                }
            } catch (Throwable t) {
                // клиентское расширение предмета упало при опросе — такой предмет не трогаем
            }
        }
        // список для сравнения конфигов HMI: logs/rpfixes-hmi-exclude.tsv
        try {
            java.nio.file.Files.writeString(net.minecraftforge.fml.loading.FMLPaths.GAMEDIR.get().resolve("logs/rpfixes-hmi-exclude.tsv"), dump);
        } catch (java.io.IOException ignored) {}
        return out;
    }

    private static boolean overrides(Object ext, String name, Class<?>... args) {
        try {
            return ext.getClass().getMethod(name, args).getDeclaringClass() != IClientItemExtensions.class;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static void apply() {
        try {
            if (cache.get(itemsValue) != itemsInstalled || itemsInstalled == null) itemsInstalled = install(itemsValue, autoItems);
            if (cache.get(modsValue) != modsInstalled || modsInstalled == null)
                modsInstalled = install(modsValue, new ArrayList<>(FixesConfig.HMI_EXCLUDE_MODS.get()));
        } catch (ReflectiveOperationException | RuntimeException e) {
            RpFixes.LOGGER.debug("RP Fixes: Hold My Items apply: {}", e.toString());
        }
    }

    /** Список из файла HMI + наш; кладём в кэш значения. */
    @SuppressWarnings("unchecked")
    private static Object install(ForgeConfigSpec.ConfigValue<?> value, List<String> extra) throws IllegalAccessException {
        cache.set(value, null);
        Set<String> merged = new LinkedHashSet<>((List<String>) value.get()); // заново из файла
        merged.addAll(extra);
        List<String> list = List.copyOf(merged);
        cache.set(value, list);
        return list;
    }
}
