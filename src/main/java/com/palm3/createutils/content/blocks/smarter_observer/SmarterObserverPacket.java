package com.palm3.createutils.content.blocks.smarter_observer;

import com.palm3.createutils.CUPackets;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class SmarterObserverPacket extends BlockEntityConfigurationPacket<SmarterObserverBlockEntity> {
    public static final StreamCodec<ByteBuf, SmarterObserverPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.pos,
            ByteBufCodecs.STRING_UTF8, packet -> packet.targetProperty,
            ByteBufCodecs.STRING_UTF8, packet -> packet.targetValue,
            SmarterObserverPacket::new
    );

    private final String targetProperty;
    private final String targetValue;

    public SmarterObserverPacket(BlockPos pos, String targetProperty, String targetValue) {
        super(pos);
        this.targetProperty = targetProperty;
        this.targetValue = targetValue;
    }

    // Applies received settings on server
    @Override
    protected void applySettings(ServerPlayer player, SmarterObserverBlockEntity sobe) {
        sobe.targetProperty = targetProperty;
        sobe.targetValue = targetValue;
        sobe.setChanged();
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CUPackets.SMARTER_OBSERVER_PACKET;
    }
}
