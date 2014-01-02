package vt.finder.main;

import java.util.ArrayList;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * clas is used to handle sending sms messages. Sends either a handshaking
 * authentication text or a schedule text
 * 
 * @author Ethan Gaebel (egaebel)
 * 
 */
public class SMSHandler {

    private static final String TAG = "SMS Handler";

    // ~Data Fields--------------------------------------------

    /**
     * Sent string
     */
    private static final String SENT = "SMS_SENT";

    /**
     * Delivered string
     */
    private static final String DELIVERED = "SMS_DELIVERED";

    /**
     * the context of the application/service that is using this SMSHandler
     */
    private Context context;

    // ~Constructors--------------------------------------------
    /**
     * connects the BroadcastReceiver to the context that is passed in
     * 
     * @param theContext
     *            the application/service context to connect the broadcast receiver to
     */
    public SMSHandler(Context theContext) {

        context = theContext;

        // sets the broadcast receiver to be used
        setBroadcastReceiver(context, SENT, DELIVERED);
    }

    // ~Methods--------------------------------------------
    /**
     * sends a "handshake" text message to a selected contact, that indicates a
     * request to share schedules
     * 
     * @param context
     *            the application's context
     * @param contact
     *            the contact that the text is going to
     * @param message
     *            the message to send to the contact, usually either, "Share?"
     *            or "Accepted"
     * 
     */
    public void sendHandshake(String contact, String message) {

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                new Intent(DELIVERED), 0);

        // actually send the text message
        SmsManager sms = SmsManager.getDefault();

        if (message.length() < 100) {

            sms.sendTextMessage(contact, null, "<ComparisonRequest>" + message
                    + "</ComparisonRequest>", sentPI, deliveredPI);
        }
        else {

            Log.i(TAG,
                    "there is a problem with the message passed to sendHandshake");
        }
    }

    /**
     * sends user's schedule via SMS to a passed in Contact
     * 
     * @param context
     *            the context with which the application is operating at
     * @param contact
     *            the name of the contact to SMS
     */
    public void sendSchedule(String contact, String message) {

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                new Intent(DELIVERED), 0);

        // actually send the text
        SmsManager sms = SmsManager.getDefault();

        sms.sendTextMessage(contact, null, "<ScheduleComparison>\n<Schedules>",
                sentPI, deliveredPI);

        ArrayList<String> messages = new ArrayList<String>();
        ArrayList<PendingIntent> sentPIS = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPIS = new ArrayList<PendingIntent>();

        // while the message is not empty, send the substring text
        while (message.length() != 0) {

            if (message.length() < 150) {

                messages.add(message.substring(0, message.length()));
                message = message.substring(message.length());

                sentPIS.add(sentPI);
                deliveredPIS.add(deliveredPI);
            }
            else {

                // to protect against "invalid" text messages such as
                // ">"
                if (message.length() == 151) {

                    messages.add(message.substring(0, 151));
                    message = message.substring(151);

                    sentPIS.add(sentPI);
                    deliveredPIS.add(deliveredPI);
                }
                // and "/>"
                else if (message.length() == 152) {

                    messages.add(message.substring(0, 152));
                    message = message.substring(152);

                    sentPIS.add(sentPI);
                    deliveredPIS.add(deliveredPI);
                }
                else {

                    messages.add(message.substring(0, 150));
                    message = message.substring(150);

                    sentPIS.add(sentPI);
                    deliveredPIS.add(deliveredPI);
                }
            }
        }

        Log.i(TAG, messages.toString());
        sms.sendMultipartTextMessage(contact, null, messages, sentPIS,
                deliveredPIS);

        sms.sendTextMessage(contact, null,
                "</Schedules>\n</ScheduleComparison>", sentPI, deliveredPI);
    }

    /**
     * sets up the broadcast receiver to handle sending the text messages
     * 
     * @param context
     *            the applications context
     */
    public void setBroadcastReceiver(final Context context, String SENT,
            String DELIVERED) {

        // ---when the SMS has been sent---
        context.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio off", Toast.LENGTH_SHORT)
                                .show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        // ---when the SMS has been delivered---
        context.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
    }
}