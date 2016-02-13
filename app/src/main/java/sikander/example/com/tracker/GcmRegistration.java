package sikander.example.com.tracker;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.gard.myapplication.backend.registration.Registration;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class GcmRegistration extends AsyncTask<Void, Void, String> {
    private static Registration regService = null;
    private GoogleCloudMessaging gcm;
    private Context context;
    private GcmIntentService intentService;

    // TODO: change to your own sender ID to Google Developers Console project number, as per instructions above
    private static final String SENDER_ID = "174495869468";

    public GcmRegistration(Context context) {
        this.context = context;
    }

    @Override
    public void onPreExecute(){

    }

    @Override
    protected String doInBackground(Void... params) {
        if (regService == null) {
            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                    // otherwise they can be skipped
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end of optional local run code
            builder.setApplicationName("Tracker");
            regService = builder.build();
        }

        String msg = "";
        String regId = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            regId = gcm.register(SENDER_ID);
            msg = "Device registered, registration ID=" + regId;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            regService.register(regId).execute();

        } catch (IOException ex) {
            ex.printStackTrace();
            msg = "Error: " + ex.getMessage();
        }
        return regId;
    }

    @Override
    protected void onPostExecute(String msg) {
        SharedPreferences gcmPrefs = context.getSharedPreferences("GCM",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = gcmPrefs.edit();

        Registration registration = getEndpoints();
        try {
            registration.register(msg);
        } catch(Exception e){
            Log.d("Datastore registration", e.getMessage());
        }

        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Logger.getLogger("REGISTRATION").log(Level.INFO, msg);

        editor.putString("GCMId",msg);
        editor.apply();
    }

    // get endpoints to the backend
    static Registration getEndpoints(){
        Registration.Builder builder = new Registration.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), getRequestInitializer())
                .setRootUrl("http://10.0.0.2:8080/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
                        request.setDisableGZipContent(true);
                    }
                });

        return builder.build();
    }

    static HttpRequestInitializer getRequestInitializer(){
        return new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {

            }
        };
    }
}
