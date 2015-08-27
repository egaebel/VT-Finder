package vt.finder.gui.fragments;

import vt.finder.gui.CourseComm;
import vt.finder.gui.DayAdapter;
import vt.finder.schedule.Schedule;
import vt.finder.R;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.viewpagerindicator.TitlePageIndicator;


/**
 * Fragment used to display the user's Schedule for any day swiped to.
 * 
 * @author Ethan Gaebel (egaebel)
 *
 */
public class ScheduleFragment extends SherlockFragment {

    //~Constants----------------------------------------------
	private static final String TAG = "SCHEDULE FRAGMENT";

    //~Data Fields--------------------------------------------
    /**
     * Adapter used for swiping between days.
     */
    private ViewPager dayPage;
    /**
     * The DayAdapter that is used to switch between course Lists for each day for the listView.
     */
    private DayAdapter dayAdapt;
    /**
     * The <STYLE>PageIndicator that makes the nice interface at the top for the ViewPager.
     */
    private TitlePageIndicator dayIndicator;
    /**
     * The schedule holding Courses passed to this fragment.  
     */
    private Schedule schedule;

    //~Constructors--------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
    
        //Setup view
        super.onCreate(savedInstanceState);
        
        Log.i(TAG, "onCreate");
        
        //get schedule from parcelable
        schedule = getArguments().getParcelable("schedule");
        dayAdapt = new DayAdapter(schedule, (CourseComm) this.getActivity());
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.schedule_fragment_layout___old_version, null);
        return view;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        
        super.onViewCreated(view, savedInstanceState);

        //Get dayPage ViewPager from layout xml code
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.linear_layout);
        dayPage = (ViewPager) layout.findViewById(R.id.schedule_day_pager);
        dayIndicator = (TitlePageIndicator) layout.findViewById(R.id.titles);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        
        super.onActivityCreated(savedInstanceState);

        dayPage.setAdapter(dayAdapt);
        dayPage.setOnPageChangeListener(dayAdapt);
        dayIndicator.setViewPager(dayPage, schedule.getTodayIndex());
        dayIndicator.setFooterColor(this.getResources().getColor(R.color.orange));
        this.getView().setBackgroundColor(this.getResources().getColor(R.color.maroon));
    }

    //~Methods-------------------------------------------------
    /**
     * Updates the schedule object backing the DayAdapter object
     * 
     * @param theSchedule schedule object to update with.
     */
    public void updateSchedule(Schedule theSchedule) {
    	Log.i(TAG, "Schedule updated in ScheduleFragment");
        schedule = theSchedule;

        dayAdapt.setSchedule(schedule);
        dayAdapt.notifyDataSetChanged();
    }
}
