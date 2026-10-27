package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Моды MCreator (SCP: Overtime, Mekanism Explosives): переменные игрока (syncPlayerVariables) и мира (syncData — всем
 * игрокам сервера или измерения) процедуры шлют каждый тик, даже если ничего не поменялось. Здесь пакет уходит, только
 * если данные отличаются от последних отправленных. При входе, возрождении и смене измерения мод шлёт данные сам,
 * мимо этих методов; а для смены измерения (та же сущность игрока на сервере, новая у клиента) память сбрасывается
 * раньше обработчика мода.
 */
public final class McreatorSync {
    private McreatorSync() {}

    private static final Map<ServerPlayer, Tag> PLAYER = new WeakHashMap<>();
    private static final Map<Object, CompoundTag> SAVED = new WeakHashMap<>();

    /** Переменные игрока изменились с последней отправки (или её не было) — слать. */
    public static boolean playerChanged(ServerPlayer p, Tag data) {
        if (!FixesConfig.MCREATOR_SYNC_ON_CHANGE.get() || data == null) return true;
        synchronized (PLAYER) {
            if (data.equals(PLAYER.get(p))) return false;
            PLAYER.put(p, data.copy());
            return true;
        }
    }

    /** Переменные мира/измерения изменились с последней рассылки — слать. Иначе только пометка «сохранить». */
    public static boolean savedChanged(SavedData data) {
        if (!FixesConfig.MCREATOR_SYNC_ON_CHANGE.get()) return true;
        CompoundTag now = data.save(new CompoundTag());
        synchronized (SAVED) {
            if (now.equals(SAVED.get(data))) {
                data.setDirty();
                return false;
            }
            SAVED.put(data, now);
            return true;
        }
    }

    /** До обработчика мода (приоритет HIGHEST): у клиента новая сущность игрока — переменные отправятся заново. */
    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) synchronized (PLAYER) { PLAYER.remove(p); }
    }
}
