package com.superduckinvaders.game;

import com.badlogic.gdx.graphics.Color;
import com.superduckinvaders.game.entity.Entity;
import com.superduckinvaders.game.net.ConnectionHandler;
import com.superduckinvaders.game.net.InboundPacketDecoder;
import com.superduckinvaders.game.net.OutboundPacketEncoder;
import com.superduckinvaders.game.net.packets.InboundPacket;
import com.superduckinvaders.game.net.packets.OutboundPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Engine {

    /**
     * The parent game instance.
     */
    private final SuperDuckInvaders parent;

    /**
     * The inetHost of the server to connect to.
     */
    private final String inetHost;

    /**
     * The inetPort of the server to connect to.
     */
    private final int inetPort;

    /**
     * The event loop for non-blocking networking.
     */
    private final EventLoopGroup workerGroup;

    /**
     * Connection channel to the server.
     */
    private Channel channel;

    /**
     * The queue of inbound packets.
     */
    private final Queue<InboundPacket> inbound = new ConcurrentLinkedQueue<>();

    /**
     * The queue of outbound packets.
     */
    private final Queue<OutboundPacket> outbound = new ConcurrentLinkedQueue<>();

    public Engine(SuperDuckInvaders parent, String inetHost, int inetPort) {
        this.parent = parent;
        this.inetHost = inetHost;
        this.inetPort = inetPort;

        workerGroup = new NioEventLoopGroup();
    }

    /**
     * Connect to the server.
     */
    public void connect() {
        Bootstrap b = new Bootstrap();
        b.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new InboundPacketDecoder());
                        pipeline.addLast(new OutboundPacketEncoder());

                        pipeline.addLast(new ConnectionHandler(Engine.this));
                    }
                });

        try {
            channel = b.connect(inetHost, inetPort).sync().channel();
            channel.closeFuture().addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } catch (Exception ex) {
            exceptionCaught(ex.getMessage());
        }
    }

    public void enqueueInbound(InboundPacket packet) {
        inbound.add(packet);
    }

    public void enqueueOutbound(OutboundPacket packet) {
        outbound.add(packet);
    }

    private void processInbound(InboundPacket packet) {
        ByteBuf payload = packet.getPayload();

        switch (packet.getType()) {
            case WAITING:
                processWaiting(payload);
                break;
            case START:
                processStart(payload);
                break;
            case CREATE:
                processCreate(payload);
                break;
            case DESTROY:
                processDestroy(payload);
                break;
            case POSITION:
                processPosition(payload);
                break;
            case DIRECTION:
                processDirection(payload);
                break;
            case TEXTURE:
                processTexture(payload);
                break;
            case INTERFACE:
                processInterface(payload);
                break;
        }
    }

    private void processWaiting(ByteBuf payload) {
        int players = payload.readByte();

        parent.getStartScreen().setMessage("Waiting for " + players + " more player" + (players == 1 ? "" : "s"));
    }

    private void processStart(ByteBuf payload) {
        int id = payload.readInt();

        System.out.println("hi im " + id);

        parent.getWorld().setPlayer(id);
        parent.showGameScreen();
    }

    private void processCreate(ByteBuf payload) {
        int id = payload.readInt();
        double x = payload.readDouble();
        double y = payload.readDouble();
        Entity.Direction direction = Entity.Direction.forOrdinal(payload.readByte());
        int texture = payload.readInt();

        parent.getWorld().createEntity(id, x, y, direction, texture);
    }

    private void processDestroy(ByteBuf payload) {
        int id = payload.readInt();

        parent.getWorld().destroyEntity(id);
    }

    private void processPosition(ByteBuf payload) {
        int id = payload.readInt();
        double x = payload.readDouble();
        double y = payload.readDouble();

        parent.getWorld().updateEntityPosition(id, x, y);
    }

    private void processDirection(ByteBuf payload) {
        int id = payload.readInt();
        Entity.Direction direction = Entity.Direction.forOrdinal(payload.readByte());

        parent.getWorld().updateEntityDirection(id, direction);
    }

    private void processTexture(ByteBuf payload) {
        int id = payload.readInt();
        int texture = payload.readInt();

        parent.getWorld().updateEntityTexture(id, texture);
    }

    private void processInterface(ByteBuf payload) {
        float health = payload.readFloat();
        float flightCharge = payload.readFloat();
        float[] powerupCharge = new float[5];

        for (int i = 0; i < powerupCharge.length; i++) {
            powerupCharge[i] = payload.readFloat();
        }

        parent.getGameScreen().setHealth(health);
        parent.getGameScreen().setFlightCharge(flightCharge);
        parent.getGameScreen().setPowerupCharge(powerupCharge);
    }

    public void exceptionCaught(String message) {
        workerGroup.shutdownGracefully();
        parent.getStartScreen().setMessage(message, Color.RED);
        parent.showStartScreen();
    }

    /**
     * Handle all currently queued inbound packets.
     */
    public void updateInbound() {
        InboundPacket packet;

        // Handle inbound packets.
        while ((packet = inbound.poll()) != null) {
            processInbound(packet);

            // Once the packet is processed release the payload to stop memory leaks.
            packet.release();
        }
    }

    /**
     * Write all currently queued outbound packets.
     */
    public void updateOutbound() {
        if (channel == null)
            return;

        OutboundPacket packet;

        // Handle outbound packets.
        while ((packet = outbound.poll()) != null) {
            channel.write(packet);
        }

        channel.flush();
    }
}
