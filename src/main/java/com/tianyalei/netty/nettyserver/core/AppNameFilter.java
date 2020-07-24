package com.tianyalei.netty.nettyserver.core;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 客户端上报自己的appName
 *
 * @author wuweifeng wrote on 2019-12-11
 * @version 1.0
 */
@Component
@Order(2)
public class AppNameFilter implements INettyMsgFilter {
    @Resource
    private IClientChangeListener clientEventListener;

    @Override
    public boolean chain(HotKeyMsg message, ChannelHandlerContext ctx) {
        if (MessageType.APP_NAME == message.getMessageType()) {
            String appName = message.getBody();
            if (clientEventListener != null) {
                clientEventListener.newClient(appName, NettyIpUtil.clientIp(ctx), ctx);
            }
            return false;
        }

        return true;
    }

}
