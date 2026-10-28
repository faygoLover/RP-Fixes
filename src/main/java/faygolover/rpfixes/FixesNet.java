package faygolover.rpfixes;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Свой сетевой канал RP Fixes (с 1.5.4). Заходить можно и без RP Fixes на другой стороне: канал необязательный,
 * сервер шлёт только тем, у кого он есть.
 */
public final class FixesNet {
    private FixesNet() {}

    private static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(RpFixes.MODID, "main"))
            .networkProtocolVersion(() -> VERSION)
            .clientAcceptedVersions(NetworkRegistry.acceptMissingOr(VERSION))
            .serverAcceptedVersions(NetworkRegistry.acceptMissingOr(VERSION))
            .simpleChannel();

    public static void init() {
        CHANNEL.registerMessage(0, BowPose.class, BowPose::encode, BowPose::decode, BowPose::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static boolean has(ServerPlayer sp) {
        return sp.connection != null && CHANNEL.isRemotePresent(sp.connection.connection);
    }

    public static void send(ServerPlayer to, Object msg) {
        if (has(to)) CHANNEL.send(PacketDistributor.PLAYER.with(() -> to), msg);
    }

    /** Survivor's Arsenal: игрок {@code entityId} натягивает (целится из) блочный лук или отпустил. */
    public record BowPose(int entityId, boolean active) {
        static void encode(BowPose m, FriendlyByteBuf buf) {
            buf.writeVarInt(m.entityId);
            buf.writeBoolean(m.active);
        }

        static BowPose decode(FriendlyByteBuf buf) {
            return new BowPose(buf.readVarInt(), buf.readBoolean());
        }

        static void handle(BowPose m, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                if (FMLEnvironment.dist.isClient()) faygolover.rpfixes.fix.SaWeaponPoses.setBow(m.entityId, m.active);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
