package com.tianyalei.netty.nettyserver.netty;

import com.tianyalei.netty.nettyserver.core.Constant;
import com.tianyalei.netty.nettyserver.core.IClientChangeListener;
import com.tianyalei.netty.nettyserver.core.INettyMsgFilter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.List;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-07-24
 */
public class NodesServer {
    private IClientChangeListener clientChangeListener;
    private List<INettyMsgFilter> messageFilters;

    public void startNettyServer(int port) throws Exception {
        //boss单线程
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //出来网络io事件，如记录日志、对消息编解码等
                    .childHandler(new ChildChannelHandler());
            //绑定端口，同步等待成功
            ChannelFuture future = bootstrap.bind(port).sync();
            //等待服务器监听端口关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            //do nothing
            System.out.println("netty stop");
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * handler类
     */
    private class ChildChannelHandler extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel ch) {
            NodesServerHandler serverHandler = new NodesServerHandler();
            serverHandler.setClientEventListener(clientChangeListener);
            serverHandler.addMessageFilters(messageFilters);

            ByteBuf delimiter = Unpooled.copiedBuffer(Constant.DELIMITER.getBytes());
            ch.pipeline()
                    .addLast(new DelimiterBasedFrameDecoder(Constant.MAX_LENGTH, delimiter))
//                    .addLast(codec.newEncoder())
//                    .addLast(codec.newDecoder())
                    .addLast(new StringDecoder())
                    .addLast(serverHandler);
        }
    }

    public void setClientChangeListener(IClientChangeListener clientChangeListener) {
        this.clientChangeListener = clientChangeListener;
    }

    public void setMessageFilters(List<INettyMsgFilter> messageFilters) {
        this.messageFilters = messageFilters;
    }

}
