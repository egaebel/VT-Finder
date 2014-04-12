package vt.finder.gui.fragments;

import java.util.ArrayList;

import vt.finder.R;
import vt.finder.schedule.UserMadeCourse;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.actionbarsherlock.app.SherlockFragment;

public class CreateCourseFragment extends SherlockFragment {

	
	private EditText courseName;
	private EditText courseCode;
	private EditText teacherName;
	private EditText beginTime;
	private EditText endTime;
	private EditText roomNumber;
	private Spinner buildingSpinner;
	private ArrayAdapter<CharSequence> spinnerAdapter;
	private String buildingString;
	private CheckBox mondayBox;
	private CheckBox tuesdayBox;
	private CheckBox wednesdayBox;
	private CheckBox thursdayBox;
	private CheckBox fridayBox;
	private CheckBox saturdayBox;
	private CheckBox sundayBox;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void createNewCourse(View view) {
		
        String[] courseCodeSplit = courseCode.getText().toString().trim().split(" ");
        
        if (courseName.getText().toString().trim().equals("")) {
            
            Toast.makeText(this.getSherlockActivity(), "Your course needs a name!!!", Toast.LENGTH_LONG).show();
        }
        else if (beginTime.getText().toString().trim().equals("")) {
            
            Toast.makeText(this.getSherlockActivity(), "Your course needs a starting time!!!", Toast.LENGTH_LONG).show();
        }
        else if (endTime.getText().toString().trim().equals("")) {
            
            Toast.makeText(this.getSherlockActivity(), "Your course needs an ending time!!!", Toast.LENGTH_LONG).show();
        }
        else if (!(mondayBox.isChecked() 
                || tuesdayBox.isChecked() 
                || wednesdayBox.isChecked() 
                || thursdayBox.isChecked() 
                || fridayBox.isChecked() 
                || saturdayBox.isChecked() 
                || sundayBox.isChecked())) {
            
            Toast.makeText(this.getSherlockActivity(),  
                    "Your course needs to occur on a day!", 
                    Toast.LENGTH_LONG).show();
        }
        else if (courseCodeSplit.length != 2 
                && courseCodeSplit.length != 0
                && !(courseCodeSplit.length == 1 && courseCodeSplit[0] == "")) {
            
            Toast.makeText(this.getSherlockActivity(), 
                    "Your course code must have the subject code followed" +
                    " by the course number. For example: CS 3114, CS 3214, MATH 4175", 
                    Toast.LENGTH_LONG).show();
        }
        else {
            
            String daysString = "";
            
            if (mondayBox.isChecked()) {

                daysString += "M";
            }
            if (tuesdayBox.isChecked()) {
                
                daysString += "T";
            }
            if (wednesdayBox.isChecked()) {
                
                daysString += "W";
            }
            if (thursdayBox.isChecked()) {
                
                daysString += "R";
            }
            if (fridayBox.isChecked()) {
                
                daysString += "F";
            }
            if (saturdayBox.isChecked()) {
                
                daysString += "S";
            }
            if (sundayBox.isChecked()) {
                
                daysString += "Su";
            }
            
            String subjectCode = "";
            String courseNumber = "";
            if (courseCodeSplit.length == 2) {
                subjectCode = courseCodeSplit[0];
                courseNumber = courseCodeSplit[1];
            }
            
            UserMadeCourse course = new UserMadeCourse(
                                            courseName.getText().toString(), 
                                            teacherName.getText().toString(), 
                                            subjectCode, 
                                            courseNumber, 
                                            beginTime.getText().toString(), 
                                            endTime.getText().toString(),
                                            buildingString.toString(), 
                                            roomNumber.getText().toString());
        }
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.course_addition_dialog, null);
        view.setBackgroundColor(this.getResources().getColor(R.color.maroon));
        
        //SETUP GUI ELEMENTS------------------------------------------
        courseName = (EditText) view.findViewById(R.id.courseName);
        courseCode = (EditText) view.findViewById(R.id.courseCode);
        teacherName = (EditText) view.findViewById(R.id.teacherName);

        beginTime = (EditText) view.findViewById(R.id.startTime);
        endTime = (EditText) view.findViewById(R.id.endTime);
        roomNumber = (EditText) view.findViewById(R.id.roomNumber);
        
        buildingSpinner = (Spinner) view.findViewById(R.id.building);
        spinnerAdapter = ArrayAdapter.createFromResource(this.getSherlockActivity(), 
        					R.array.buildings_array, 
        					android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buildingSpinner.setAdapter(spinnerAdapter);
        buildingString = "";
        buildingSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int pos, long id) {

                buildingString = parent.getItemAtPosition(pos).toString();
                
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

                buildingString = "";
            }});
        
        mondayBox = (CheckBox) view.findViewById(R.id.mondayBox);
        tuesdayBox = (CheckBox) view.findViewById(R.id.tuesdayBox);
        wednesdayBox = (CheckBox) view.findViewById(R.id.wednesdayBox);
        thursdayBox = (CheckBox) view.findViewById(R.id.thursdayBox);
        fridayBox = (CheckBox) view.findViewById(R.id.fridayBox);
        saturdayBox = (CheckBox) view.findViewById(R.id.saturdayBox);
        sundayBox = (CheckBox) view.findViewById(R.id.sundayBox);
        
        return view;
    }
	
	//Interface used to send data through to the Activity
	public interface ReceiveCreatedCourse {
		
		//Takes a UserMadeCourse and a String indicating the days the course occurs on and does something with them
		public void receiveCreatedCourse(UserMadeCourse course, String daysString);
	}
}
