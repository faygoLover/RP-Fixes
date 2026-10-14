package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.RpFixes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Function;

/**
 * Эмоции Emotecraft и анимации RP Sports (player-animation-lib) с Fresh Animations (Entity Model Features): обе
 * стороны двигают модель игрока, и EMF перекладывает части поверх эмоции — персонаж садится «раком», заваливается
 * набок (находка 109, 14.10). Пока на игроке играет анимация player-animation-lib, просим EMF рисовать его обычной
 * моделью (или только остановить анимации EMF — {@code emf_emote_mode}). Всё через рефлексию: ни EMF, ни
 * player-animation-lib не нужны, без них заплатка молчит. Только клиент.
 */
public final class EmoteFixes {
    private EmoteFixes() {}

    /** IPlayer.getAnimationStack() и AnimationStack.isActive(). */
    private static MethodHandle stackOf, isActive;
    private static Class<?> iPlayer;

    public static void init() {
        String mode = FixesConfig.EMF_EMOTE_MODE.get();
        if (mode.equals("off") || !ModList.get().isLoaded("entity_model_features") || !ModList.get().isLoaded("playeranimator")) return;
        try {
            iPlayer = Class.forName("dev.kosmx.playerAnim.api.IPlayer");
            Class<?> stack = Class.forName("dev.kosmx.playerAnim.api.layered.AnimationStack");
            MethodHandles.Lookup l = MethodHandles.publicLookup();
            stackOf = l.findVirtual(iPlayer, "getAnimationStack", MethodType.methodType(stack));
            isActive = l.findVirtual(stack, "isActive", MethodType.methodType(boolean.class));
            Class<?> api = Class.forName("traben.entity_model_features.EMFAnimationApi");
            String method = mode.equals("pause") ? "registerPauseCondition" : "registerVanillaModelCondition";
            Function<Object, Boolean> cond = EmoteFixes::animating;
            api.getMethod(method, Function.class).invoke(null, cond);
            RpFixes.LOGGER.info("RP Fixes: эмоции и Fresh Animations — EMF.{} для игроков с анимацией player-animation-lib", method);
        } catch (Throwable t) {
            RpFixes.LOGGER.warn("RP Fixes: заплатка эмоций для EMF не встала: {}", t.toString());
        }
    }

    /** Игрок, на котором сейчас играет анимация player-animation-lib (эмоция, упражнение). */
    private static boolean animating(Object entity) {
        if (!(entity instanceof Player) || !iPlayer.isInstance(entity)) return false;
        try {
            Object stack = stackOf.invoke(entity);
            return stack != null && (boolean) isActive.invoke(stack);
        } catch (Throwable t) {
            return false;
        }
    }
}
