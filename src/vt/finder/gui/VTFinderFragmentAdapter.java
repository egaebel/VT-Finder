package vt.finder.gui;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;

/**
 * Class that handles switching between tabs/fragments and clicking of Tabs
 * to perform the switching. 
 * 
 * @author Ethan Gaebel (egaebel)
 *
 */
public final class VTFinderFragmentAdapter 
	extends FragmentPagerAdapter 
    implements ActionBar.TabListener, 
    	ViewPager.OnPageChangeListener {

	//~Constants-----------------------------------------------------------------------------
	private static final String TAG = "VT Finder Fragment Adapter";
	
	//~Data Fields-----------------------------------------------------------------------------
    /**
     * ArrayList of TabInfo used to retrieve the appropriate Fragment when a tab is clicked.
     */
    private final ArrayList<TabInfo> theTabs = new ArrayList<TabInfo>();
    /**
     * The ViewPager as it was retrieved from the enclosing Activity to use to
     * switch between tabs/fragments.
     */
    private final ViewPager viewPager;
    /**
     * The ActionBar as it was retrieved from the enclosing activity to use to
     * hold the navigation tabs.
     */
    private final ActionBar actionBar;
    /**
     * The context of the enclosing activity.
     */
    private final Context context;
    private final SherlockFragmentActivity activity;
    //~Constructors--------------------------------------------------//
    /**
     * Only constructor. Takes in a SherlockFragment activity (for compatibility)
     * and a ViewPager associated with the application and stores them within this
     * sub-class and uses them to get the FragmentManager, context, and ActionBar, as well
     * as set the adapter and onPageChangeListener for the ViewPager.
     * 
     * @param activity the main activity.
     * @param pager the ViewPager for the activity.
     */
    public VTFinderFragmentAdapter(SherlockFragmentActivity activity, ViewPager pager) {

        super(activity.getSupportFragmentManager());
        
        context = activity.getBaseContext();
        actionBar = activity.getSupportActionBar();
        viewPager = pager;
        viewPager.setAdapter(this);
        viewPager.setOnPageChangeListener(this);
        
        this.activity = activity;
    }

    //~Methods--------------------------------------------------------//
    /**
     * Takes in an ActionBar.Tab, a Class, and a bundle and creates a new tab
     * object that is identified by a TabInfo object created here which holds the
     * className that the tab switches to.
     * 
     * @param tab the ActionBar.Tab to use.
     * @param theClass the class the ActionBar.Tab switches to.
     * @param args the arguments for this (usually null....)
     */
    public void addTab(ActionBar.Tab tab, Class<?> theClass, Bundle args) {
        
        TabInfo info = new TabInfo(theClass, args);
        tab.setTag(info);
        tab.setTabListener(this);
        theTabs.add(info);
        actionBar.addTab(tab);
        this.notifyDataSetChanged();
        //Log.i(TAG, "FRAGMENT ADAPTER onStart: " + activity.getSupportFragmentManager().getFragments());
    }
    
    /**
     * Gets the fragment at the passed in index.
     * 
     * @param index the index of the Fragment to be retrieved.
     * @return an instance of the Fragment identified by the passed index.
     */
    @Override
    public Fragment getItem(int index) {

        TabInfo info = theTabs.get(index);

        return Fragment.instantiate(context, info.classType.getName(), info.getArgs());
    }

    /**
     * Method called when the passed in tab is selected. Switches
     * to the fragment identified by the tab.
     * 
     * @param tab the tab that was clicked 
     * @param ft the type of transaction that is to occur???   
     */
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {

        Object tag = tab.getTag();
        for (int i = 0; i < theTabs.size(); i++) {
            
        	if (theTabs.get(i).equals(tag)) {

        		this.setPrimaryItem(viewPager, i, this.getItem(i));
                viewPager.setCurrentItem(i);
                break;
            }

        }
    }
    
    /**
     * Gets the tag used to denote the Fragment at the passed in index. This tag can be plugged
     * into a FragmentManager to get the actual fragment.
     * 
     * @param index the index of the fragment to grab.
     * @return the tag of the Fragment denoted by the index.
     */
    public String getFragmentTag(int index) {
        
       return "android:switcher:" + viewPager.getId() + ":" + index;
   }

    /**
     * Getter for the number of tabs (represented by TabInfo objects) stored here.
     * 
     * @return the number of tabs.
     */
    @Override
    public int getCount() {

        return theTabs.size();
    }

    @Override
    public void startUpdate(ViewGroup container) {
        super.startUpdate(container);
        //Log.i(TAG, "startUpdate ViewGroup: " + container.getId());
        //Log.i(TAG, "FRAGMENT ADAPTER startUpdate: " + activity.getSupportFragmentManager().getFragments());
    }
    
    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
        //Log.i(TAG, "FRAGMENT ADAPTER finishUpdate: " + activity.getSupportFragmentManager().getFragments());        
    }
    
    /**
     * Takes in an index and sets the tab identified by the index  
     * to be the active tab.
     * 
     * @param index the index of the ActionBar.Tab to set as active. 
     */
    @Override
    public void onPageSelected(int index) {

        actionBar.setSelectedNavigationItem(index);
        
    }
    
    //~Unused Methods---------------------------------------------//
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }
    
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }
    
    /**
     * Gets an instance of a TabInfo object which represents a class of the 
     * passed kind.
     * 
     * @param classType
     * @return TabInfo object
     */
    public TabInfo getTabInfo(Class<?> classType) {
        
        return new TabInfo(classType, null);
    }
    
    //~Sub-classes--------------------------------------------------//
    /**
     * Class used to denote tabs. Performs this by comparing the type of
     * class that a Tab switches to (which should be some sort of Fragment).
     * 
     * @author Ethan Gaebel (egaebel)
     *
     */
    static final class TabInfo {
        
        //~Data Fields---------------//
        /**
         * The classType that a tab switches to.
         */
        private final Class<?> classType;
        /**
         * The arguments to store that are asoociated with a Tab.
         */
        private final Bundle args;
        
        //~Constructors--------------//
        /**
         * Only constructor, takes a Class<?> object and sets 
         * classType to it.
         * 
         * @param theClassType the type of class that this TabInfo applies to.
         * @param theArgs a bundle holding the arguments (if any) associated with 
         *          this TabInfo's Tab.
         */
        public TabInfo(Class<?> theClassType, Bundle theArgs) {
            
            classType = theClassType;
            args = theArgs;
        }
        
        /**
         * Getter method for the Bundle args stored here.
         * 
         * @return args a Bundle of arguments.
         */
        public Bundle getArgs() {
            
            return args;
        }
        
        /**
         * Getter for the classType. 
         * Used to compare TabInfo objects.
         * 
         * @return classType.
         */
        public Class<?> getClassType() {
            
            return classType;
        }
        
        @Override
        public boolean equals(Object o) {
            
            if (o instanceof TabInfo) {
                
                if (this.getClassType().equals(((TabInfo) o).getClassType())) {
                    
                    return true;
                }
            }
            
            return false;
        }
    }
}