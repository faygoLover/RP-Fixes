package faygolover.rpfixes.mixin.carryon;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.common.carry.CarryOnData;
import tschipp.carryon.common.carry.CarryOnDataManager;
import tschipp.carryon.common.carry.PlacementHandler;

/**
 * Carry On 2.1.2.7, PlacementHandler.tryStackEntity: у несущего игрока тип «несу игрока», а пассажира уже нет (несомый
 * вышел, слез, телепортировался) — {@code getFirstPassenger()} null и NPE на каждом ПКМ по сущности (живой 01.11, 9 раз),
 * а несущий «несёт пустоту» и ничего не может поднять. Тогда ношу сбрасываем и клик не обрабатываем.
 */
@Mixin(value = PlacementHandler.class, remap = false)
public abstract class LostPassengerMixin {
    @Inject(method = "tryStackEntity", at = @At("HEAD"), cancellable = true)
    private static void rpfixes$lostPassenger(ServerPlayer player, Entity clicked, CallbackInfo ci) {
        if (!FixesConfig.CARRYON_LOST_PASSENGER.get()) return;
        CarryOnData carry = CarryOnDataManager.getCarryData(player);
        if (carry.isCarrying(CarryOnData.CarryType.PLAYER) && player.getFirstPassenger() == null) {
            carry.clear();
            CarryOnDataManager.setCarryData(player, carry);
            ci.cancel();
        }
    }
}
