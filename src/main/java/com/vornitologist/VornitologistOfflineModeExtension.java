package com.vornitologist;

import java.util.Date;
import java.util.List;

import com.vaadin.addon.touchkit.extensions.OfflineMode;
import com.vornitologist.model.ObservationDB;
import com.vornitologist.model.ObservationPoint;
import com.vornitologist.model.Species;
import com.vornitologist.util.ClassificatiodDataReader;
import com.vornitologist.widgetset.client.Observation;
import com.vornitologist.widgetset.client.PersistOfflineObservationsServerRpc;

/**     
 * 
 * This is server side counter part for Vornitologists offline application. Here
 * we handle persisting observations stored during offline usage.
 * 
 */
public class VornitologistOfflineModeExtension extends OfflineMode {

    private PersistOfflineObservationsServerRpc serverRpc = new PersistOfflineObservationsServerRpc() {
        @Override
        public void persistObservations(List<Observation> observations) {

            for (Observation observation : observations) {
                com.vornitologist.model.Observation obs = new com.vornitologist.model.Observation();
                obs.setCount(observation.getCount());
                Species speciesById = ClassificatiodDataReader
                        .getSpeciesById(observation.getSpeciesId());
                obs.setSpecies(speciesById);

                // The demo offline mode example currently don't send location
                // data etc, we'll just fake them
                ObservationPoint location = new ObservationPoint();
                location.setName("Siberia (offline)");
                location.setLatitude(67.713);
                location.setLongitude(28.491);
                obs.setLocation(location);
                obs.setObserver(VornitologistUI.getApp().getUser());
                obs.setObservationTime(new Date());
                ObservationDB.persist(obs);
            }
        }
    };

    public VornitologistOfflineModeExtension() {
        registerRpc(serverRpc);
    }

}
