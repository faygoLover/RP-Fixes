package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.RpFixes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TickEvent;

import java.util.UUID;

/**
 * Диагностика (скин Ely.by пропадает через несколько секунд после входа): пишет в лог, когда меняется скин своего
 * персонажа или его запись в списке игроков, и все пакеты списка игроков про него — с тем, есть ли в профиле скин.
 * Включается {@code fixes.debug_skin}, первые 2 минуты после входа.
 */
public final class SkinDebug {
    private SkinDebug() {}

    private static String lastSkin = "";
    private static PlayerInfo lastInfo;
    private static int ticks;

    private static boolean on() {
        try {
            return FixesConfig.DEBUG_SKIN.get();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !on()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) {
            ticks = 0;
            lastSkin = "";
            lastInfo = null;
            return;
        }
        if (++ticks > 20 * 120) return;
        PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
        ResourceLocation skin = mc.player.getSkinTextureLocation();
        String now = String.valueOf(skin);
        if (info != lastInfo) {
            RpFixes.LOGGER.info("[skin] t={} запись в списке игроков: {} -> {}", ticks, id(lastInfo), id(info));
            lastInfo = info;
        }
        if (!now.equals(lastSkin)) {
            RpFixes.LOGGER.info("[skin] t={} скин: {} -> {}", ticks, lastSkin, now);
            lastSkin = now;
        }
    }

    public static void onUpdate(ClientboundPlayerInfoUpdatePacket p) {
        if (!on() || !Minecraft.getInstance().isSameThread() || Minecraft.getInstance().player == null) return;
        UUID me = Minecraft.getInstance().player.getUUID();
        for (var e : p.entries()) {
            if (!me.equals(e.profileId())) continue;
            String tex = e.profile() == null ? "без профиля"
                    : e.profile().getProperties().get("textures").isEmpty() ? "профиль БЕЗ скина" : "профиль со скином";
            RpFixes.LOGGER.info("[skin] t={} пакет: обновить себя {} ({})", ticks, p.actions(), tex);
        }
    }

    public static void onRemove(ClientboundPlayerInfoRemovePacket p) {
        if (!on() || !Minecraft.getInstance().isSameThread() || Minecraft.getInstance().player == null) return;
        if (p.profileIds().contains(Minecraft.getInstance().player.getUUID()))
            RpFixes.LOGGER.info("[skin] t={} пакет: УДАЛИТЬ себя из списка игроков", ticks, new Throwable("откуда"));
    }

    private static String id(PlayerInfo i) {
        return i == null ? "нет" : "#" + Integer.toHexString(System.identityHashCode(i));
    }
}
