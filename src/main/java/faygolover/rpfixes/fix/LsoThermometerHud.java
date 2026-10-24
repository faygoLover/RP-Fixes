package faygolover.rpfixes.fix;

import faygolover.rpcore.client.hud.Anchor;
import faygolover.rpcore.client.hud.HudElement;
import faygolover.rpcore.client.hud.HudRegistry;
import faygolover.rpfixes.FixesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import sfiomn.legendarysurvivaloverhaul.client.render.RenderTemperatureGui;
import sfiomn.legendarysurvivaloverhaul.common.integration.curios.CuriosUtil;
import sfiomn.legendarysurvivaloverhaul.config.Config;
import sfiomn.legendarysurvivaloverhaul.util.EnumUtil;

/**
 * Legendary Survival Overhaul, клиент: термометр (значок температуры над хотбаром или градусник с цифрами, если
 * термометр надет в Curios) — элемент общего HUD RP Core. В {@code /rphud} его двигают, масштабируют и скрывают;
 * свой слой LSO {@code temperature} при этом не рисуется, тот же рисунок выводится в месте элемента.
 * Сдвиги из конфига LSO не влияют — положение задаёт только раскладка RP Core.
 */
public final class LsoThermometerHud implements HudElement {
    private static final ResourceLocation OVERLAY = new ResourceLocation("legendarysurvivaloverhaul", "temperature");
    private static final ResourceLocation ICONS = new ResourceLocation("legendarysurvivaloverhaul", "textures/gui/overlay.png");
    private static final ResourceLocation ID = new ResourceLocation("rpfixes", "lso_thermometer");

    private LsoThermometerHud() {}

    /** Клиент, FMLClientSetupEvent: только при RP Core и LSO. */
    public static void init() {
        if (!ModList.get().isLoaded("rpcore") || !ModList.get().isLoaded("legendarysurvivaloverhaul")) return;
        if (!FixesConfig.LSO_THERMOMETER_HUD.get()) return;
        HudRegistry.register(new LsoThermometerHud());
        MinecraftForge.EVENT_BUS.addListener(LsoThermometerHud::onOverlay);
    }

    private static void onOverlay(RenderGuiOverlayEvent.Pre e) {
        if (OVERLAY.equals(e.getOverlay().id())) e.setCanceled(true);
    }

    private static boolean body() {
        return CuriosUtil.isThermometerEquipped;
    }

    @Override public ResourceLocation id() { return ID; }
    @Override public Component title() { return Component.literal("Термометр (LSO)"); }
    // Значок — 16×16; градусник — рамка с цифрами 23×13 и трубка 10×22 справа над ней.
    @Override public int width() { return body() ? 32 : 16; }
    @Override public int height() { return body() ? 23 : 16; }
    @Override public Anchor defaultAnchor() { return Anchor.BOTTOM_CENTER; }
    @Override public int defaultX() { return 0; }
    @Override public int defaultY() { return -42; }   // как в сборке: 52 от низа и сдвиг −6 из конфига LSO

    @Override
    public void render(GuiGraphics g, float partialTick, boolean preview) {
        Minecraft mc = Minecraft.getInstance();
        if (preview) {
            if (body()) {
                g.blit(ICONS, 0, 10, 0, 211, 23, 13);
                g.blit(ICONS, 22, 0, 22, 202, 10, 22);
            } else {
                g.blit(ICONS, 0, 0, 0, 48, 16, 16);
                g.blit(ICONS, 0, 0, 48, 48, 16, 16);
            }
            return;
        }
        if (!(mc.gui instanceof ForgeGui gui) || mc.player == null) return;
        if (!body() && Config.Baked.temperatureDisplayMode != EnumUtil.temperatureDisplayMode.SYMBOL) return;
        int sw = g.guiWidth(), sh = g.guiHeight();
        // Где LSO рисует сам (RenderTemperatureGui.drawTemperatureAsSymbol / drawBodyTemperature) → в (0,0) элемента.
        int x, y;
        if (body()) {
            x = sw / 2 - 124 + Config.Baked.bodyTemperatureDisplayOffsetX;
            y = sh - 14 + Config.Baked.bodyTemperatureDisplayOffsetY - 10;
            Player p = mc.player;
            if (!p.getOffhandItem().isEmpty() && p.getMainArm() == HumanoidArm.RIGHT
                    && Config.Baked.bodyTemperatureDisplayOffsetX == 0 && Config.Baked.bodyTemperatureDisplayOffsetY == 0) x -= 31;
        } else {
            x = sw / 2 - 8 + Config.Baked.temperatureDisplayOffsetX;
            y = sh - 52 + Config.Baked.temperatureDisplayOffsetY;
        }
        g.pose().pushPose();
        g.pose().translate(-x, -y, 0);
        RenderTemperatureGui.TEMPERATURE_GUI.render(gui, g, partialTick, sw, sh);
        g.pose().popPose();
    }
}
