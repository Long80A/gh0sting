package com.github.socks;

import com.github.handler.SocksRequestHandler;
import com.github.utils.LogUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.socks.SocksCmdRequestDecoder;
import io.netty.handler.codec.socks.SocksInitRequestDecoder;
import io.netty.handler.codec.socks.SocksMessageEncoder;
import sun.rmi.runtime.Log;


public class SocksServer {

    private final int port;
    private final ServerType serverType;

    /**
     *
     * @param port 监听端口
     * @param serverType 服务类型
     */
    public SocksServer(String port, ServerType serverType) {
        this.port = Integer.parseInt(port);
        this.serverType = serverType;

    }

    public void start() throws Exception {
        LogUtil.info(String.format("start server, port %s ....",this.port));

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
//            b.handler(new LoggingHandler(LoggerFactory.class,LogLevel.DEBUG));
            b.childHandler(new ChannelInitializer() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    if (SocksServer.this.serverType == ServerType.LOCAL_SERVER) {
                        pipeline.addLast(new SocksInitRequestDecoder());
                    } else {
                        pipeline.addLast(new SocksCmdRequestDecoder());
                    }
                    pipeline.addLast(new SocksMessageEncoder());
                    pipeline.addLast(new SocksRequestHandler(SocksServer.this.serverType));
                }
            });
            b.bind(port).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
