package com.github.handler;

import com.github.socks.ServerType;
import com.github.utils.LogUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socks.*;


public class SocksRequestHandler extends SimpleChannelInboundHandler<SocksRequest> {
    private final ServerType serverType;

    public SocksRequestHandler(ServerType st) {
        this.serverType = st;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SocksRequest msg) throws Exception {
        SocksRequestType reqType = msg.requestType();
        switch (reqType) {
            case AUTH:
                ctx.writeAndFlush(new SocksAuthResponse(SocksAuthStatus.SUCCESS));
                break;
            case INIT:
                if (serverType == ServerType.LOCAL_SERVER) {
                    ctx.pipeline().addLast(new LocalProxyConnectHandler());
                    ctx.pipeline().remove(this);
                    ctx.fireChannelRead(msg);
                }
                break;
            case CMD:
                SocksCmdRequest req = (SocksCmdRequest) msg;
                if (req.cmdType() == SocksCmdType.CONNECT) {
                    ctx.pipeline().addLast(new RemoteProxyConnectHandler());
                    ctx.pipeline().remove(this);
                    ctx.fireChannelRead(req);
                } else {
                    ctx.channel().close();
                }
                break;
            case UNKNOWN:
                break;
            default:
                break;
        }
    }
}