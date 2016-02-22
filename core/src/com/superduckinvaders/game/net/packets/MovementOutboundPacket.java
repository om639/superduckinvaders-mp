package com.superduckinvaders.game.net.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.BitSet;

/**
 * Created by Oliver on 20/02/2016.
 */
public final class MovementOutboundPacket implements OutboundPacket {

    private final BitSet movement;

    public MovementOutboundPacket(BitSet movement) {
        this.movement = movement;
    }

    @Override
    public int getOpcode() {
        return 1;
    }

    @Override
    public ByteBuf getPayload() {
        return Unpooled.buffer(1)
                .writeBytes(movement.toByteArray());
    }
}
