package vt.finder.activities;

import vt.finder.main.SMSHandler;
import vt.finder.main.VTFinderService;
import vt.finder.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class VTHandshakeActivity extends Activity {

    // ~Constants--------------------------------------
    /**
     * tag for this class
     */
    private static final String TAG = "VTHandshakeActivity";

    /**
     * the second text message in the handshake process, confirms a shedule
     * comparison will take place
     */
    private static final String ACCEPT = "Accept";

    // ~Data Fields--------------------------------------------
    /**
     * the text view that is displayed with the friends name
     */
    private TextView dialog;

    /**
     * boolean value, tells whether this activity is bound to the service or not
     */
    private boolean isBound;

    /**
     * the name of the contact schedule comparison is requested from
     */
    private String name;

    /**
     * the number of the contact schedule comparision is requested from
     */
    private String number;
    
    /**
     * SMSHandler reference to the SMSHandeler in the service
     */
    private SMSHandler handle;

    // ~Constructors--------------------------------------------
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.handshake_dialog);

        dialog = (TextView) findViewById(R.id.handshakeResponseDialog);
        
        dialog.setBackgroundColor(this.getResources().getColor(R.color.maroon));
        
        Bundle received = this.getIntent().getExtras();
        name = received.getString("vt.orientation.friendsName");
        number = received.getString("vt.orientation.friendsNumber");

        dialog.setText(name
                + " would like to share their schedule with you (and vice versa). Acceptance will send your schedule to "
                + name
                + " via several text messages. Normal texting rates apply.");

        // bind this activity to the service, which should already be running
        doBindService();
    }

    // ~Methods--------------------------------------------------------------
    /**
     * called when the accept button is clicked, sends a text message of
     * Acceptance in response to the handshake via the orientation service's
     * SMSHandler.
     * 
     * then unbinds the service, and closes this activity down
     * 
     * @param view
     */
    public void acceptClicked(View view) {

        handle.sendHandshake(number, ACCEPT);

        orientationService.setComparison(true);
        
        doUnbindService();
        this.finish();        
    }

    /**
     * closes this activity, unbinds the service
     * 
     * @param view
     */
    public void denyClicked(View view) {

        orientationService.setComparison(false);
        
        doUnbindService();
        this.finish();
    }

    // ~Service
    // Stuff------------------------------------------------------------//
    // ~-------------------------------------------------------------------------//
    /**
     * the service that runs continuously checking for text messages
     */
    private VTFinderService orientationService;

    /**
     * service connection to the VTOrientationService
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {

            // sets value to orientationService
            orientationService = ((VTFinderService.myBinder) service).getService();
            
            handle = orientationService.getHandle();
        }

        public void onServiceDisconnected(ComponentName name) {

            orientationService = null;
        }
    };

    void doBindService() {

        // creates an intent for the service
        Intent service = new Intent(this, VTFinderService.class);

        // Establish a connection with the service. We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).//
        isBound = this.bindService(service, serviceConnection,
                Context.BIND_AUTO_CREATE);
    }

    void doUnbindService() {

        if (isBound) {

            unbindService(serviceConnection);
            isBound = false;
        }
    }
}