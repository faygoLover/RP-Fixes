package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.RpFixes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

import java.util.Locale;

/**
 * Диагностика (находка Sports 111, ноги «прилипли» к телу): пока на своём персонаже играет анимация
 * player-animation-lib, раз в ~2 с пишет в лог ноги и штанины — сразу после позы ({@code setupAnim}) и в момент
 * отрисовки. Разница между строками — значит, ноги после эмоции сбивает кто-то другой. Включается
 * {@code fixes.debug_emote_legs}, не больше 60 строк за запуск.
 */
public final class EmoteLegsDebug {
    private EmoteLegsDebug() {}

    private static PlayerModel<?> localModel;
    private static String afterSetup = "";
    private static long lastLog;
    private static int lines;

    private static boolean on() {
        try {
            return FixesConfig.DEBUG_EMOTE_LEGS.get() && lines < 60;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public static void afterSetupAnim(PlayerModel<?> model, LivingEntity entity) {
        if (!on() || entity != Minecraft.getInstance().player || !EmoteFixes.animating(entity)) return;
        localModel = model;
        afterSetup = parts(model);
    }

    public static void beforeRender(Object model) {
        if (!on() || model != localModel) return;
        long now = System.currentTimeMillis();
        if (now - lastLog < 2000) return;
        lastLog = now;
        lines += 2;
        PlayerModel<?> m = localModel;
        RpFixes.LOGGER.info("[legs] после позы:  {}", afterSetup);
        RpFixes.LOGGER.info("[legs] при отрисовке: {}  (класс ноги {}, тело {})", parts(m),
                m.leftLeg.getClass().getSimpleName(), part(m.body));
        localModel = null;
    }

    private static String parts(PlayerModel<?> m) {
        return "L " + part(m.leftLeg) + " R " + part(m.rightLeg) + " Lp " + part(m.leftPants) + " Rp " + part(m.rightPants);
    }

    private static String part(ModelPart p) {
        return String.format(Locale.ROOT, "[rot %.2f %.2f %.2f pos %.1f %.1f %.1f%s]", p.xRot, p.yRot, p.zRot, p.x, p.y, p.z,
                p.visible ? "" : " hidden");
    }
}
