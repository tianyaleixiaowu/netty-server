package com.tianyalei.netty.nettyserver.netty;

import com.tianyalei.netty.nettyserver.core.IClientChangeListener;
import com.tianyalei.netty.nettyserver.core.INettyMsgFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author wuweifeng wrote on 2019-12-11
 * @version 1.0
 */
@Component
public class NodesServerStarter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IClientChangeListener iClientChangeListener;
    @Resource
    private List<INettyMsgFilter> messageFilters;

    @PostConstruct
    public void start() {
        logger.info("netty server is starting");

        NodesServer nodesServer = new NodesServer();
        nodesServer.setClientChangeListener(iClientChangeListener);
        nodesServer.setMessageFilters(messageFilters);
        try {
            nodesServer.startNettyServer(11111);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
