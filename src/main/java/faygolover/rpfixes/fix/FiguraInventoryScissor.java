package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.client.gui.GuiGraphics;
import org.figuramc.figura.config.Configs;
import org.figuramc.figura.model.rendering.EntityRenderMode;

/**
 * Figura 0.1.5 рисует игрока в окне инвентаря сама и обрезает его рамкой {@code (x-15, y-39)…(x+17, y+4)} — это
 * окошко творческого инвентаря (там {@code size = 20}). В инвентаре выживания (и Curios) окошко другое —
 * {@code (x-25, y-67)…(x+24, y+3)} при {@code size = 30}, и рамка Figura срезала голову по уровню глаз.
 * <p>
 * Здесь, когда рисунок уже идёт под рамкой Figura, для {@code size = 30} она заменяется ванильным окошком; её
 * собственный {@code disableScissor} после рисунка снимает уже нашу рамку.
 */
public final class FiguraInventoryScissor {
    private FiguraInventoryScissor() {}

    public static void fix(float x, float y, float scale, GuiGraphics gui, EntityRenderMode mode) {
        if (mode != EntityRenderMode.MINECRAFT_GUI || scale != 30f) return;
        if (!FixesConfig.FIGURA_INVENTORY_SCISSOR.get()) return;
        if (!Boolean.TRUE.equals(Configs.INVENTORY_SCISSOR.value)) return;
        int ix = (int) x, iy = (int) y;
        gui.disableScissor();
        gui.enableScissor(ix - 25, iy - 67, ix + 24, iy + 3);
    }
}
