package sikander.example.com.tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.gard.myapplication.backend.registration.Registration;
import com.example.gard.myapplication.backend.registration.model.User;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.example.gard.myapplication.backend.registration.Registration;
import com.googlecode.objectify.cmd.Query;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class MainActivity extends Activity{

    Button signIn;
    Button signUp;

    Registration registration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signIn = (Button)findViewById(R.id.signin);
        signUp = (Button)findViewById(R.id.signup);
        onSignIn();
    }

    public void goToMap(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void onSignIn(){
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUser();
            }
        });
    }

    void getUser(){
        // dette er til cloud messaging
        Registration registration = getEndpoints();
        try {
            registration.register("Sikander");
        } catch(Exception e) {
            e.printStackTrace();
        }

        try{

            Collection users = registration.listDevices(2).values();
            System.out.println("Size: "+users.size());
            //System.out.println(users.toString());
            Iterator it = users.iterator();
            while (it.hasNext()){
                System.out.println(it.next().toString());
            }
            if(users.contains("Sikander")){
                System.out.println("HAHHAHA");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Registration getEndpoints(){
        Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver
                .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
        // end options for devappserver

        return builder.setApplicationName("Tracker").build();
    }

}