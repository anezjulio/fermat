package com.bitdubai.fermat_pip_api.layer.pip_platform_service.event_manager.events;

import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUser;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.event_manager.enums.EventType;

import java.util.List;

/**
 * Created by Nerio on 08/10/15.
 */
public class AssetUserActorRequestListRegisteredNetworkServiceNotificationEvent extends AbstractFermatEvent {

    private List<ActorAssetUser> actorAssetUserList;

    public List<ActorAssetUser> getActorAssetUserList() {
        return actorAssetUserList;
    }

    public void setActorAssetUserList(List<ActorAssetUser> actorAssetUserList) {
        this.actorAssetUserList = actorAssetUserList;
    }

    public AssetUserActorRequestListRegisteredNetworkServiceNotificationEvent(EventType eventType) {
        super(eventType);
    }
}