package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.RpFixes;

import java.util.Map;
import java.util.UUID;

/**
 * Custom NPCs и скины игроков (Ely.by). При входе сервер CNPC спрашивает у клиента его скин, клиент берёт его из
 * профиля сессии лаунчера — у Ely.by там скина нет, и CNPC отвечает «стандартный» (Стив, Алекс…). Сервер запоминает
 * это (player_skins.dat) и рассылает назад, а клиент в {@code SkinUtil.resetSkin} очищает текстуры игрока и ставит
 * стандартный скин поверх настоящего. «Через раз» — гонка: успел скин Ely.by загрузиться до ответа или нет.
 * <p>
 * Здесь: если у игрока в CNPC только «свои» скины (ГМ их не менял — место скина совпадает с исходным), текстуры игрока
 * не трогаем. Скин, заданный ГМом через CNPC, по-прежнему применяется. Через рефлексию — CNPC для сборки не нужен.
 */
public final class CnpcPlayerSkins {
    private CnpcPlayerSkins() {}

    private static boolean warned;

    /** true — не давать CNPC подменять текстуры этого игрока. */
    public static boolean keepOwnSkin(UUID uuid) {
        if (uuid == null) return false;
        try {
            if (!FixesConfig.CNPC_KEEP_PLAYER_SKIN.get()) return false;
            Object controller = Class.forName("noppes.npcs.controllers.PlayerSkinController").getMethod("getInstance").invoke(null);
            Object data = controller.getClass().getMethod("get", UUID.class).invoke(controller, uuid);
            if (!(data instanceof Map<?, ?> skins) || skins.isEmpty()) return false;
            for (Object skin : skins.values()) {
                Object def = skin.getClass().getMethod("getDefault").invoke(skin);
                Object loc = skin.getClass().getMethod("getLocation").invoke(skin);
                if (def == null || !def.equals(loc)) return false; // ГМ задал свой скин — пусть CNPC применяет
            }
            RpFixes.LOGGER.info("RP Fixes: Custom NPCs не подменяет скин игрока {} стандартным", uuid);
            return true;
        } catch (Throwable t) {
            if (!warned) {
                warned = true;
                RpFixes.LOGGER.warn("RP Fixes: заплатка скинов Custom NPCs не сработала: {}", t.toString());
            }
            return false;
        }
    }
}
