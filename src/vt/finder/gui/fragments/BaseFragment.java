package vt.finder.gui.fragments;

import java.util.ArrayList;
import java.util.List;
import vt.finder.R;
import vt.finder.gui.NoSwipeViewPager;
import vt.finder.gui.VTFinderFragmentAdapter;
import vt.finder.schedule.Course;
import vt.finder.schedule.Schedule;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * 
 * @author egaebel
 *
 */
public class BaseFragment extends SherlockFragment {

	//~Constants----------------------------------------------------------------------------------------
	private static final String TAG = "BASE FRAGMENT";
	
	//~Data Fields--------------------------------------------------------------------------------------------
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
     * The ActionBar UI element.
     */
    private ActionBar actionBar;
	
    //~Lifecycle Methods--------------------------------------------------------------------------------------
    /**
     * Called when fragment is associated with an activity.  
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Log.i(TAG, "onAttach: " + ((SherlockFragmentActivity)activity).getSupportFragmentManager().getFragments());
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Log.i(TAG, "onCreate called");
		//Log.i(TAG, "onCreate: " + getSherlockActivity().getSupportFragmentManager().getFragments());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);
		
		//Log.i(TAG, "onCreateView");
		
        View view = inflater.inflate(R.layout.base_fragment_layout, null);
        //Log.i(TAG, "onCreateView: " + getSherlockActivity().getSupportFragmentManager().getFragments());
        return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		super.onViewCreated(view, savedInstanceState);
		
		//Log.i(TAG, "onViewCreated");
		//Log.i(TAG, "onViewCreated: " + getSherlockActivity().getSupportFragmentManager().getFragments());
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		//Log.i(TAG, "onActivityCreated");
		//Setup Fragment Adapter and Pager-------- 
        //NO SWIPE ViewPagers setup.
        pager = new NoSwipeViewPager(this.getSherlockActivity());
        pager.setId(R.id.pager);
        this.getSherlockActivity().setContentView(pager);
		
		//~Setup ActionBar Tabs----------------------------------------------
		actionBar = this.getSherlockActivity().getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        //FragmentPagerAdapter setup.
        fragAdapt = new VTFinderFragmentAdapter(this.getSherlockActivity(), pager);
		
		Bundle arguments = this.getArguments();
		
		Schedule schedule = arguments.getParcelable("schedule");
		ArrayList<Course> finalsList = arguments.getParcelableArrayList("finalsList");
		
        //Create bundles to pass to the TabInfos.
        Bundle scheduleBundle = new Bundle();
        scheduleBundle.putParcelable("schedule", schedule);
        Bundle finalExamBundle = new Bundle();
        finalExamBundle.putParcelableArrayList("finalsList", finalsList);
        /*
        Bundle freeTimeBundle = new Bundle();
        freeTimeBundle.putParcelable("freeTime", 
        		arguments.getParcelable("freeTime"));
        */
        
        //Create/add tabs and Fragments, and set text, and set first visible object.
		Tab scheduleTab = actionBar.newTab().setText("Schedule");
		Tab finalsTab = actionBar.newTab().setText("Final Exams");
		//Tab freeTimeTab = actionBar.newTab().setText("Free Time");
		fragAdapt.addTab(finalsTab, ExamScheduleFragment.class, finalExamBundle);
		fragAdapt.addTab(scheduleTab, ScheduleFragment.class, scheduleBundle);
		//fragAdapt.addTab(freeTimeTab, FreeTimeFragment.class, freeTimeBundle);
		actionBar.selectTab(scheduleTab);
		
		//If previously used, set to tab that was previously on.
        if (savedInstanceState != null) {
            
        	actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 1));
        }
        
        //If previously used, set to tab that was previously on.
        if (savedInstanceState != null) {
            
        	actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 1));
        }
		
		//setupExamScheduleListView(finalsList);
		//setupScheduleListViews(schedule);
        //Log.i(TAG, "endOfOnActivityStarted: " + getSherlockActivity().getSupportFragmentManager().getFragments());
	}
	
	@Override
	public void onViewStateRestored(Bundle bundle) {
	    super.onViewStateRestored(bundle);
	    //Log.i(TAG, "FRAGMENT onvIEWsTATErESTORED: " + this.getSherlockActivity().getSupportFragmentManager().getFragments());
	}
	
	@Override
    public void onStart() {
	    super.onStart();
	    //Log.i(TAG, "FRAGMENT onStart: " + this.getSherlockActivity().getSupportFragmentManager().getFragments());
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    //Log.i(TAG, "FRAGMENT onResume: " + this.getSherlockActivity().getSupportFragmentManager().getFragments());
	}
	
    //~Methods------------------------------------------------------------------------------------------------
    /**
     * Updates the ExamScheduleFragment with the finalsList stored in the model.
     */
    public void setupExamScheduleListView(ArrayList<Course> finalsList) {

        if (finalsList != null) {

            ExamScheduleFragment sched = (ExamScheduleFragment) getSherlockActivity()
            		.getSupportFragmentManager()
            		.findFragmentByTag(fragAdapt.getFragmentTag(0));
            sched.updateExamList(finalsList);
        }
    }
    
    /**
     * Updates both the schedule and freeTime schedule from the schedule in the model.
     */
    public void setupScheduleListViews(Schedule schedule) {

        if (schedule != null) {

        	ScheduleFragment sched = (ScheduleFragment) getSherlockActivity()
        			.getSupportFragmentManager()
        			.findFragmentByTag(fragAdapt.getFragmentTag(1));
            sched.updateSchedule(schedule);

            /*
            FreeTimeFragment freeTime = (FreeTimeFragment) getSherlockActivity()
            		.getSupportFragmentManager()
            		.findFragmentByTag(fragAdapt.getFragmentTag(2));
            freeTime.updateSchedule(schedule.findFreeTime());
            */
        }
    }
}