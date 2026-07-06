package com.palm3.createutils;

import com.palm3.createutils.content.blocks.smarter_observer.SmarterObserverPacket;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Locale;

import static com.palm3.createutils.CUMain.modRes;

public enum CUPackets implements BasePacketPayload.PacketTypeProvider {

    SMARTER_OBSERVER_PACKET(SmarterObserverPacket.class, SmarterObserverPacket.STREAM_CODEC);

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> CUPackets(Class<T> tClass, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(modRes(name)),
                tClass, codec
        );
    }

    @Override
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void register() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(CUMain.MOD_ID, CUMain.CREATE_VERSION);
        for (CUPackets packet : CUPackets.values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }
}
