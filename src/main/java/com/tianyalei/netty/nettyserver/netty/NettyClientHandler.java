package com.tianyalei.netty.nettyserver.netty;

import com.tianyalei.netty.nettyserver.core.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author wuweifeng wrote on 2019-11-05.
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;

            if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                //向服务端发送消息
                ctx.writeAndFlush(MsgBuilder.buildByteBuf(new HotKeyMsg(MessageType.PING, Constant.PING)));
            }
        }

        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(MsgBuilder.buildByteBuf(new HotKeyMsg(MessageType.APP_NAME, "abc")));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("channelInactive");
        //断线了，可能只是client和server断了，但都和etcd没断。也可能是client自己断网了，也可能是server断了
        //发布断线事件。后续10秒后进行重连，根据etcd里的worker信息来决定是否重连，如果etcd里没了，就不重连。如果etcd里有，就重连
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String message) {
        HotKeyMsg msg = FastJsonUtils.toBean(message, HotKeyMsg.class);
        if (MessageType.PONG == msg.getMessageType()) {
            System.out.println("heart beat");
            return;
        }
        System.out.println( "receive new key : " + msg);

    }

}
