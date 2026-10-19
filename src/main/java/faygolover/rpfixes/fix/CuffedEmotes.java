package faygolover.rpfixes.fix;

import com.lazrproductions.cuffed.entity.animation.ArmRestraintAnimationFlags;
import com.lazrproductions.cuffed.entity.base.IDetainableEntity;
import com.lazrproductions.cuffed.entity.base.IRestrainableEntity;
import com.lazrproductions.cuffed.restraints.RestraintAPI;
import com.lazrproductions.cuffed.restraints.custom.PilloryRestraint;
import faygolover.rpfixes.FixesConfig;
import net.minecraft.world.entity.player.Player;

/**
 * Эмоции (player-animation-lib: Emotecraft, RP Sports, Better Combat) на связанном игроке (Cuffed): анимация играет, но
 * связанные части остаются как их держит Cuffed. Руки — если на руках путы с позой, ноги — если путы на ногах, голова —
 * в колодке; в колодке-блоке (pillory) эмоция не трогает ничего. Игрок, чью модель сейчас ставят, — в {@link #CURRENT}
 * (PlayerModel.setupAnim, только клиент, поток рендера).
 */
public final class CuffedEmotes {
    private CuffedEmotes() {}

    public static final ThreadLocal<Player> CURRENT = new ThreadLocal<>();

    /** true — не давать эмоции двигать эту часть (имена частей player-animation-lib). */
    public static boolean locked(String part) {
        Player p = CURRENT.get();
        if (p == null || !FixesConfig.CUFFED_EMOTE_LOCK.get()) return false;
        if (p instanceof IDetainableEntity d && d.getDetained() > -1) return true; // в колодке: Cuffed ставит позу целиком
        if (!(p instanceof IRestrainableEntity r) || !r.isRestrained()) return false;
        return switch (part) {
            case "leftArm", "rightArm" -> {
                ArmRestraintAnimationFlags f = !set(r.getArmRestraintId()) ? null : RestraintAPI.getArmAnimationFlagByKey(r.getArmRestraintId());
                yield f != null && f != ArmRestraintAnimationFlags.NONE;
            }
            case "leftLeg", "rightLeg" -> set(r.getLegRestraintId());
            case "head" -> PilloryRestraint.ID.equals(r.getHeadRestraintId());
            default -> false;
        };
    }

    /**
     * Путы надеты. Без пут Cuffed хранит пустую строку («minecraft:») или «minecraft:air» (так отдаёт его capability) —
     * это «нет». Из-за «air» ноги считались связанными у всех, и эмоции их не двигали (находка Sports 111).
     */
    private static boolean set(net.minecraft.resources.ResourceLocation id) {
        return id != null && !id.getPath().isEmpty() && !id.getPath().equals("air");
    }
}
