package vt.finder.activities;

//Internal Imports
import vt.finder.gui.CourseComm;
import vt.finder.gui.VTFinderFragmentAdapter;
import vt.finder.gui.NoSwipeViewPager;
import vt.finder.gui.fragments.BaseFragment;
import vt.finder.gui.fragments.CreateCourseFragment;
import vt.finder.gui.fragments.CreateCourseFragment.ReceiveCreatedCourse;
import vt.finder.gui.fragments.ExamScheduleFragment;
import vt.finder.gui.fragments.FreeTimeFragment;
import vt.finder.gui.fragments.ScheduleFragment;
import vt.finder.io.PasswordIO;
import vt.finder.main.BuildingGpsMap;
import vt.finder.main.ScheduleWaypoint;
import vt.finder.main.VTFinderService;
import vt.finder.schedule.Course;
import vt.finder.schedule.Point;
import vt.finder.schedule.UserMadeCourse;
import vt.finder.R;
import vt.finder.web.BuildingNotFoundException;
import vt.finder.main.SMSHandler; /* REQUIRES TELEPHONY FEATURES */




//Java Imports
import java.io.File;
import java.util.Observable;
import java.util.Observer;




//ActionBar Sherlock Imports
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;




//Android Imports
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Central Activity of VT Finder. Holds sub-classes which
 * deal with tabs, fragments etc.
 * 
 * @author Ethan Gaebel (egaebel)
 *
 */
public class VTFinderActivity extends SherlockFragmentActivity implements CourseComm, ReceiveCreatedCourse {

    //~Constants---------------------------------------------------------//
    private static final String TAG = "VT ORIENTATION ACTIVITY";
    /**
     * Constant for preferences file used to store information pertaining 
     * to if this is the first run or not.
     */
    private final String PREFS_NAME = "MyPrefsFile";
    private final String QUICK_START_TEXT = "Thank you for downloading VT Finder! \n Please take the time to rate VT Finder in Google Play." +
    		"\n\nGET STARTED:" +
    		"\n�Change views from 'FINAL EXAMS' to 'SCHEDULE' to 'FREE TIME' by swiping left to right and vice-versa. " +
    		"\n�Get your schedule from hokiespa by clicking \'Get Schedule\' at the bottom of the screen under either the \'SCHEDULE\' or \'FREE TIME\' views. " +
    		"\n�Get your final exam schedule by clicking \'Get Exam Schedule\' at the bottom of the \'FINAL EXAMS\' view." +
    		"\n\nLOGGING IN: " +
    		"\n�To log into hokiespa you just need to select the term you want information for, and then enter your username and password. *THIS INFORMATION IS NOT MISUSED*" +
    		"you can optionally save this information to your phone to save time later. When saved it is encrypted to keep people from snooping." +
    		"\n\nGETTING DIRECTIONS TO CLASS:" +
    		"\n�After you\'ve loaded your schedule from hokiespa, just tap one of your classes and then push the \'Take me to class\' button to get instant Google Maps directions." +
    		"�If you do not have GPS satellites enabled you will be prompted to enable them, and then you\'ll have a bit of a wait because Android takes forever to acquire GPS data." +
    		"\n\nSWAPPING SCHEDULES WITH FRIENDS:" +
    		"\n�VT Finder enables you to easily exchange schedules with your friends, and automatically compares your schedules to find common blocks of free time. " +
    		"\n�Just click \'Compare with a Friend\' in the \'SCHEDULE\' view or the \'FREE TIME\' view and then select someone from your contacts list to swap schedules with. " +
    		"They\'ll be informed of your request, and prompted to accept or decline your offer to exchange schedules. If they accept, both of your schedules will then be" +
    		" exchanged using text messages, compared for free time, and loaded into VT Finder.";
    
    //~Request Codesc
    /**
     * request code for switching to the Contact list
     */
    private static final int PICK_CONTACT = 77;

    /**
     * request code for switching to settings menu for enabling of GPS
     */
    private static final int GPS_CHECK = 88;

    /**
     * request code for switching to settings menu for disabling GPS on closing
     * application
     */
    private static final int GPS_CHECK_ON_CLOSE = 99;

    //~Handshake strings
    /**
     * the first text message in the handshake process
     */
    private static final String SHARE = "Share?";
    
