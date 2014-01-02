package vt.finder.main;

import java.io.File;
import vt.finder.activities.VTComparisonActivity;
import vt.finder.activities.VTHandshakeActivity;
import vt.finder.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.RemoteViews;


public class VTFinderService extends Service {

    private static final String TAG = "ORIENTATION SERVICE";

    // ~Data Fields--------------------------------------------

    /**
     * constant to specify the type of intent to be accepted to the broadcast
     * receiver via an intent filter with this thing in it
     */
    public final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    /**
     * identifier for location of Schedule Notification
     */
    private int SCHEDULE_NOTIFICATION = 37;

    /**
     * identifier for location of Handshake Notification
     */
    private int HANDSHAKE_NOTIFICATION = 38;

    /**
     * instance of SMSReceiver used for receipt of messages
     */
    private SMSReceiver text;

    /**
     * instance of SMSHandler used for sending of messages
     */
    private SMSHandler handle;

    /**
     * instance of the model so that the user can save schedules that are texted
     * to it in the buddies list
     * 
     * also provides access to the FileIO class
     */
    private ScheduleWaypoint model;

    /**
     * notification manager
     */
    private NotificationManager noteManage;

    /**
     * determines if the schedule notification is to be shown
     */
    @SuppressWarnings("unused")
    private boolean showScheduleNotification;

    /**
     * determines if the handshake notification is to be shown
     */
    @SuppressWarnings("unused")
    private boolean showHandshakeNotification;

    /**
     * true if a comparison is in progress
     * 
     * false otherwise
     */
    private boolean comparison;

    // ~Constructors--------------------------------------------
    /**
     * the behavior of the service when it is created
     */
    @Override
    public void onCreate() {

        super.onCreate();
        
        noteManage = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // instantiates SMSHandler class
        handle = new SMSHandler(this.getBaseContext());

        // instantiates SMSReceiver class and registers it to receive SMS
        // messages
        text = new SMSReceiver();
        IntentFilter filter = new IntentFilter(SMS_RECEIVED);
        this.getBaseContext().registerReceiver(text, filter);

        showScheduleNotification = false;
    }

    /**
     * the behavior of the service when it is being removed
     */
    @Override
    public void onDestroy() {

        // unregisters the BroadcastReceiver
        this.getBaseContext().unregisterReceiver(text);

        // shuts down constant notifications
        noteManage.cancel(HANDSHAKE_NOTIFICATION);
        noteManage.cancel(SCHEDULE_NOTIFICATION);

        model.saveAll();
        
        super.onDestroy();
    }
    
    /**
     * checks to see if the passed in file exists
     * 
     * @param file
     *            the file to check for existance
     * @return returns true if the test_file.xml exists, returns false otherwise
     */
    private boolean fileExists(File file) {

        boolean value = false;

        if (file != null) {

            value = file.exists();
        }

        return value;
    }
    
    /**
     * sets up the Service's model, called by the activity to pass the model
     * 
     * IRRELEVANT?
     */
    public void setupModel() {

        // file to be used in the FileIO class within the ScheduleWaypoint
        // object model
        // also used to load schedule from file
        File schedulesFile = new File(getFilesDir(), "test_file.xml");
        File examsFile = new File(getFilesDir(), "exams_file.xml");
        model = new ScheduleWaypoint(schedulesFile, examsFile);

        // loads the xml file into the model contained in the service
        // check if file exists/        
        if (fileExists(schedulesFile) && fileExists(examsFile)) {

            // create the model class with the passed file
            model = new ScheduleWaypoint(schedulesFile, examsFile);

            // if so, load data from it,
            // calls load, passes the assets used by this program to it
            model.setSchedule(model.loadSchedules(schedulesFile));
            model.setFinalsList(model.loadExams(examsFile));
        }
        else if (fileExists(schedulesFile)) {

            // create the model class with the passed file
            model = new ScheduleWaypoint(schedulesFile, examsFile);

            // if so, load data from it,
            // calls load, passes the assets used by this program to it
            model.setSchedule(model.loadSchedules(schedulesFile));
        }
        else if (fileExists(examsFile)) {

            // create the model class with the passed file
            model = new ScheduleWaypoint(schedulesFile, examsFile);

            // if so, load data from it,
            // calls load, passes the assets used by this program to it
            model.setFinalsList(model.loadExams(examsFile));
        }
        else {

            // create the ScheduleWaypoint model class, with the file passed to
            // it
            model = new ScheduleWaypoint(schedulesFile, examsFile);
        }
    }

