package com.vornitologist.widgetset.client;
import java.util.List;

import com.vaadin.shared.communication.ServerRpc;

public interface PersistOfflineObservationsServerRpc extends ServerRpc {

    public void persistObservations(List<Observation> observations);

}