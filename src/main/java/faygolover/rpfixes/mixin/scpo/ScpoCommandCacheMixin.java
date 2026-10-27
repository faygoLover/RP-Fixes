package faygolover.rpfixes.mixin.scpo;

import faygolover.rpfixes.fix.ScpoTicks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * SCP: Overtime: процедуры тика выполняют команды строкой (частицы, tp, team join, effect...) — разбор строки каждый тик
 * на каждую сущность. Разобранная команда запоминается и выполняется заново от нового источника (координаты «~» и
 * селекторы считаются при выполнении) — см. ScpoTicks.command.
 */
@Mixin(targets = {
        "net.scpo.procedures.BabySCP939OnEntityTickUpdateProcedure",
        "net.scpo.procedures.BubbleBathOnEffectActiveTickProcedure",
        "net.scpo.procedures.CloudAmensiaOnEntityTickUpdateProcedure",
        "net.scpo.procedures.ColaBottlesOnEffectActiveTickProcedure",
        "net.scpo.procedures.DeliveryChopperOnEntityTickUpdateProcedure",
        "net.scpo.procedures.FearOnEffectActiveTickProcedure",
        "net.scpo.procedures.MalOEffectActiveTickConditionProcedure",
        "net.scpo.procedures.MoneyprinterUpdateTickProcedure",
        "net.scpo.procedures.NVGHelmetTickEventProcedure",
        "net.scpo.procedures.SCP009UpdateTickProcedure",
        "net.scpo.procedures.SCP023OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP058OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP0662OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP1048AOnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP1048OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP131OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP143TornadoOnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP1781OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP178HelmetTickEventProcedure",
        "net.scpo.procedures.SCP280OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP409SpreadUpdateTickProcedure",
        "net.scpo.procedures.SCP457FlamethrowerWhileProjectileFlyingTickProcedure",
        "net.scpo.procedures.SCP457OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP457SmolOnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP6051OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP650OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP682OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP96AggroOnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP96OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SCP999OnEntityTickUpdateProcedure",
        "net.scpo.procedures.SenbonzakuraTornadoOnEntityTickUpdateProcedure",
        "net.scpo.procedures.SinkingOnEffectActiveTickProcedure",
        "net.scpo.procedures.SnowstormOnEntityTickUpdateProcedure",
        "net.scpo.procedures.StructureGenStairwellBlockAddedProcedure",
        "net.scpo.procedures.SweepTelekillWhileProjectileFlyingTickProcedure",
        "net.scpo.procedures.TheContactOnEntityTickUpdateProcedure",
        "net.scpo.procedures.TheoldmanOnEntityTickUpdateProcedure",
        "net.scpo.procedures.ThestatueOnEntityTickUpdateProcedure"
}, remap = false)
public abstract class ScpoCommandCacheMixin {
    @Redirect(method = "execute*", at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I"))
    private static int rpfixes$cached(Commands commands, CommandSourceStack source, String command) {
        return ScpoTicks.command(commands, source, command);
    }
}