    // ~Methods--------------------------------------------
    /**
     * updates the model with the contents of a just received text
     * 
     * sends notification
     */
    public void update() {

        // add code here to search contacts by phone number (which is stored in
        // getFriendsName())
        String actualName = searchByPhoneNumber(text.getFriendsName());

        // model is ScheduleWaypoint, text is SMSHandler
        // when getFriendsName and getFriendsSchedule are called, the value is
        // gotten, then DESTROYED
        model.addFriend(actualName, text.getFriendsSchedule());

        // shows the notification of schedule shared
        showScheduleNotification(actualName);

        // write new schedules to file//
        model.saveAll();
    }

    /**
     * called when one of the handshake entries are received, either sends the
     * user's schedule or starts a notification for the user to either accept or
     * deny a request
     * 
     * @param shake
     *            the shake command
     * @param number
     *            the phone number of the user
     */
    public void handshakeReceived(String shake, String number) {

        if (shake.equals("Accept") && comparison) {
            
            // send text message
            handle.sendSchedule(number, model.getSchedule().toXML());
        }
        else if (shake.equals("Share?")) {

            // call notification, and bring user to accept/deny dialog
            showHandshakeNotification(searchByPhoneNumber(number), number);
        }
        else {

            // denied
            // do nothing
        }
    }

