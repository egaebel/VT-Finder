package vt.finder.gui;

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
public class CompScheduleFragment extends SherlockFragment {

    //~Constants----------------------------------------------
    private final String TAG = "COMPARE_SCHEDULE_FRAGMENT";

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
        
        //get schedule from parcelable
        schedule = getArguments().getParcelable("schedule");
        dayAdapt = new DayAdapter(schedule, (CourseComm) this.getActivity());
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.comp_schedule_fragment_layout, null);
        view.setBackgroundColor(this.getResources().getColor(R.color.maroon));
        return view;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        
        super.onViewCreated(view, savedInstanceState);

        //Get dayPage ViewPager from layout xml code
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.linear_layout);
        dayPage = (ViewPager) layout.findViewById(R.id.schedule_day_pager);
        Log.i(TAG, "yeah! right before I find my view!");
        dayIndicator = (TitlePageIndicator) layout.findViewById(R.id.titles);
        Log.i(TAG, "yeah! right afterrrrrrrr I find my view!");
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        
        super.onActivityCreated(savedInstanceState);

        dayPage.setAdapter(dayAdapt);
        dayPage.setOnPageChangeListener(dayAdapt);
        dayIndicator.setViewPager(dayPage, schedule.getTodayIndex());
        dayIndicator.setFooterColor(this.getResources().getColor(R.color.orange));
    }

    //~Methods-------------------------------------------------
    /**
     * Updates the schedule object backing the DayAdapter object
     * 
     * @param theSchedule schedule object to update with.
     */
    public void updateSchedule(Schedule theSchedule) {

        schedule = theSchedule;

        dayAdapt.setSchedule(schedule);
        dayAdapt.notifyDataSetChanged();
    }
}
