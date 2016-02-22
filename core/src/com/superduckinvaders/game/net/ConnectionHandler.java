package com.superduckinvaders.game.net;

import com.superduckinvaders.game.Engine;
import com.superduckinvaders.game.net.packets.InboundPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by Oliver on 20/02/2016.
 */
@ChannelHandler.Sharable
public class ConnectionHandler extends SimpleChannelInboundHandler<InboundPacket> {

    private final Engine engine;

    public ConnectionHandler(Engine engine) {
        this.engine = engine;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InboundPacket msg) throws Exception {
        engine.enqueueInbound(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        engine.exceptionCaught(cause.getMessage());
        ctx.close();
    }
}
