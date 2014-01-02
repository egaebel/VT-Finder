package vt.finder.quadtree;

import java.util.ArrayList;
import java.util.Collection;
import vt.finder.schedule.Course;
import vt.finder.schedule.Point;
import android.util.Log;


/**
 * the wrapper of the Quad tree which directs traffic to the nodes of the tree
 * where all of the real logic lies
 * 
 * @author ethan
 * 
 * updated 3/22/2012
 */
public class QuadTree {

    // ~Constants--------------------------------------------------------
    /**
     * denotes the minimum unit of time that counts as chill time
     * 
     * normal time, is 30 minutes minimum, but because there are 60 mins
     * in an hour, and the quad tree goes by hundres, I must add 40 to
     * equalize it
     */
    private static final int MINIMUM_CHILL_TIME = 30;

    private static final String TAG = "QUAD_TREE";

    // ~DataFields-------------------------------------------------------------
    /**
     * the root changing bounds object which holds the bounds for the quadTree's
     * world
     */
    private ChangingBounds boundary;

    /**
     * root node of the quadTree, can be an empty node, leaf node, or internal
     * node depending on the stage the tree is in
     */
    private QuadNode root;


    // ~Constructors-----------------------------------------------------------
    /**
     * initializes the ChangingBounds object boundary, to a passed in maximum
     * point and the origin
     */
    @SuppressWarnings("static-access")
    public QuadTree(int boundX, int boundY) {

        boundary = new ChangingBounds(boundX, boundY, 0, 0);
        root = root.empty;
    }

    /**
     * initializes the ChangingBounds object boundary, to a passed in maximum
     * point and the origin
     */
    @SuppressWarnings("static-access")
    public QuadTree(int maxX, int maxY, int minX, int minY) {

        boundary = new ChangingBounds(maxX, maxY, minX, minY);
        root = root.empty;
    }

    /**
     * constructor that takes a ChangingBounds object as an argument and sets
     * the boundary ChangingObject to that value
     * 
     * @param newWorld
     *            the ChangingBounds object to set boundary to
     */
    @SuppressWarnings("static-access")
    public QuadTree(ChangingBounds newWorld) {

        boundary = newWorld;
        root = root.empty;
    }

    // ~Methods----------------------------------------------------------------

    /**
     * inserts a passed in LeafNode, if the x, y cords are within the boundaries
     * then insert, and return true, otherwise do not insert and return false
     * 
     * if the root is null the root becomes that node, otherwise recurse.
     * 
     * @param node
     *            the node to insert
     * @param x
     *            the x value of the insertion
     * @param y
     *            the y value of the insertion
     * @return value true if the insertion was within bounds, false otherwise
     */
    public boolean insert(LeafNode node) {

        boolean value = false;
        
        // if the point being inserted is within the boundaries
        if (boundary.withinBounds(node.getRecord().getCoursePoint())) {
        
            if (!(root instanceof EmptyNode)) {

                root = root.insert(node, boundary);
                value = true;
            }
            else {

                root = node;
                value = true;
            }
        }
        else {

            Log.i("FreeFinder", "is NOT within bounds!!!");
        }

        return value;
    }

    /**
     * finds a LeafNode with the same coordinates as the passed in one
     * 
     * @param node
     *            the node to find
     */
    @SuppressWarnings("static-access")
    public QuadNode find(LeafNode node) {

        // if the root is not null
        if (root != null) {

            return root.find(node, boundary);
        }
        else {

            return node.empty;
        }
    }

    /**
     * removes a passed in LeafNode
     * 
     * @param node
     *            the node to remove
     */
    public void remove(LeafNode node) {

        // must set root to result for contractions
        root = root.remove(node, boundary);
    }

    /**
     * finds all members within the box made by the passed in members, returns
     * the number of nodes looked at, and inserts values into a passed in
     * collection
     * 
     * Workshop Notes: --helper method --check for intersecting rectangles --use
     * the intersection points to determine which directions to go into --must
     * perform explicit checks in the LeafNode class at the bottom
     * 
     * @param x
     *            the x origin of the box
     * @param y
     *            the y origin of the box
     * @param width
     *            the width of the box
     * @param height
     *            the height of the box
     * @param found
     *            a Collection that will be filled with the CityData records
     *            found
     * @return the number of nodes looked at
     */
    public int rFind(int x, int y, int width, int height,
            Collection<Course> found) {

        if (!(boundary.withinBounds(new Point(x, y))
                && boundary.withinBounds(new Point(x + width, y))
                && boundary.withinBounds(new Point(x, y + height)) && boundary
                .withinBounds(new Point(x + width, y + height)))) {

            return -1;
        }
        else {

            return root.rFind(x, y, width, height, boundary, found);
        }
    }

