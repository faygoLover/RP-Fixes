package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;

/**
 * Anomaly Rephased (MCreator, PlayerCanSeeAnomalyProcedure): каждый тик игрока без своего эффекта «Тревога» снимает
 * постэффект экрана — любой, не только свой {@code phosphor}. Постэффект RP Medicine (боль, серость) тут же ставится
 * заново, и шейдеры пересобираются 20 раз в секунду — рывки. Снимаем только его собственный.
 */
public final class AnomalyPostEffect {
    private AnomalyPostEffect() {}

    private static final String OWN = "minecraft:shaders/post/phosphor.json";

    public static void shutdown(GameRenderer renderer) {
        if (!FixesConfig.ANOMALY_OWN_POST_EFFECT.get()) {
            renderer.shutdownEffect();
            return;
        }
        PostChain cur = renderer.currentEffect();
        if (cur != null && OWN.equals(cur.getName())) renderer.shutdownEffect();
    }
}
