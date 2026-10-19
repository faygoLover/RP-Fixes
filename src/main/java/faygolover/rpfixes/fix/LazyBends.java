package faygolover.rpfixes.fix;

/** Часть тела, которую сейчас ставит эмоция (AnimationApplier.updatePart), — для направления сгиба в BendHelperMixin. */
public final class LazyBends {
    private LazyBends() {}

    public static final ThreadLocal<String> PART = new ThreadLocal<>();
}
