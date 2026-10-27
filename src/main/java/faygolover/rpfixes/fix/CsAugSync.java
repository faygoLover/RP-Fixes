package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/** [CS] Augmentations: пакет органов и модификаторы — только при изменении (см. mixin.csaug.OrganDataMixin). */
public final class CsAugSync {
    private CsAugSync() {}

    private static final Map<ServerPlayer, CompoundTag> SENT = new WeakHashMap<>();

    public static boolean organsChanged(ServerPlayer p, ItemStackHandler data) {
        if (!FixesConfig.CSAUG_ON_CHANGE.get()) return true;
        CompoundTag now = data.serializeNBT();
        synchronized (SENT) {
            if (now.equals(SENT.get(p))) return false;
            SENT.put(p, now);
            return true;
        }
    }

    /** Модификатор id = amount (сложение); тот же — ничего не трогаем (атрибут не «меняется», пакета нет). */
    public static void set(AttributeInstance inst, UUID id, String name, double amount, boolean permanent) {
        if (inst == null) return;
        AttributeModifier old = inst.getModifier(id);
        if (old != null && old.getAmount() == amount && old.getOperation() == AttributeModifier.Operation.ADDITION) return;
        if (old != null) inst.removeModifier(id);
        AttributeModifier m = new AttributeModifier(id, name, amount, AttributeModifier.Operation.ADDITION);
        if (permanent) inst.addPermanentModifier(m);
        else inst.addTransientModifier(m);
    }

    public static void remove(AttributeInstance inst, UUID id) {
        if (inst != null && inst.getModifier(id) != null) inst.removeModifier(id);
    }

    /** У клиента новая сущность игрока (новый id) — органы отправить заново. До обработчиков мода. */
    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) synchronized (SENT) { SENT.remove(p); }
    }
}