    //~Data Fields-------------------------------------------------------//
    /**
     * View pager object used for holding and switching between pages of fragments.
     */
    private NoSwipeViewPager pager;
    /**
     * Adapter used for creating tabs, handling tab clicking, page changing, and fragment
     * paging.
     */
    private VTFinderFragmentAdapter fragAdapt;
    /**
     * The ScheduleWaypoint object that holds, and is the intermediary for all of the data in this app.
     */
    private ScheduleWaypoint model;
    /**
     * PasswordIO class that saves the user's hokiespa login info for automatic
     * entry
     */
    private PasswordIO passwordSave;
    /**
     * Instance of the SMSHandler class used for sending/receiving texts.
     */
    private SMSHandler text;
    /**
     * String that represents the semester selected by the user in the pop up
     * box that comes up after getSchedule is called.
     */
    private String semester;
    /**
     * indicates whether the service is bound to this activity or not
     */
    private boolean isBound = false;
    /**
     * The Course object that has been selected in the fragment.
     */
    private Course selection;
    /**
     * The ActionBar UI element.
     */
    private ActionBar actionBar;
    /**
     * The BaseFragment that is the root element of the entire UI.
     */
    private BaseFragment baseFragment;
    
    //~Lifecycle Methods------------------------------------------------------//
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vtfinder);
        
        /*
        //NO SWIPE ViewPagers setup.
        pager = new NoSwipeViewPager(this.getBaseContext());
        pager.setId(R.id.pager);
        setContentView(pager);
        //FragmentPagerAdapter setup.
        fragAdapt = new VTFinderFragmentAdapter(this, pager);
        
        //ActionBar setup
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        */
        
        // setup password saving stuff
        File passwordFile = new File(getFilesDir(), "password_file.txt");
        passwordSave = new PasswordIO(passwordFile);

        // Get path for the file on external storage. If external
        // storage is not currently mounted this will fail.
        File schedulesFile = new File(getFilesDir(), "test_file.xml");
        File examsFile = new File (getFilesDir(), "exams_file.xml");
        
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
        
        //Create BaseFragment, and setup arguments------------------------------
        baseFragment = new BaseFragment();
        Bundle dataBundle = new Bundle();
        dataBundle.putParcelable("schedule", model.getSchedule());
        dataBundle.putParcelableArrayList("finalsList", model.getFinalsList());
        dataBundle.putParcelable("freeTime", model.getSchedule().findFreeTime());
        baseFragment.setArguments(dataBundle);
        
        //Add Fragment
        FragmentTransaction fragTrans = this.getSupportFragmentManager().beginTransaction();
        fragTrans.replace(R.id.base_layout, baseFragment);
        fragTrans.commit();
        getSupportFragmentManager().executePendingTransactions();
        
        /*
        //Create bundles to pass to the TabInfos.
        Bundle scheduleBundle = new Bundle();
        scheduleBundle.putParcelable("schedule", model.getSchedule());
        Bundle finalExamBundle = new Bundle();
        finalExamBundle.putParcelableArrayList("finalsList", model.getFinalsList());
        Bundle freeTimeBundle = new Bundle();
        freeTimeBundle.putParcelable("freeTime", model.getSchedule().findFreeTime());

        //Create/add tabs and Fragments, and set text, and set first visible object.
        Tab scheduleTab = actionBar.newTab().setText("Schedule");
        fragAdapt.addTab(actionBar.newTab().setText("Final Exams"), ExamScheduleFragment.class, finalExamBundle);
        fragAdapt.addTab(scheduleTab, ScheduleFragment.class, scheduleBundle);
        fragAdapt.addTab(actionBar.newTab().setText("Free Time"), FreeTimeFragment.class, freeTimeBundle);
        actionBar.selectTab(scheduleTab);
        */
                
        /*
        //If previously used, set to tab that was previously on.
        if (savedInstanceState != null) {
            
        	actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 1));
        }
        */

        //start service
        Intent service = new Intent(this.getBaseContext(),
                VTFinderService.class);
        startService(service);

        // binds service to activity//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        doBindService();
        
        //First start check for alert dialog introduction
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);

        if (!dialogShown) {
        
            // AlertDialog code here
            firstRunAlert();

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.commit();    
        }
        
        //initialize async task switches to false
        scheduleScraping = false;
        examScheduleScraping = false;
    }
    
    /**
     * Creates and displays an alert dialog to be displayed on a first run.
     */
    private void firstRunAlert() {
        
        // --ALERT BOX FOR QUICK START GUIDE ON FIRST RUN
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.quick_start_guide, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this)
                .setView(layout);

        alert.setTitle("Quick Start Guide");
        TextView text = (TextView) layout.findViewById(R.id.quick_start_message);
        text.setText(QUICK_START_TEXT);

        alert.setPositiveButton("OK",
                new DialogInterface.OnClickListener() { 
                    public void onClick(DialogInterface dialog, int whichButton) {
                        
                        dialog.cancel();
                    }});
        
        AlertDialog startAlertDialog = alert.create();
        startAlertDialog.show();
    }
    
    /**
     * Defines activity behavior on resume.
     */
    @Override
    protected void onResume() {
    	super.onResume();
    }
    
    /**
     * Defines activity behavior on suspension
     */
    @Override
    protected void onPause() {
    	super.onPause();
    	model.saveAll();
    }
    
    /**
     * Defines activity behavior on exit
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();

        //doUnbindService();
        //TODO remove, and re-add this to see the effect on the service's persistence.
        model.deleteObservers();
        unbindService(serviceConnection);
        model.saveAll();
    }

    /**
     * Helper method, checks to see if the passed in file exists.
     * 
     * @param file
     *            the file to check for existence
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
     * Updates the ExamScheduleFragment with the finalsList stored in the model.
     */
    protected void setupExamScheduleListView() {

    	/*
        if (model.getFinalsList() != null) {
            
            ExamScheduleFragment sched = (ExamScheduleFragment) getSupportFragmentManager().findFragmentByTag(fragAdapt.getFragmentTag(0));
            sched.updateExamList(model.getFinalsList());
        }
        */
    }
    
    /**
     * Updates both the schedule and freeTime schedule from the schedule in the model.
     */
    protected void setupScheduleListViews() {

    	/*
        if (model.getSchedule() != null) {
            ScheduleFragment sched = (ScheduleFragment) getSupportFragmentManager().findFragmentByTag(fragAdapt.getFragmentTag(1));
            sched.updateSchedule(model.getSchedule());
        }
        
        if (model.getFreeTime() != null) {
            FreeTimeFragment freeTime = (FreeTimeFragment) getSupportFragmentManager().findFragmentByTag(fragAdapt.getFragmentTag(2));
            freeTime.updateSchedule(model.getSchedule().findFreeTime());
        }
        */
    }
    
    //~Methods---------------------------------------------------------------------------------------//
    /**
     * Takes a UserMadeCourse and a daysString and puts the UserMadeCourse into
     * the model in the days specified by daysString.
     * 
     * @param course the UserMadeCourse that is to be added to the model.
     * @param daysString a String indicating which days the passed in course occurs on.
     */
    public void receiveCreatedCourse(UserMadeCourse course, String daysString) {
    	
    	//Add and refresh
        model.addUserMadeCourse(course, daysString);
        setupScheduleListViews();
    }
    
    private static String CREATE_COURSE_FRAG = "CREATE_COURSE_FRAGMENT";
    /**
     * Pops up a box to enter in course data. Creates a course with the corresponding course data.
     * 
     * @param view
     */
    public void addCourseClicked(View view) {
    
    	//Create a Course/On Campus EVENT----------------------------------------------------------------------
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.course_addition_selection_dialog, null);
        layout.setBackgroundColor(this.getResources().getColor(R.color.maroon));
        final AlertDialog.Builder alert = new AlertDialog.Builder(this).setView(layout);
        
        alert.setTitle("Create a Course/VT Event");
        
        
        alert.setPositiveButton("Submit",
                new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						//Either pull schedule
						final RadioButton timetable = (RadioButton) layout.findViewById(R.id.timetableRadioButton);
						final RadioButton customCourse = (RadioButton) layout.findViewById(R.id.customCourseRadioButton);
						
						//Next Dialog box
						if (timetable.isChecked()) {
							
							launchSemesterSelectionDialog();
							//Scrape
						}
						//Launch Custom Course Fragment
						else if (customCourse.isChecked()) {
							
							pushCreateCourseFragment();
						}
						else {
							
							mustSelectCourseSelectionToast();
						}
					}
                         
                });
        
        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        dialog.cancel();
                    }
                });
        
        alert.show();
    }
    
    /**
     * Display a toast used in adding extra course dialog.
     */
    private void mustSelectCourseSelectionToast() {
    	
    	Toast.makeText(this, 
				"You must select either Timetable or Custom Course", 
				Toast.LENGTH_LONG).show();
    }
    
    /**
     * Creates a new instance of the CreateCourseFragment and pushes it onto the top of the stack.
     */
    private void pushCreateCourseFragment() {
    	
    	Log.i(TAG, "Pushing CreateCoureFragment to the pager view....");
    	final String CREATE_COURSE_FRAGMENT_NAME = "CreateCourseFragment"; 

    	SherlockFragment createCourseFragment = new CreateCourseFragment();
    	//Fragment Transaction
    	getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
			
			@Override
			public void onBackStackChanged() {
			
				Log.i(TAG, "BackStackEntryCount == " + getSupportFragmentManager().getBackStackEntryCount());
				//Base level
				if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
					
					actionBar.show();
				}
				//If top is CreateCourseFragment
				else if (getSupportFragmentManager()
						.getBackStackEntryAt((getSupportFragmentManager().getBackStackEntryCount() - 1))
						.getName()
						.equals(CREATE_COURSE_FRAGMENT_NAME)) {
					
					actionBar.hide();
				}
			}
		});
		FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();
		//fragTrans.remove(arg0);
		fragTrans.replace(R.id.pager, createCourseFragment, CREATE_COURSE_FRAG);
		fragTrans.addToBackStack(CREATE_COURSE_FRAGMENT_NAME);
		fragTrans.commit();
		getSupportFragmentManager().executePendingTransactions();
		Log.i(TAG, "Pushed CreateCoureFragment to the pager view!");
    }
    
    public void launchLoginDialog() {
    	//TODO: Prompt for login.
    }
    
    /**
     * Parameter-less wrapper for main launchSemesterSelectionDialog method.
     */
    public void launchSemesterSelectionDialog() {
    	launchSemesterSelectionDialog(null);
    }
    
    /**
     * Creates a dialog where the user is prompted to enter in the semester they are interested in.
     * Takes a passedAlert for chaining together dialogs. If there is no dialog to chain, this argument should be null. 
     * 
     * @param passedAlert the next Dialog to create.
     */
    public void launchSemesterSelectionDialog(final AlertDialog.Builder passedAlert) {
    	
    	// --ALERT BOX FOR SEMESTER SELECTION
        LayoutInflater semesterInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View semesterLayout = semesterInflater.inflate(
                R.layout.semester_selection_box, null);
        semesterLayout.setBackgroundColor(this.getResources().getColor(R.color.maroon));
        AlertDialog.Builder semesterAlert = new AlertDialog.Builder(this)
                .setView(semesterLayout);

        semesterAlert.setTitle("Semester Selection");

        final RadioButton fall = (RadioButton) semesterLayout
                .findViewById(R.id.fall);
        final RadioButton spring = (RadioButton) semesterLayout
                .findViewById(R.id.spring);
        final RadioButton summer1 = (RadioButton) semesterLayout
                .findViewById(R.id.summer1);
        final RadioButton summer2 = (RadioButton) semesterLayout
                .findViewById(R.id.summer2);

        semesterAlert.setPositiveButton("Submit",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (fall.isChecked()) {

                            semester = "09";
                        }
                        else if (spring.isChecked()) {

                            semester = "01";
                        }
                        else if (summer1.isChecked()) {

                            semester = "06";
                        }
                        else if (summer2.isChecked()) {

                            semester = "07";
                        }
                        else {

                            // if something went wrong, default to fall, cus the
                            // freshies wont know whats what
                            semester = "09";
                        }

                        //If we were passed another AlertDialog to initiate....
                        if (passedAlert != null) {
	                        AlertDialog alertDialog = passedAlert.create();
	                        alertDialog.show();
                        }
                    }
                });

        semesterAlert.setNegativeButton("Do later",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        dialog.cancel();
                    }
                });

        // box is created later, right after login box is

        AlertDialog semesterAlertDialog = semesterAlert.create();
        semesterAlertDialog.show();
    }
    
    /**
     * contains code for two alert boxes, one that involves selection of a
     * semester to get the schedule for, and a second to enter login information
     * for hokiespa that then passes that information to file (in encrypted
     * format) if the user specifies, and into the login space in hokiespa
     * 
     * After this method is called and the correct values are entered, the
     * schedule should be populated, unless there is no internet connection at
     * the time
     * 
     * @param view
     */
    public void getScheduleClicked(View view) {
        
        semester = null;

        //if scheduleScraping is NOT already happening
        if (!scheduleScraping) {

            // --ALERT BOX FOR LOGIN
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.login_box, null);
            layout.setBackgroundColor(this.getResources().getColor(R.color.maroon));
            final AlertDialog.Builder alert = new AlertDialog.Builder(this)
                    .setView(layout);
    
            alert.setTitle("Login Dialog");
    
            final EditText user = (EditText) layout.findViewById(R.id.userField);
            final EditText password = (EditText) layout
                    .findViewById(R.id.passwordField);
    
            final CheckBox savePassword = (CheckBox) layout
                    .findViewById(R.id.savePasswordCheck);
    
            if (passwordSave.fileExists()) {
    
                String[] loginData = passwordSave.readDecrypt();
    
                if (loginData != null) {
    
                    user.setText(loginData[0]);
                    password.setText(loginData[1]);
                }
            }
    
            alert.setPositiveButton("Submit",
                    new DialogInterface.OnClickListener() {
    
                        public void onClick(DialogInterface dialog, int whichButton) {
    
                            boolean savePasswordChecked = savePassword.isChecked();
    
                            if (savePasswordChecked 
                                    && user.getText().toString().length() > 0 
                                    && password.getText().toString().length() > 0) {
    
                                // write password to file
                                passwordSave.writeEncrypt(
                                        user.getText().toString(), password
                                                .getText().toString());
                            }
    
                            new ScheduleScrapeTask().execute(user.getText().toString(), 
                                    password.getText().toString(), semester, getFilesDir().toString());
                            
                            grabbingScheduleToast();
                        }
    
                    });
    
            alert.setNegativeButton("Do later",
                    new DialogInterface.OnClickListener() {
    
                        public void onClick(DialogInterface dialog, int whichButton) {
    
                            dialog.cancel();
                        }
                    });
            
            //Launch the semester Selection
            launchSemesterSelectionDialog(alert);
        }
        else {
            
            Toast.makeText(this, "Wait for schedule scraping to " +
            		"complete before trying to do so again.", Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Display a toast indicating that the schedule is being grabbed 
     */
    private void grabbingScheduleToast() {
    	
    	Toast.makeText(this, 
    			"Grabbing schedule, please wait...", 
    			Toast.LENGTH_LONG).show();
    }
    
    /**
     * executed when the "Get Finals Schedule" button is clicked, switches to an
     * activity displaying a listview of the finals after prompting the user to
     * enter in a term (fall, spring, summer 1, summer 2) and their hokiespa
     * username and password via pop up boxes
     * 
     * @param view
     */
    public void getFinalsScheduleClicked(View view) {

        if (!examScheduleScraping) {           
    
            // --ALERT BOX FORjbnjbbnjbkkkkkjbj
            // LOGIN--------------------------------------------------------------------
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.login_box, null);
            layout.setBackgroundColor(this.getResources().getColor(R.color.maroon));
            final AlertDialog.Builder alert = new AlertDialog.Builder(this)
                    .setView(layout);
    
            alert.setTitle("Login Dialog");
    
            final EditText user = (EditText) layout.findViewById(R.id.userField);
            final EditText password = (EditText) layout
                    .findViewById(R.id.passwordField);
    
            final CheckBox savePassword = (CheckBox) layout
                    .findViewById(R.id.savePasswordCheck);
    
            if (passwordSave.fileExists()) {
    
                String[] loginData = passwordSave.readDecrypt();
    
                if (loginData != null) {
    
                    user.setText(loginData[0]);
                    password.setText(loginData[1]);
                }
            }
    
            alert.setPositiveButton("Submit",
                    new DialogInterface.OnClickListener() {
    
                        public void onClick(DialogInterface dialog, int whichButton) {
    
                            boolean savePasswordChecked = savePassword.isChecked();
    
                            if (savePasswordChecked
                                    && user.getText().toString().length() > 0 
                                    && password.getText().toString().length() > 0) {
    
                                // write password to file
                                passwordSave.writeEncrypt(
                                        user.getText().toString(), password
                                                .getText().toString());
                            }
    
                            new ExamScrapeTask().execute(user.getText().toString(), 
                                    password.getText().toString(), semester, getFilesDir().toString());
                            
                            makeGrabbingExamScheduleToast();
                        }
    
                    });
    
            alert.setNegativeButton("Do later",
                    new DialogInterface.OnClickListener() {
    
                        public void onClick(DialogInterface dialog, int whichButton) {
    
                            dialog.cancel();
                        }
                    });
    
            this.launchSemesterSelectionDialog(alert);
        }
        else {
            
            Toast.makeText(this, "Already grabbing exam schedule, please wait.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Display a toast indicating the final exam schedule is being grabbed.
     */
    private void makeGrabbingExamScheduleToast() {
    	
    	Toast.makeText(this, 
    			"Grabbing final exam schedule, please wait....", 
    			Toast.LENGTH_LONG).show();
    }
    
    /**
     * executed when "Take me to Class" is clicked, switches view to a google
     * maps view that maps the user to their selected class
     */
    public void takeMeToClassClicked(View view) {

        if (selection != null) {

            if (!selection.getBuilding().trim().equals("TBA")) {
                    
                // check if GPS is on...
                String provider = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if (!provider.contains("gps")) {
    
                    Toast.makeText(getBaseContext(),
                            "You must turn on GPS to use this feature",
                            Toast.LENGTH_LONG).show();
    
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, GPS_CHECK);
                }
                else {
    
                    emporDialog();
                }
            }
            else {
                
                Toast.makeText(getBaseContext(), "That class does not have a location.", Toast.LENGTH_LONG).show();
            }
        }
        else {

            Toast.makeText(getBaseContext(), "Select a class to map to!", Toast.LENGTH_LONG)
                    .show();
        }
    }
    
    /**
     * called when "Compare with Friends" button is clicked
     * 
     * 1. Switches to Contacts view 2. Receives chosen contact number from
     * contacts view 3. Sends Schedule to Contact ** The Service waits for a
     * return text ** When service receives return text, display sent courses
     * list
     * 
     * @param view
     */
    public void compareWithFriendsClicked(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

        startActivityForResult(intent, PICK_CONTACT);
    }

    /**
     * switches to the google maps app to show directions from the user's
     * current location to the selected class
     */
    private void showMapping(final Course selection) {

        Toast.makeText(getBaseContext(),
                "Wait one moment while gps data is fetched.....",
                Toast.LENGTH_LONG).show();

        // creates new location manager objects to get gps position
        LocationManager manage = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        // LocationListener class and all of its required methods
        LocationListener listen = new LocationListener() {

            private int locationChangedCounter = 0;

            public void onLocationChanged(Location locale) {

                locationChangedCounter++;
                
                // get gps data twice, to be precise, but fast
                if (locationChangedCounter == 2) {

                    Point buildingPoint = null;
                    try {
                        buildingPoint = BuildingGpsMap.get(selection.getBuilding());
                    }
                    catch (BuildingNotFoundException e) {
                        e.printStackTrace();
                    }
                    
                    //Since I can't throw an Exception here for some reason...
                    if (buildingPoint != null) {
                    
                        // gets the latitude and longitude
                        double latitude = buildingPoint.x / 1e6;
                        double longitude = buildingPoint.y / 1e6;

                        // ****start gooogle maps with passed in
                        // cords*****************//
                        Intent intent = new Intent(
                                android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?" + "saddr="
                                        + locale.getLatitude() + ", "
                                        + locale.getLongitude() + "&daddr="
                                        + latitude + ", " + longitude));

                        intent.setClassName("com.google.android.apps.maps",
                                "com.google.android.maps.MapsActivity");

                        startActivity(intent);
                    }
                    //if there was an error...
                    else {
                        //XXX this is in place to make error reporting easy//
                        Toast.makeText(getBaseContext(), 
                                "The building data is not stored, " +
                                "please submit crash report so I can add it!", 
                                Toast.LENGTH_LONG).show();
                        try {
                            Thread.sleep(5000);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //I CRASH IT MYSELF!!
                        Object crash = null;
                        crash.toString();
                    }
                }
            }

            public void onStatusChanged(String provider, int status,
                    Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }

        };
        // registers the listener with the location manager to receive updates
        manage.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                listen);
    }

    /**
     * If the current selection is EMPOR
     * displays a dialog box that asks the user if they want directions to the
     * math emporiuum or burruss hall, and informs them of their options.
     * Otherwise just displays mapping to route.
     * 
     * @param selection the Course object that was selected to map to.
     */
    private void emporDialog() {

        // check if building is the EMPORIUM
        if (selection.getBuilding().equals("EMPOR")) {

            // the below is a pop-up box that tells how to get to the math
            // empo
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            
            alert.setTitle("Math Emporium Directions");
            alert.setMessage("To get to the math emporium you can take either "
                    + "the University Mall shuttle or the University City Boulevard Bus which can "
                    + "be caught in front of Burruss Hall among other places. Press Map to get directions to Burruss, close to close this");

            alert.setPositiveButton("Map",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                int whichButton) {

                            showMapping(selection);
                        }

                    });

            alert.setNegativeButton("Close",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                int whichButton) {

                            dialog.cancel();
                        }
                    });

            alert.show();
        }
        else {

            showMapping(selection);
        }


    }
    
    // ~ON ACTIVITY RESULT------------VERY
    // LONG-----------------------------------
    /**
     * / Called when an activity that we started returns a result back to us.
     * 
     * @param requestCode
     *            the request code, an integer that identifies which activity
     *            returned
     * @param resultCode
     *            either RESULT_OK or RESULT_CANCELLED
     * @param data
     *            the data that the activity sent back in the form of an Intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case PICK_CONTACT:

                if (resultCode == Activity.RESULT_OK) {

                    String id = "";
                    String name = "";
                    final String number;

                    Uri contactData = data.getData();
                    //TODO fix contact data access point
                    //managedQuery(contactData, null, null, null, null);
                    Cursor cursor = getContentResolver().query(contactData, null, null, null, null);

                    if (cursor.moveToFirst()) {

                        id = cursor.getString(cursor
                                .getColumnIndex(ContactsContract.Contacts._ID));
                        name = cursor
                                .getString(cursor
                                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    }

                    // if searchFriends SUCCEEDED!; get the friend's schedule
                    // from the list
                    int searchResult = model.searchFriends(name);
                    if (searchResult != -1) {

                        // switch to activity to show similar schedules
                        // the intent that refers to the activity to be launched
                        // upon clicked
                        // the notification
                        Intent intent = new Intent(this,
                                VTComparisonActivity.class);

                        // places the two schedules in the intent
                        intent.putExtra("mySchedule", model.getSchedule());
                        intent.putExtra("otherSchedule",
                                model.getBuddy(searchResult));

                        startActivity(intent);
                    }
                    else {

                        // if the selected contact has a phone number, send the
                        // schedule to them
                        if (Integer
                                .parseInt(cursor.getString(cursor
                                        .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) != 0) {

                            Cursor phones = getContentResolver()
                                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                            null,
                                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                    + " = " + id, null, null);

                            // gets the phone number and sets number to it
                            // then launches dialog and sends text
                            if (phones.moveToFirst()) {

                                number = phones
                                        .getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                                phones.close();

                                // dialog, ARE YOU SURE YOU WANT TO TEXT???!!!
                                AlertDialog.Builder alert = new AlertDialog.Builder(
                                        this);

                                alert.setTitle("Did you pick the right person?");
                                alert.setMessage("Are you sure you want to share your schedule with "
                                        + name
                                        + "? Standard texting rates apply");

                                alert.setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {

                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int whichButton) {

                                                // sends 1st handshake text
                                                // message
                                                text.sendHandshake(number,
                                                        SHARE);
                                            }

                                        });

                                alert.setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {

                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int whichButton) {

                                                dialog.cancel();
                                            }
                                        });

                                alert.show();

                            }
                        }
                        // if no phone number, notify
                        else {

                            Toast.makeText(
                                    getBaseContext(),
                                    "There is no phone number associated with that person...",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
                break;

            case GPS_CHECK:

                // check if GPS is on...
                String provider = Settings.Secure.getString(
                        getContentResolver(),
                        Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if (!provider.contains("gps")) {

                    Toast.makeText(
                            getBaseContext(),
                            "You must turn on GPS to use this feature, "
                                    + "Click Take me To class to try again",
                            Toast.LENGTH_LONG).show();
                }
                else {

                    emporDialog();
                }
                break;

            case GPS_CHECK_ON_CLOSE:

                finish();

                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // ~Service
    // Stuff------------------------------------------------------------//
    // ~-------------------------------------------------------------------------//
    /**
     * the service that runs continuously checking for text messages
     */
    private VTFinderService orientationService;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {

            // sets value to orientationService
            orientationService = ((VTFinderService.myBinder) service)
                    .getService();

            // set the SMSHandler reference to be equal to the SMSHandler in the
            // service
            text = orientationService.getHandle();

            // link both models to an observer
            linkObservers();
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
        isBound = bindService(service, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    void doUnbindService() {

        if (isBound) {

            unbindService(serviceConnection);

            isBound = false;

            orientationService.getModel().deleteObservers();
            model.deleteObservers();
        }
    }

    //~Observer stuff--------------
    /**
     * Links the models in both the service and the activity to a Waypoint
     * observer.
     */
    public void linkObservers() {

        WaypointObserver watcher = new WaypointObserver();

        orientationService.setModel(model);
        
        // links the model of the service to the waypoint observer
        orientationService.getModel().addObserver(watcher);

        // links the model of the activity to the waypoint observer
        model.addObserver(watcher);
    }
    
    //~Sub-Classes---------------------------------------------------------------------------//
    /**
     * observer sub class for the VTOrientation activity, updates the activity
     * model if running whenever the model in the service is updated
     * 
     * @author Ethan Gaebel (egaebel)
     * 
     */
    public class WaypointObserver implements Observer {

        /**
         * updates the buddies list in the activity with the buddies list from
         * the service
         */
        public void update(Observable arg0, Object arg1) {

            // check if the activity's buddies is smaller than the service's
            if (model.getBuddiesSchedules().size() < orientationService
                    .getModel().getBuddiesSchedules().size()) {

                // update the activity's buddies list

                model.setBuddeisSchedules(orientationService.getModel()
                        .getBuddiesSchedules());
            }
            else {

                // update the service's schedule with the activity's schedule
                orientationService.getModel().setSchedule(model.getSchedule());
            }
        }
    }
    
    /**
     * Takes a course object and sets selection equal to Course.
     *
     * Part of CourseComm interface.
     * 
     * @param course the Course object that is the current selection.
     */
    @Override
    public void setCourse(Course course) {
        
        selection = course;
    }
    
    //~Async Tasks------------------------------------------------
    /**
     * Boolean variable identifying whether ScheduleScrapeTask 
     * is running currently or not. True if running, false if not.
     */
    private boolean scheduleScraping;
    /**
     * Boolean variable identifying whether the ExamScrapeTask is
     * currently running or not. True if running, false if not.
     */
    private boolean examScheduleScraping;
    /**
     * Boolean variable identifying whether the TimetableScrapeTask 
     * is currently running or not. True if running, false if not.
     */
    private boolean timetableScraping;
    /**
     * Asynchronous task that performs schedule scraping.
     * 
     * @author Ethan Gaebel (egaebel)
     *
     */
    private class ScheduleScrapeTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            scheduleScraping = true;
            return model.scrapeSchedule(params[0], params[1], params[2], params[3]);
        }
        
        @Override
        protected void onPostExecute(Boolean value) {
            
            if (value) {
                
                baseFragment.setupScheduleListViews(model.getSchedule());
            }
            else {
                
                Toast.makeText(getBaseContext(), "You entered an incorrect PID or password. Or your connection could not be established. Please Try again.", 
                        Toast.LENGTH_LONG).show();
            }
            
            scheduleScraping = false;
        }
    }
    
    /**
     * Asynchronous task that performs exam schedule scraping.
     * 
     * @author Ethan Gaebel (egaebel)
     *
     */
    private class ExamScrapeTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            
            examScheduleScraping = true;
            return model.scrapeExamSchedule(params[0], params[1], params[2], params[3]);
        }
        
        @Override
        protected void onPostExecute(Boolean value) {
            
          if (value) {  
              
              baseFragment.setupExamScheduleListView(model.getFinalsList());
          }
          else {
              
              Toast.makeText(getBaseContext(), "You entered an incorrect PID or password. Or your connection could not be established. Please Try again.", 
                      Toast.LENGTH_LONG).show();
          }
          
          examScheduleScraping = false;
        }
    }
    
    private class TimetableScrapeTask extends AsyncTask<String, Integer, Boolean> {
    	
    	@Override
    	protected Boolean doInBackground(String... params) {
    		
    		timetableScraping = true;
    		//scrape stuff by either CRN or course code
    		//TODO
    		return false;
    	}
    	
    	@Override
    	protected void onPostExecute(Boolean value) {
    		
    		if (value) {
    			
    			//Do something....
    		}
    		else {
    			
    			Toast.makeText(getBaseContext(), "You entered in an invalid CRN or course code. Or your connection could not be established. Please try again.", 
    					Toast.LENGTH_LONG).show();
    		}
    		
    		timetableScraping = false;
    	}
    }
}