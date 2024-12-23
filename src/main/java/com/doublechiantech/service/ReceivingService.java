package com.doublechiantech.service;
import com.doublechaintech.manage.ChannelManager;
import com.doublechaintech.realtime.MessageCenterEndPoint;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import java.util.List;
@Path("/message-center/post")
@ApplicationScoped
public class ReceivingService {
    private static final Logger LOG = Logger.getLogger(ReceivingService.class);
    @Inject
    MessageCenterEndPoint messageCenterEndPoint;

    @Inject
    ChannelManager channelManager;
    @PUT
    public MessagePostResponse postMessage(MessagePostRequest request) {
        try{
            checkMessagePostRequest(request);
        }catch (IllegalArgumentException ex){
            LOG.error(ex.getMessage());
            return MessagePostResponse.withMessage(ex.getMessage());
        }
        if(messageCenterEndPoint==null){
            LOG.error("container is not working.."+ messageCenterEndPoint);
            return new MessagePostResponse();
        }

        if(!request.getSubscribers().isEmpty()){
            messageCenterEndPoint.multicast(request.getSubscribers(),request.getMessage());
            return  MessagePostResponse.withMessage("sent to end points: " +String.join(",", request.getSubscribers()));
        }

        if(channelManager.isDebugChannel(request.getChannelName())){
            messageCenterEndPoint.broadcast(request.getMessage());
            return  MessagePostResponse.withMessage("send to all!");
        }

        List<String> endPoints = channelManager.getEndPointForChannel(request.getChannelName());
        //LOG.error(channelManager.listSubscriptionText());
        messageCenterEndPoint.multicast(endPoints,request.getMessage());
        return  MessagePostResponse.withMessage("sent to end points:" +String.join(", ", endPoints));
    }


    private void checkMessagePostRequest(MessagePostRequest request) {

        if(request==null){
            throw new IllegalArgumentException("request is null");
        }
        if(request.getChannelName()==null){
            throw new IllegalArgumentException("chanelName is null");
        }
        if(request.getMessage()==null){
            throw new IllegalArgumentException("message is null");
        }

    }


}
