package faygolover.rpfixes.mixin.inventoryfree;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * InventoryFree заворачивает слоты инвентаря игрока во всех меню в свой {@code BlockedSlot}. Меню, которые узнают свои
 * слоты по классу, после этого ломаются: альбом Exposure кладёт фото только из {@code AlbumPlayerInventorySlot}, а
 * получал {@code BlockedSlot} — фото просто бралось на курсор. Слоты из {@code fixes.inventoryfree_keep_slots} не
 * заворачиваем; закрытые ячейки InventoryFree всё равно освобождает сам каждый тик (InventoryEnforcer).
 */
@Pseudo
@Mixin(targets = "kirderf1.inventoryfree.SlotBlocker", remap = false)
public abstract class SlotBlockerMixin {
    @Inject(method = "shouldReplaceSlot(Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/world/entity/player/Inventory;)Z",
            at = @At("HEAD"), cancellable = true)
    private static void rpfixes$keepOwnSlots(Slot slot, Inventory inv, CallbackInfoReturnable<Boolean> cir) {
        if (FixesConfig.keepInventoryFreeSlot(slot)) cir.setReturnValue(false);
    }
}
