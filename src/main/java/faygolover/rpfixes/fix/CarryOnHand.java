package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderHandEvent;
import tschipp.carryon.common.carry.CarryOnDataManager;
import tschipp.carryon.events.ClientEvents;

/**
 * Carry On, клиент: ношу от 1-го лица Carry On рисует в {@link RenderHandEvent} обычного приоритета, отменённое событие
 * он не получает. «Пустая рука» RP Core отменяет то же событие при пустой руке вне действия (а пока несёшь, рука пустая).
 * Кто из них раньше — зависит от порядка загрузки модов: если RP Core, ноша видна только на взмахе и пропадает из рук
 * (тест 03.11: сковородка). Здесь, раньше обоих, ношу рисует сам Carry On, его же кодом.
 */
public final class CarryOnHand {
    private CarryOnHand() {}

    public static void onRenderHand(RenderHandEvent e) {
        var p = Minecraft.getInstance().player;
        if (p == null || !FixesConfig.CARRYON_HAND.get() || !CarryOnDataManager.getCarryData(p).isCarrying()) return;
        ClientEvents.renderHand(e);
    }
}
