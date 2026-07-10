package com.palm3.createutils.content.blocks.smarter_observer;

import com.palm3.createutils.register.CUPackets;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class SmarterObserverPacket extends BlockEntityConfigurationPacket<SmarterObserverBlockEntity> {
    public static final StreamCodec<ByteBuf, SmarterObserverPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> {
                BlockPos.STREAM_CODEC.encode(buf, packet.pos);
                ByteBufCodecs.STRING_UTF8.encode(buf, packet.targetProperty);
                ByteBufCodecs.STRING_UTF8.encode(buf, packet.targetValue);
                ByteBufCodecs.STRING_UTF8.encode(buf, packet.detectMode);
                ByteBufCodecs.INT.encode(buf, packet.onForTicks);
                ByteBufCodecs.STRING_UTF8.encode(buf, packet.blockRemovedDetectingPropsBh);
                ByteBufCodecs.BOOL.encode(buf, packet.showOnlyTicks);
            },
            buf -> new SmarterObserverPacket(
                    BlockPos.STREAM_CODEC.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf)
            )
    );

    private final String targetProperty;
    private final String targetValue;
    private final String detectMode;
    private final Integer onForTicks;
    private final String blockRemovedDetectingPropsBh;
    private final Boolean showOnlyTicks;

    public SmarterObserverPacket(BlockPos pos, String targetProperty, String targetValue, String detectMode, Integer onForTicks, String blockRemovedDetectingPropsBh, Boolean showOnlyTicks) {
        super(pos);
        this.targetProperty = targetProperty;
        this.targetValue = targetValue;
        this.detectMode = detectMode;
        this.onForTicks = onForTicks;
        this.blockRemovedDetectingPropsBh = blockRemovedDetectingPropsBh;
        this.showOnlyTicks = showOnlyTicks;
    }

    // Applies received settings on server
    @Override
    protected void applySettings(ServerPlayer player, SmarterObserverBlockEntity sobe) {
        sobe.targetProperty = targetProperty;
        sobe.targetValue = targetValue;
        sobe.detectMode = detectMode;
        sobe.onForTicks = onForTicks;
        sobe.blockRemovedDetectingPropsBh = blockRemovedDetectingPropsBh;
        sobe.showOnlyTicks = showOnlyTicks;
        sobe.setChanged();
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CUPackets.SMARTER_OBSERVER_PACKET;
    }
}