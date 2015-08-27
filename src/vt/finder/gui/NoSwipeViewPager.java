package vt.finder.gui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;


/**
 * Implementation of ViewPager that is identical to the original, except that
 * the swiping capability is disabled.
 * 
 * @author Ethan Gaebel (egaebel)
 *
 */
public class NoSwipeViewPager extends ViewPager {

    //~Constructors--------------------------------------------
    public NoSwipeViewPager(Context context) {

        super(context);
        Log.i("NO SWIPE VIEW PAGER", "Constructor called");
    }

    //~Methods-------------------------------------------------
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        return false;
    }
    
    @Override
    public boolean onInterceptTouchEvent (MotionEvent event) {
        
        return false;
    }
}
