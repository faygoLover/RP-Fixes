package faygolover.rpfixes.mixin;

import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/** Вставки в чужие моды применяются, только если этот мод есть в сборке (иначе Mixin ругался бы на отсутствующие цели). */
public final class RpFixesMixinPlugin implements IMixinConfigPlugin {
    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains(".worldedit.")) return has("worldedit");
        if (mixinClassName.contains(".bendy.")) return has("bendylib") && has("playeranimator");
        if (mixinClassName.contains(".cuffed.")) return has("cuffed") && has("playeranimator");
        if (mixinClassName.contains(".securitycraft.")) return has("securitycraft");
        if (mixinClassName.contains(".terralith.")) return has("terralith");
        return true;
    }

    private static boolean has(String modId) {
        LoadingModList list = LoadingModList.get();
        return list != null && list.getModFileById(modId) != null;
    }

    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
