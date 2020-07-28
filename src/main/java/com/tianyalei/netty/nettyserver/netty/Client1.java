package com.tianyalei.netty.nettyserver.netty;

import com.tianyalei.netty.nettyserver.core.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-07-24
 */
public class Client1 {

    private Bootstrap initBootstrap() {
        //少线程
        EventLoopGroup group = new NioEventLoopGroup(2);

        Bootstrap bootstrap = new Bootstrap();
        NettyClientHandler nettyClientHandler = new NettyClientHandler();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ByteBuf delimiter = Unpooled.copiedBuffer(Constant.DELIMITER.getBytes());
                        ch.pipeline()
                                .addLast(new DelimiterBasedFrameDecoder(Constant.MAX_LENGTH, delimiter))
//                                .addLast(codec.newEncoder())
//                                .addLast(codec.newDecoder())
                                .addLast(new StringDecoder())
                                //10秒没消息时，就发心跳包过去
                                .addLast(new IdleStateHandler(0, 0, 30))
                                .addLast(nettyClientHandler);
                    }
                });
        return bootstrap;
    }

    public static void main(String[] args) {
        Bootstrap bootstrap = new Client1().initBootstrap();
        try {
//            ChannelFuture channelFuture = bootstrap.connect("open-worker1.jd.com", 2001).sync();
//            ChannelFuture channelFuture = bootstrap.connect("lipintao.com", 9000).sync();
            ChannelFuture channelFuture = bootstrap.connect("49.233.16.249", 9000).sync();
            Channel channel = channelFuture.channel();
            while (true) {
                Thread.sleep(1000);
                String hotMsg = FastJsonUtils.convertObjectToJSON(new HotKeyMsg(MessageType.REQUEST_NEW_KEY, "haaha"));
                channel.writeAndFlush(MsgBuilder.buildByteBuf(hotMsg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
