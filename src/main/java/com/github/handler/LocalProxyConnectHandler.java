package com.github.handler;

import com.github.utils.ChannelUtil;
import com.github.utils.ConfigFileReader;
import com.github.utils.LogUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socks.SocksAuthResponse;
import io.netty.handler.codec.socks.SocksAuthScheme;
import io.netty.handler.codec.socks.SocksAuthStatus;
import io.netty.handler.codec.socks.SocksInitResponse;
import io.netty.util.concurrent.Promise;


@ChannelHandler.Sharable
public class LocalProxyConnectHandler extends SimpleChannelInboundHandler<Object> {


    private final Bootstrap bootstrap = new Bootstrap();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Promise<Channel> promise = ctx.executor().newPromise();
        promise.addListener(future -> {
            final Channel outboundChannel = (Channel) future.getNow();
            if (future.isSuccess()) {
                ctx.writeAndFlush(new SocksInitResponse(SocksAuthScheme.NO_AUTH))
                        .addListener(future1 -> {
                            ctx.pipeline().remove(LocalProxyConnectHandler.this);
                            //LOCAL_SERVER proxy out channel
                            outboundChannel.pipeline().addLast(new ForwardInHandler(ctx.channel()));
                            //proxy socks forward
                            ctx.pipeline().addLast(new ForwardOutHandler(outboundChannel));
                        });
            } else {
                ctx.channel().writeAndFlush(new SocksAuthResponse(SocksAuthStatus.FAILURE));
                ChannelUtil.closeOnFlush(ctx.channel());
            }
        });

        final Channel inboundChannel = ctx.channel();
        bootstrap.group(inboundChannel.eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new DirectClientHandler(promise));

        String host = ConfigFileReader.property("server");
        int port = Integer.parseInt(ConfigFileReader.property("server_port"));
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                LogUtil.info(String.format("connect remote server %s:%s success", host, port));
            } else {
                // Close the connection if the connection attempt has failed.
                ctx.channel().writeAndFlush(new SocksAuthResponse(SocksAuthStatus.FAILURE));
                ChannelUtil.closeOnFlush(ctx.channel());
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ChannelUtil.closeOnFlush(ctx.channel());
    }
}
