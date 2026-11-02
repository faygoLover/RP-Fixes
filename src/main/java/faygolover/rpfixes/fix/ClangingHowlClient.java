package faygolover.rpfixes.fix;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

/** Clanging Howl, клиент: «двигаюсь» серверу — только про себя, при смене и раз в 5 с на всякий случай. */
public final class ClangingHowlClient {
    private ClangingHowlClient() {}

    private static int lastId = Integer.MIN_VALUE;
    private static boolean lastMoving;

    public static boolean shouldSend(LivingEntity entity) {
        if (entity != Minecraft.getInstance().player) return false;
        boolean moving = entity.getDeltaMovement().horizontalDistanceSqr() > 2.5000003E-7F;
        if (entity.getId() == lastId && moving == lastMoving && entity.tickCount % 100 != 0) return false;
        lastId = entity.getId();
        lastMoving = moving;
        return true;
    }
}
