package com.github.handler;

import com.github.utils.ChannelUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socks.SocksCmdRequest;
import io.netty.handler.codec.socks.SocksCmdResponse;
import io.netty.handler.codec.socks.SocksCmdStatus;
import io.netty.handler.codec.socks.SocksMessageEncoder;
import io.netty.util.concurrent.Promise;


@ChannelHandler.Sharable
public final class RemoteProxyConnectHandler extends SimpleChannelInboundHandler<SocksCmdRequest> {

    private final Bootstrap bootstrap = new Bootstrap();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final SocksCmdRequest request) throws Exception {
        Promise<Channel> promise = ctx.executor().newPromise();
        promise.addListener(
                future -> {
                    final Channel outboundChannel = (Channel) future.getNow();
                    if (future.isSuccess()) {
                        ctx.channel().writeAndFlush(new SocksCmdResponse(SocksCmdStatus.SUCCESS,
                                request.addressType()))
                                .addListener(channelFuture -> {
                                    ctx.pipeline().remove(RemoteProxyConnectHandler.this);
                                    ctx.pipeline().remove(SocksMessageEncoder.class);
                                    //direct proxy out channel
                                    outboundChannel.pipeline().addLast(new ForwardInHandler(ctx.channel()));
                                    //proxy socks forward
                                    ctx.pipeline().addLast(new ForwardOutHandler(outboundChannel));
                                });
                    } else {
                        ctx.channel().writeAndFlush(new SocksCmdResponse(SocksCmdStatus.FAILURE, request.addressType()));
                        ChannelUtil.closeOnFlush(ctx.channel());
                    }
                });

        final Channel inboundChannel = ctx.channel();
        bootstrap.group(inboundChannel.eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new DirectClientHandler(promise));

        bootstrap.connect(request.host(), request.port()).addListener(future -> {
            if (future.isSuccess()) {
                // Connection established use handler provided results

            } else {
                // Close the connection if the connection attempt has failed.
                ctx.channel().writeAndFlush(
                        new SocksCmdResponse(SocksCmdStatus.FAILURE,
                                request.addressType()));
                ChannelUtil.closeOnFlush(ctx.channel());
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ChannelUtil.closeOnFlush(ctx.channel());
    }
}
