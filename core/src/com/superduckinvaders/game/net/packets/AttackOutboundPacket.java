package com.superduckinvaders.game.net.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by Oliver on 20/02/2016.
 */
public final class AttackOutboundPacket implements OutboundPacket {

    private final double targetX, targetY;

    public AttackOutboundPacket(double targetX, double targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    public int getOpcode() {
        return 2;
    }

    @Override
    public ByteBuf getPayload() {
        return Unpooled.buffer(8)
                .writeDouble(targetX)
                .writeDouble(targetY);
    }
}
