package com.palm3.createutils.content.blocks.temp_code;

import com.palm3.createutils.CUPackets;
import com.palm3.createutils.content.blocks.smarter_observer.SmarterObserverBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class SmarterObserverPacket /*extends BlockEntityConfigurationPacket<SmarterObserverBlockEntity> {
    public static final StreamCodec<ByteBuf, SmarterObserverPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.pos,
            ByteBufCodecs.INT, packet -> packet.accelerateToPacket,
            ByteBufCodecs.BOOL, packet -> packet.negativeDirectionPacket,
            ByteBufCodecs.INT, packet -> packet.increasedRpmPerTickPacket,
            ByteBufCodecs.INT, packet -> packet.increaseEveryPacket,
            ByteBufCodecs.BOOL, packet -> packet.showOnlyTicksPacket,
            AcceleratorMotorPacket::new
    );

    private final int accelerateToPacket;
    private final boolean negativeDirectionPacket;
    private final int increasedRpmPerTickPacket;
    private final int increaseEveryPacket;
    private final boolean showOnlyTicksPacket;

    public SmarterObserverPacket(BlockPos pos, int accelerateToPacket, boolean negativeDirPacket, int increasedRpmPerTickPacket, int increaseEveryPacket, boolean showOnlyTicksPacket) {
        super(pos);
        this.accelerateToPacket = accelerateToPacket;
        this.negativeDirectionPacket = negativeDirPacket;
        this.increasedRpmPerTickPacket = increasedRpmPerTickPacket;
        this.increaseEveryPacket = increaseEveryPacket;
        this.showOnlyTicksPacket = showOnlyTicksPacket;
    }

    @Override
    protected void applySettings(ServerPlayer player, SmarterObserverBlockEntity be) {
        be.accelerateTo = accelerateToPacket;
        be.negativeDirection = negativeDirectionPacket;
        be.increasedRpmPerTick = increasedRpmPerTickPacket;
        be.increaseEvery = increaseEveryPacket;
        be.guiShowsOnlyTicks = showOnlyTicksPacket;
        be.setChanged();
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CUPackets.SMARTER_OBSERVER_PACKET;
    }*/{
}

