package com.palm3.createutils;

import com.palm3.createutils.content.blocks.smarter_observer.SmarterObserverPacket;
import com.simibubi.create.Create;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Locale;

public enum CUPackets implements BasePacketPayload.PacketTypeProvider {
    // Client --> server
    SMARTER_OBSERVER_PACKET(SmarterObserverPacket.class, SmarterObserverPacket.STREAM_CODEC);

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> CUPackets(Class<T> tClass, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(Create.asResource(name)),
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
