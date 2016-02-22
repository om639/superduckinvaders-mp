package com.superduckinvaders.game.net.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by Oliver on 20/02/2016.
 */
public final class FlyOutboundPacket implements OutboundPacket {

    @Override
    public int getOpcode() {
        return 3;
    }

    @Override
    public ByteBuf getPayload() {
        return Unpooled.buffer(0);
    }
}
