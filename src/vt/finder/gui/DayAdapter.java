package vt.finder.gui;

import vt.finder.schedule.Course;
import vt.finder.schedule.Schedule;
import vt.finder.R;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * PageAdapter class, provides functionality for switching between days of
 * the week.
 * 
 * @author Ethan Gaebel (egaebel)
 *
 */
public final class DayAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener, OnItemClickListener {
    
    //~Data Fields----------------------------------------//
    /**
     * The list adapter that the current list is being handed to.
     */
    private Schedule schedule;
    /**
     * The current index of the day that is to be displayed.
     */
    private int currentIndex;
    /**
     * ArrayAdpter to be used to connect data to all of the list views.
     */
    private ArrayAdapter<Course> adapter;
    /**
     * Instance of the interface that allows for 
     * communication between the fragment and the activity.
     */
    private CourseComm comm;
    
    //~Constructors---------------------------------------//
    /**
     * Constructor for the DayAdapter implementation of PagerAdapter. Takes in a
     * Schedule object which is to be the data backing of the views displayed.
     * 
     * @param theSChedule the Schedule object that is the backing for the ListViews.
     * @param theComm communicator used to relay
     */
    public DayAdapter(Schedule theSchedule, CourseComm theComm) {
        
        schedule = theSchedule;
        currentIndex = schedule.getTodayIndex();
        comm = theComm;
    }

    //~Methods--------------------------------------------//
    public void setSchedule(Schedule theSchedule) {

        schedule = theSchedule;
        adapter.clear();
        adapter.notifyDataSetChanged();
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int index) {

        ListView view = new ListView(container.getContext(), null, R.style.AppTheme);

        adapter = new ArrayAdapter<Course>(container.getContext(), 
                R.layout.list_view_child, schedule.getDay(index).getList());
        view.setAdapter(adapter);
        view.setOnItemClickListener(this);

        ((ViewPager) container).addView(view);
        
        return view;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        
        ViewPager pager = (ViewPager) container;
        View view = (View) object;

        pager.removeView(view);
    }
    
    /**
     * Gets the pageTitle, which is the name of the day that is at position.
     * 
     * @param position the index of the day selected.
     * @return the name of the day the index refers to.
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return schedule.getDay(position).getThisDay();
    }
    
    @Override
    public int getCount() {
        
        if (schedule.getAnyDay().getList().isEmpty()) {            
            return 5;
        }
        else {
            return 6;
        }
    }
    
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {        
    }

    @Override
    public void onPageSelected(int index) {}
    
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentIndex = position;
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

        if (schedule.getDay(currentIndex) != null) {
            comm.setCourse(schedule.getDay(currentIndex).getList().get(position));
            //TODO do I need the below??
    //        if (selection.getBuilding().equals("EMPOR")) {
    //
    //
    //        }
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}