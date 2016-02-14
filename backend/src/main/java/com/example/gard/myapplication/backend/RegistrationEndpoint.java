/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.example.gard.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Entity;

import java.util.List;
import java.util.logging.Logger;
import javax.inject.Named;

import static com.example.gard.myapplication.backend.OfyService.ofy;

// DETTE ER CLOUD MESSAGING RETARD
// DATASTORE ER ANNERLEDES
// (til Gard)

/**
 * A registration endpoint class we are exposing for a device's GCM registration id on the backend
 *
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 *
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(
  name = "registration",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "backend.myapplication.gard.example.com",
    ownerName = "backend.myapplication.gard.example.com",
    packagePath=""
  )
)
public class RegistrationEndpoint {

    private static final Logger log = Logger.getLogger(RegistrationEndpoint.class.getName());

    /**
     * Register a device to the backend
     *
     * @param regId The Google Cloud Messaging registration Id to add
     */
    @ApiMethod(name = "register")
    public void registerDevice(@Named("regId") String regId) {
        if(findUser(regId) != null) {
            log.info("Device " + regId + " already registered, skipping register");
            return;
        }
        User user = new User();
        user.setRegId(regId);
        //Entity entity = new Entity(regId);
        ofy().save().entity(user).now();
    }

    /**
     * Unregister a device from the backend
     *
     * @param regId The Google Cloud Messaging registration Id to remove
     */
    @ApiMethod(name = "unregister")
    public void unregisterDevice(@Named("regId") String regId) {
        User user = findUser(regId);
        if(user == null) {
            log.info("Device " + regId + " not registered, skipping unregister");
            return;
        }
        ofy().delete().entity(user).now();
    }

    /**
     * Return a collection of registered devices
     *
     * @param count The number of devices to list
     * @return a list of Google Cloud Messaging registration Ids
     */
    @ApiMethod(name = "listDevices")
    public CollectionResponse<User> listDevices(@Named("count") int count) {
        List<User> users = ofy().load().type(User.class).limit(count).list();
        return CollectionResponse.<User>builder().setItems(users).build();
    }

    private User findUser(String regId) {
        return ofy().load().type(User.class).filter("regId", regId).first().now();
    }
}
