package com.tianyalei.netty.nettyserver.core;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 热key消息，包括从netty来的和mq来的。收到消息，都发到队列去
 *
 * @author wuweifeng wrote on 2019-12-11
 * @version 1.0
 */
@Component
@Order(3)
public class HotKeyFilter implements INettyMsgFilter {

    public static AtomicLong totalReceiveKeyCount = new AtomicLong();

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean chain(HotKeyMsg message, ChannelHandlerContext ctx) {
        if (MessageType.REQUEST_NEW_KEY == message.getMessageType()) {
            totalReceiveKeyCount.incrementAndGet();

            logger.info(message.toString());

            String hotMsg = FastJsonUtils.convertObjectToJSON(message);
            FlushUtil.flush(ctx, MsgBuilder.buildByteBuf(hotMsg));


            return false;
        }

        return true;
    }


}