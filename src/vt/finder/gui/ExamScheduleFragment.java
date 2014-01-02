package vt.finder.gui;

import java.util.ArrayList;
import vt.finder.schedule.Course;
import vt.finder.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.actionbarsherlock.app.SherlockListFragment;


public class ExamScheduleFragment extends SherlockListFragment {

    //~Constants----------------------------------------------


    //~Data Fields--------------------------------------------


    //~Constructors--------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
    
        super.onCreate(savedInstanceState);
        onCreateView(getLayoutInflater(savedInstanceState), (ViewGroup) this.getView(), savedInstanceState);
        
        ArrayList<Course> finalsList = getArguments().getParcelableArrayList("finalsList");
        
        //TODO remove if block later
        if (finalsList == null) {
            finalsList = new ArrayList<Course>();
        }
        //TODO if block is now wrapped!!!^^^^^^
        
        setListAdapter(new ArrayAdapter<Course>(getSherlockActivity(),
                R.layout.list_view_child, finalsList));
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.exam_schedule_fragment_layout, container, false);
        view.setBackgroundColor(this.getResources().getColor(R.color.maroon));
        return view;
    }
    //~Methods-------------------------------------------------
    
    public void updateExamList(ArrayList<Course> finals) {
        
        setListAdapter(new ArrayAdapter<Course>(getSherlockActivity(),
                R.layout.list_view_child, finals));
    }
}
