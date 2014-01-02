package vt.finder.activities;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import vt.finder.gui.CompFreeTimeFragment;
import vt.finder.gui.CompScheduleFragment;
import vt.finder.gui.CourseComm;
import vt.finder.gui.MyFragmentAdapter;
import vt.finder.gui.NoSwipeViewPager;
import vt.finder.main.ScheduleComparison;
import vt.finder.schedule.Course;
import vt.finder.schedule.Schedule;
import vt.finder.R;
import android.os.Bundle;

/**
 * Activity which displays a comparison of two (or more) users' free time,
 * and common classes.
 * 
 * @author Ethan Gaebel (egaebel)
 *
 */
public class VTComparisonActivity extends SherlockFragmentActivity implements CourseComm {

    //~Data Fields--------------------------------------------
    /**
     * View Pager object used for holding and switching between
     * pages of fragments, with common classes, and common free time.
     */
    private NoSwipeViewPager pager;
    /**
     * Fragment adapter used to handle fragments under tabs.
     */
    private MyFragmentAdapter fragAdapt;
    /**
     * Instance of ScheduleComparison class, used to compare the received schedules.
     */
    private ScheduleComparison compare;
    
    //~Constructors--------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vtfinder);
        
        //ViewPagers setup.
        pager = new NoSwipeViewPager(this.getBaseContext());
        pager.setId(R.id.pager);
        setContentView(pager);
        
        //ActionBar setup
        final ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(false);
        bar.setDisplayShowTitleEnabled(false);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        
        //Get Schedules from intent, and pass comparison results to tabs
        // get passed intent and rebuild the two schedules passed in it
        Bundle received = this.getIntent().getExtras();
        Schedule mySchedule = received.getParcelable("mySchedule");
        Schedule friendsSchedule = received
                .getParcelable("otherSchedule");
        
        compare = new ScheduleComparison(mySchedule, friendsSchedule);
        
        Bundle scheduleBundle = new Bundle();
        scheduleBundle.putParcelable("schedule", compare.getSharedCourses());
        Bundle freeTimeBundle = new Bundle();
        freeTimeBundle.putParcelable("freeTime", compare.getSharedFreeTime());
        
        //FragmentPagerAdapter etc. setup.
        fragAdapt = new MyFragmentAdapter(this, pager);
        
        //Create/add tabs, and set text.
        fragAdapt.addTab(bar.newTab().setText("Common Classes"), CompScheduleFragment.class, scheduleBundle);
        fragAdapt.addTab(bar.newTab().setText("Common Free Time"), CompFreeTimeFragment.class, freeTimeBundle);
        
        //If previously used, set to tab that was previously on.
        if (savedInstanceState != null) {
            
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }
    //~Methods-------------------------------------------------

    @Override
    public void setCourse(Course course) {

        //TODO Does nothing so far; SHOULD IT? CAN IT? probably not...
    }
    
    //~Sub-Classes---------------------------------------------
}
