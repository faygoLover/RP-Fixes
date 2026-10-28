package faygolover.rpfixes.mixin.saweapons;

import faygolover.rpfixes.fix.SaBowSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Survivor's Arsenal 1.0.0, сервер: после каждого изменения состояния блочного лука (начал / бросил натягивать,
 * прицелился / опустил, выстрелил, сменил предмет) — {@link SaBowSync} рассылает «натягивает / нет» видящим игрока.
 */
@Mixin(targets = "com.ogaba.sa_combat.combat.weapon.CompoundBowServerState", remap = false)
public abstract class CompoundBowServerStateMixin {
    @Inject(method = "beginCharge", at = @At("RETURN"))
    private static void rpfixes$begin(ServerPlayer player, CallbackInfo ci) {
        SaBowSync.update(player);
    }

    @Inject(method = "cancelCharge", at = @At("RETURN"))
    private static void rpfixes$cancel(ServerPlayer player, CallbackInfo ci) {
        SaBowSync.update(player);
    }

    @Inject(method = "setAiming", at = @At("RETURN"))
    private static void rpfixes$aim(ServerPlayer player, boolean aiming, CallbackInfo ci) {
        SaBowSync.update(player);
    }

    @Inject(method = "consumeShot", at = @At("RETURN"))
    private static void rpfixes$shot(ServerPlayer player, ItemStack stack, CallbackInfoReturnable<Object> cir) {
        SaBowSync.update(player);
    }

    @Inject(method = "invalidateChargeForStack", at = @At("RETURN"))
    private static void rpfixes$invalidate(ServerPlayer player, ItemStack stack, CallbackInfo ci) {
        SaBowSync.update(player);
    }
}