    /**
     * searches the contact list by phone number to find the contact name with
     * the phone number
     * 
     * @param number
     *            the number to search the contact list
     * @return contactName the name of the contact who has the number that was
     *         passed in
     */
    public String searchByPhoneNumber(String number) {

        String contactName = "";
        if (number.length() > 1) {
         
            number = number.substring(1);
    
            Uri uri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(number));
            Cursor curse = getContentResolver().query(uri,
                    new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null);
    
            // check if the cursor has stuff in it
            if (curse.moveToFirst()) {
    
                // get the contactName
                contactName = curse.getString(curse
                        .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
    
            return contactName;
        }
        else {
            
            Log.i(TAG, "there was a problem with number's length, ITS TOO SHORT!!");
            return "error";
        }
    }

    /**
     * passes the parameters on to the SMSHandler's sendHandshake method
     * 
     * necessary to avoid Generic Failure from SMSHandler by calling from
     * VTHandshakeActivity
     * 
     * @param number
     *            the phone number of the person to send the Handshake to
     * @param reply
     *            the one word handshake to send to the person
     */
    public void sendHandshake(String number, String reply) {

        handle.sendHandshake(number, reply);
    }

    // ~Service Methods-----------------------------------
    /**
     * Called by the system every time a client explicitly starts the service by
     * calling startService(Intent), providing the arguments it supplied and a
     * unique integer token representing the start request. Do not call this
     * method directly.
     * 
     * @return We want this service to continue running until it is explicitly
     *         stopped, so we have to return START_STICKY
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    /**
     * object that will receive interactions from clients
     */
    private final IBinder mBinder = new myBinder();

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class myBinder extends Binder {

        public VTFinderService getService() {

            return VTFinderService.this;
        }
    }

    public void setShowHandshakeNotification(boolean status) {

        showHandshakeNotification = status;
    }

    /**
     * Sets the value of showNotification
     * 
     * @param status
     *            the new value of showNotification
     */
    public void setShowScheduleNotification(boolean status) {

        showScheduleNotification = status;
    }

    /**
     * show a notification of handshake received while service is running
     * 
     * @param name
     *            the name of the person who is requesting a share
     */
    private void showHandshakeNotification(String name, String number) {
        
        //place name & number in intent
        Intent intent = new Intent(this, VTHandshakeActivity.class);
        intent.putExtra("vt.orientation.friendsName", name);
        intent.putExtra("vt.orientation.friendsNumber", number);

        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");

        // The PendingIntent to launch our activity if the user selects this
        // notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, 0);

        
        // Set the icon, scrolling text and timestamp
        String theText = name + " would like to share their schedule with you!";
        Notification notification = new Notification();
        notification.contentIntent = contentIntent;
        notification.tickerText = theText;
        notification.icon = R.drawable.ic_launcher;
        notification.when = System.currentTimeMillis(); 

        // defines default vibration and sounds for the notification
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;

        // makes it so notification disappears after user clicks it
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        
        //Set up content View
        notification.contentView = new RemoteViews(this.getPackageName(), R.layout.content_view);
        notification.contentView.setTextViewText(R.id.remoteTextView, theText);

        // Send the notification.
        noteManage.notify(HANDSHAKE_NOTIFICATION, notification);
    }

    /**
     * Show a notification of schedule shared while this service is running.
     * 
     * @param name
     *            the name of the person who shared their schedule
     */
    private void showScheduleNotification(String name) {

        // the intent that refers to the activity to be launched upon clicked
        // the notification
        //TODO ensure no problems occur here
        Intent intent = new Intent(this, VTComparisonActivity.class);

        // places the two schedules in the intent
        intent.putExtra("mySchedule", model.getSchedule());
        intent.putExtra("otherSchedule",
                model.getBuddy(model.getBuddiesSchedules().size() - 1));

        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");

        // The PendingIntent to launch our activity if the user selects this
        // notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, 0);

        
        // Set the icon, scrolling text and timestamp
        String theText = name + " has shared their schedule with you!";
        Notification notification = new Notification();
        notification.contentIntent = contentIntent;
        notification.tickerText = theText;
        notification.icon = R.drawable.ic_launcher;
        notification.when = System.currentTimeMillis();
        
        // defines default vibration and sounds for the notification
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;

        // makes it so notification disappears after user clicks it
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        //Set up content View
        notification.contentView = new RemoteViews(this.getPackageName(), R.layout.content_view);
        notification.contentView.setTextViewText(R.id.remoteTextView, theText);
        
        // Send the notification.
        noteManage.notify(SCHEDULE_NOTIFICATION, notification);
    }

    /**
     * sets the ScheduleWaypoint object here to the passed value
     * 
     * @param theModel
     *            the SCheduleWaypoint object to set model to
     */
    public void setModel(ScheduleWaypoint theModel) {

        model = new ScheduleWaypoint(theModel);
    }

    /**
     * returns the model contained in this service
     * 
     * used to update the Activity's model
     * 
     * @return model the model being used by the service
     */
    public ScheduleWaypoint getModel() {

        return model;
    }

    /**
     * returns an instance of the SMSHandler class
     * 
     * used to get a reference to it in the activity
     * 
     * @return handle the sms handler class in the service
     */
    public SMSHandler getHandle() {

        return handle;
    }

    /**
     * @param comparison
     *            the comparison to set
     */
    public void setComparison(boolean comparison) {

        this.comparison = comparison;
    }

    /**
     * @return the comparison
     */
    public boolean getComparison() {

        return comparison;
    }

    /**
     * the Broadcast Receiver subclass that intercepts specific text messages
     * (those containing <ScheduleComparison> to </ScheduleComparison>) and
     * saves them into the model as schedule objects
     * 
     * @author Ethan Gaebel (egaebel)
     * 
     */
    public class SMSReceiver extends BroadcastReceiver {

        // ~Data Fields---------------------------------------------------
        private final static String TAG = "SMS RECEIVER";

        /**
         * string representation of a received schedule
         */
        private String friendsSchedule;

        /**
         * friend's name who belongs to the schedule
         */
        private String friendsName;

        // ~Constructors-------------------------------------------------
        public SMSReceiver() {

            friendsSchedule = "";
            friendsName = "";
            setComparison(false);
        }

        // ~Methods---------------------------------------------------
        /**
         * getter method for friendsSchedule
         * 
         * returns friendsSchedule's value as well as wiping its value
         * 
         * @return temp the friendsSchedule in XML format
         */
        public String getFriendsSchedule() {

            String temp = friendsSchedule;

            friendsSchedule = "";

            return temp;
        }

        /**
         * getter method for friendsName
         * 
         * return the friendsName object and wipes its value
         * 
         * @return temp the friendsName value
         */
        public String getFriendsName() {

            String temp = friendsName;

            friendsName = "";

            return temp;
        }

        /**
         * receives several text messages and adds them to friend's schedule
         * until reaching the ending text, then it clears friendsSchedule and
         * awaits another transmission
         * 
         * beginning with text: <ScheduleComparison> and ending with text:
         * </ScheduleComparison>
         * 
         * @param context
         * @param intent
         *            contains the information pertaining to the text message
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            // ---get the SMS message passed in---
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;

            if (bundle != null) {

                // ---retrieve the SMS message received---
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];

                for (int i = 0; i < msgs.length; i++) {

                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    friendsSchedule += msgs[i].getMessageBody().toString();
                }

                // check for xml tag's presence, normal texts most certainly
                // will not have them
                if ((friendsSchedule != null)
                        && (friendsSchedule.contains("<") || friendsSchedule
                                .contains(">"))) {

                    // check each of the different tag possibilities that i use
                    if (friendsSchedule
                            .equals("<ComparisonRequest>Accept</ComparisonRequest>")) {

                        // get the message from the xml tags
                        friendsSchedule = friendsSchedule.replace(
                                "</ComparisonRequest>", "");
                        friendsSchedule = friendsSchedule.substring(19);

                        friendsName = msgs[0].getOriginatingAddress();

                        // get rid of the +, because it messes things up
                        friendsName.replace("+", "");

                        handshakeReceived(friendsSchedule, friendsName);
                        
                        friendsSchedule = "";
                        
                        abortBroadcast();
                    }
                    else if (friendsSchedule
                            .equals("<ComparisonRequest>Share?</ComparisonRequest>")) {

                        // get the message from the xml tags
                        friendsSchedule = friendsSchedule.replace(
                                "</ComparisonRequest>", "");
                        friendsSchedule = friendsSchedule.substring(19);

                        friendsName = msgs[0].getOriginatingAddress();

                        // get rid of + here because it causes problems
                        // otherwise
                        friendsName = friendsName.replace("+", "");

                        // pass message to handshake, for further action
                        // depending on
                        // nature of handshake, also pass phone number, to
                        // return texts
                        handshakeReceived(friendsSchedule, friendsName);
                        
                        friendsSchedule = "";
                        
                        abortBroadcast();
                    }
                    else if (friendsSchedule.contains("<ScheduleComparison>")) {

                        friendsName = msgs[0].getOriginatingAddress();

                        friendsSchedule = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                                + friendsSchedule.replace(
                                        "<ScheduleComparison>", "");
                        
                        abortBroadcast();
                    }
                    else if (friendsSchedule.contains("</ScheduleComparison>")) {

                        friendsSchedule = friendsSchedule.replaceFirst(
                                "</ScheduleComparison>", "");

                        friendsName = friendsName.replace("+", "");

                        update();

                        friendsSchedule = "";
                        
                        abortBroadcast();
                    }
                    else {

                        abortBroadcast();
                    }
                    
                    clearAbortBroadcast();
                }
                else {

                    Log.i(TAG, "your friendsSchedule is null apparently....");
                    friendsSchedule = "";
                }
            }
        }
    }
}