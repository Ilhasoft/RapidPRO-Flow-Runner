package br.com.ilhasoft.flowrunner_sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import br.com.ilhasoft.flowrunner.flow.FlowRunnerStarter;
import br.com.ilhasoft.flowrunner.fragments.FlowFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_key),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            FlowRunnerStarter starter = new FlowRunnerStarter(token, "10c3578c-77fa-405c-a9bb-3c6ce75a518d"
                    , "token ae3dd7849a97fbd57c1520aa77085e5b9514acdb");
            starter.loadFlows(new FlowRunnerStarter.FlowListener() {
                @Override
                public void onflowIsReady(FlowFragment flowFragment) {
                    Log.d(TAG, "onflowIsReady() called with: " + "flowFragment = [" + flowFragment + "]");
                }

                @Override
                public void onflowError(Exception e) {
                    Log.d(TAG, "onflowError() called with: " + "e = [" + e + "]");
                }
            });
        } catch(Exception exception) {
            Log.e(TAG, "onCreate: ", exception);
        }
    }
}
