package com.superduckinvaders.game.net.packets;

import io.netty.buffer.ByteBuf;

/**
 * Created by Oliver on 20/02/2016.
 */
public interface OutboundPacket {

    int getOpcode();

    ByteBuf getPayload();
}