    /**
     * finds the similar free time between two ArrayLists of Course objects and
     * returns a list of Course objects that represent that free time
     * 
     * @param points
     *            the ArrayList of Course objects that holds the points that
     *            comprise the diagonals of the search area in the quad tree
     * @return the arrayList of Course objects representing all of the areas of
     *         free time found in the QuadTree
     */
    public ArrayList<Course> freeFinder(ArrayList<Course> points) {
        
        // makes the points arrayList passed in a variable subject to change
        ArrayList<Course> varPoints = points;
        
        // temporary array that will be set to the results of root.freeFinder(
        ArrayList<Course> temp = new ArrayList<Course>();

        // return array that will hold all of the freeTimes for this set of
        // courses
        ArrayList<Course> freeTimes = new ArrayList<Course>();

        // if the tree is not empty
        if (!(root instanceof EmptyNode)) {

            // set the initial value of size to the size of varPoints
            int size = varPoints.size();

            // searches through each pair of points
            // size - 1 because I use the (i + 1)th index below
            for (int i = 0; i < size - 1; i++) {

                // recurses freeFinder method using the ith and (i + 1)th points
                // in free finder
                // if there were no other Points found within the search area
                temp = root.freeFinder(varPoints.get(i).getCoursePoint(),
                        varPoints.get(i + 1).getCoursePoint(), boundary);
                
                // if temp didnt find any points in the search area
                if (temp.size() == 0) {
                    // ***********************************************3/22/2012
                    // THE PROBLEM CAUSING STACK OVERFLOW IS TO BE SOLVED BY
                    // INCREASING THE QUAD TREE SIZE -- INCREASED QUAD TREE MULTIPLICATION FACTOR TO 10000 -- 9/22/2012
                    // ***********************************************3/22/2012

                    // if (the beginning time of the second Point - the ending
                    // time of the second Point) is at least the
                    // MINIMUM_CHILL_TIME
                    // then add the time block to the freeTimes ArrayList
                    if ((varPoints.get(i + 1).getCoursePoint().getX() - varPoints
                            .get(i).getCoursePoint().getY()) >= MINIMUM_CHILL_TIME) {
                        
                        // insert the time intervals just searched
                        // go from the end of the first to the beginning of the
                        // second
                        freeTimes.add(new Course("Free Time", varPoints
                                .get(i).getEndTime(), varPoints.get(i + 1)
                                .getBeginTime()));
                    }
                }
                // temp DID find points in the search area, points must be added
                // to varPoints
                else {

                    // add the points found in the search area to the boundaries
                    // arrayList starting at the index (i + 1)
                    varPoints.addAll(i + 1, temp);

                    // increment size to account for new points in search area
                    // boundaries
                    size += temp.size();

                    // decrements i so that the bounds will be from last i to
                    // new i+1
                    i--;
                }

                // reset temp to null for next round of setting
                temp = new ArrayList<Course>();
            }
        }

        // return the ArrayList with free times
        return freeTimes;
    }

    /**
     * prints out a string representation of the QuadTree in PRE-Order; Quad
     * tree children in, NW, NE, SW, SE each node prints on a separate line
     * --depth i, prints in character column 4i --starts at 0 --Internal node,
     * print INTERNAL followed by x,y cords of the CENTER SPLIT POINT that
     * determines the quadrants of 4 children --for an empty node print empty --
     * non empty leaf node print the city record --format???
     * 
     * @return the String representation of the QuadTree
     */
    public String list() {

        // if the root is an empty node then the database is effectively
        // empty, alter output to refelct this fact instead of merely printing
        // the toString of an emptyNode
        // otherwise recurse
        if (root instanceof EmptyNode) {

            return "\n<no records>";
        }
        else {

            return root.list(boundary, "");
        }
    }

    /**
     * resets the root node of the tree to an empty node which effectively
     * "empties" the tree
     */
    @SuppressWarnings("static-access")
    public void makeNull() {

        root = root.empty;
    }
}
