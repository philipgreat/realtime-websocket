package com.doublechiantech.service;

import com.doublechaintech.manage.Audience;
import com.doublechaintech.manage.ChannelManager;
import org.jboss.logging.Logger;

import  jakarta.enterprise.context.ApplicationScoped;
import  jakarta.inject.Inject;
import  jakarta.ws.rs.PUT;
import  jakarta.ws.rs.Path;
@ApplicationScoped
@Path("/message-center/channel")
public class ChannelService {
    private static final Logger LOG = Logger.getLogger(ChannelService.class);
    @Inject
    ChannelManager channelManager;

    @PUT
    public AddChanelResponse addToChanel(AddToChannelRequest request) {

        try{
            checkAddChannelRequest(request);
        }catch (IllegalArgumentException ex){
            LOG.error(ex.getMessage());
            return AddChanelResponse.withErrorMessage(ex.getMessage());
        }


        if(channelManager==null){
            String msg="the container is not working for ChannelService";
            LOG.error(msg);
            return AddChanelResponse.withErrorMessage(msg);
        }
        //ChannelManager channelManager = ChannelManager.inst();
        channelManager.listenToChannel(request.getChannelName(), Audience.withName(request.getEndpoint()));
        return AddChanelResponse.withMessage("End point '"+request.getChannelName()+"' has been added to channel '"+request.getEndpoint()+"'.");
    }

    private void checkAddChannelRequest(AddToChannelRequest request) {
        if(request==null){
            throw new IllegalArgumentException("request is null");
        }
        if(request.getChannelName()==null){
            throw new IllegalArgumentException("chanelName is null");
        }
        if(request.getChannelName().length()<1){
            throw new IllegalArgumentException("chanelName should not be an empty string");
        }
        if(request.getEndpoint()==null){
            throw new IllegalArgumentException("endpoint is null");
        }
        if(request.getEndpoint().length() < 1 ){
            throw new IllegalArgumentException("endpoint should not be an empty string");
        }

    }

}
