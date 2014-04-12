package vt.finder.gui.fragments;

import vt.finder.gui.CourseComm;
import vt.finder.gui.DayAdapter;
import vt.finder.schedule.Schedule;
import vt.finder.R;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.viewpagerindicator.TitlePageIndicator;


public class FreeTimeFragment extends SherlockFragment {

    //~Constants----------------------------------------------


    //~Data Fields--------------------------------------------
    /**
     * Adapter used for swiping between days.
     */
    private ViewPager dayPage;
    /**
     * The <STYLE>PageIndicator that makes the nice interface at the top for the ViewPager.
     */
    private TitlePageIndicator dayIndicator;
    /**
     * The DayAdapter that is used to switch between course Lists for each day for the listView.
     */
    private DayAdapter dayAdapt;
    /**
     * The schedule passed to this fragment.
     */
    private Schedule freeTime;

    //~Life Cycle Methods--------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
    
        //Setup view
        super.onCreate(savedInstanceState);

        //get schedule from parcelable
        freeTime = getArguments().getParcelable("freeTime");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.free_time_fragment_layout, null, false);
        view.setBackgroundColor(this.getResources().getColor(R.color.maroon));
        return view;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        
        super.onViewCreated(view, savedInstanceState);
        
        //Get dayPage ViewPager from layout xml code
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.linear_layout);
        dayPage = (ViewPager) layout.findViewById(R.id.free_time_day_pager);
        dayIndicator = (TitlePageIndicator) layout.findViewById(R.id.titles);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        
        super.onActivityCreated(savedInstanceState);
        
        dayAdapt = new DayAdapter(freeTime, (CourseComm) this.getActivity());
        dayPage.setAdapter(dayAdapt);
        dayIndicator.setViewPager(dayPage, freeTime.getTodayIndex());
        dayIndicator.setFooterColor(this.getResources().getColor(R.color.orange));
    }
    
    //~Methods----------------------------------------
    /**
     * Updates the schedule stored in here, as well as the schedule stored in dayAdapt.
     *   
     * @param theSchedule the Schedule object to update to.
     */
    public void updateSchedule(Schedule theSchedule) {
        
        freeTime = theSchedule;
        dayAdapt.setSchedule(freeTime);
        dayAdapt.notifyDataSetChanged();
    }
}