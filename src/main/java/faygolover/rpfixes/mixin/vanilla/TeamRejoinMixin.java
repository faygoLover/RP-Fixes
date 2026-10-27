package faygolover.rpfixes.mixin.vanilla;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Ванилла: «добавить в команду» того, кто уже в ней, выводит его из команды и добавляет снова — два пакета всем игрокам.
 * Моды (SCP: Overtime: шлемы SCP-178 и ПНВ, Mal-O, существа SCP-178) делают так каждый тик. Уже в этой команде —
 * ничего не делаем (итог тот же).
 */
@Mixin(Scoreboard.class)
public abstract class TeamRejoinMixin {
    @Shadow public abstract PlayerTeam getPlayersTeam(String name);

    @Inject(method = "addPlayerToTeam", at = @At("HEAD"), cancellable = true)
    private void rpfixes$alreadyThere(String name, PlayerTeam team, CallbackInfoReturnable<Boolean> cir) {
        if (team == null || getPlayersTeam(name) != team) return;
        boolean on;
        try { on = FixesConfig.TEAM_REJOIN_NOOP.get(); } catch (IllegalStateException notLoaded) { on = true; }
        if (on) cir.setReturnValue(false);
    }
}
