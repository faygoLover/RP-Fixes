package faygolover.rpfixes.mixin.scpo;

import faygolover.rpfixes.fix.ScpoTicks;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * SCP: Overtime: процедуры тика (игрок, сущность, эффект, предмет в руке/на голове) каждый тик заново дают короткий
 * эффект — у игрока это пакет эффекта каждый тик (присед, костюм химзащиты, «Когда наступает день», ПНВ...). Эффект
 * продлевается, только когда от него осталось меньше половины (или он слабее нового) — см. ScpoTicks.refreshEffect.
 */
@Mixin(targets = {
        "net.scpo.procedures.AmnesiaOnEffectActiveTickProcedure",
        "net.scpo.procedures.Attack093UncleanProcedure",
        "net.scpo.procedures.CloudAmensiaOnEntityTickUpdateProcedure",
        "net.scpo.procedures.CryophobiaOnEffectActiveTickProcedure",
        "net.scpo.procedures.CrystallophobiaOnEffectActiveTickProcedure",
        "net.scpo.procedures.FemurbreakerOnEntityTickUpdateProcedure",
        "net.scpo.procedures.HazmatUseProcedure",
        "net.scpo.procedures.NVGHelmetTickEventProcedure",
        "net.scpo.procedures.OfficeChairOnEntityTickUpdateProcedure",
        "net.scpo.procedures.PlayerWDBEffectTriggerProcedure",
        "net.scpo.procedures.RedHandItemInHandTickProcedure",
        "net.scpo.procedures.SCP049OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP058OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP0662OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP1048AOnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP1048BOnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP1048COnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP105OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP250OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP3199OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP682OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP745OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP966OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP96OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP999OnEntityTickUpdateProcedure",
        "net.scpo.procedures.ScopophobiaOnEffectActiveTickProcedure",
        "net.scpo.procedures.SleepOnEffectActiveTickProcedure",
        "net.scpo.procedures.SneakingProcedureProcedure",
        "net.scpo.procedures.SnowstormOnEntityTickUpdateProcedure",
        "net.scpo.procedures.StoolOnEntityTickUpdateProcedure",
        "net.scpo.procedures.TheoldmanOnEntityTickUpdateProcedure"
}, remap = false)
public abstract class ScpoEffectRefreshMixin {
    @Redirect(method = "execute*", at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private static boolean rpfixes$refresh(LivingEntity target, MobEffectInstance effect) {
        return ScpoTicks.refreshEffect(target, effect);
    }
}
