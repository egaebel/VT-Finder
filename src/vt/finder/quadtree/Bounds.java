package vt.finder.quadtree;

import vt.finder.schedule.Point;


/**
 * wrapper class to hold the maximum coordinate bounds, and minimum coordinate
 * bounds and allow access to them in an object that can be passed as one
 * 
 * @author ethan
 * 
 */
public class Bounds {

    // ~DataFields-------------------------------------------------------------
    /**
     * holds x,y cords of maximum point
     */
    private Point max;

    /**
     * holds x,y cords of minimum point
     */
    private Point min;

    // ~Constructors-----------------------------------------------------------
    /**
     * initializes the x and y values of the largest and smallest bounds
     * 
     * @param newMaxX
     *            the maximum x value
     * @param newMaxY
     *            the maximum y value
     * @param newMinX
     *            the minimum x value
     * @param newMinY
     *            the minimum y value
     */
    public Bounds(int newMaxX, int newMaxY, int newMinX, int newMinY) {

        max = new Point(newMaxX, newMaxY);

        min = new Point(newMinX, newMinY);
    }

    /**
     * initializes the max and min points to passed in point objects
     * 
     * @param newMax
     *            new maximum point value
     * @param newMin
     *            new minimum point value
     */
    public Bounds(Point newMax, Point newMin) {

        max = newMax;
        min = newMin;
    }

    /**
     * determines if two BOunds objects are equal
     * 
     * @param thing
     *            the Bounds Object to compare this one to
     * @return value true if equal, false otherwise
     */
    @Override
    public boolean equals(Object thing) {

        boolean value = false;

        if (thing != null) {

            if (thing instanceof Bounds) {

                if (((Bounds) thing).getMax().equals(this.max)
                        && ((Bounds) thing).getMin().equals(this.min)) {

                    value = true;
                }
            }
        }

        return value;
    }

    // ~Setters/Getters--------------------------------------------------------
    /**
     * returns the value of the maximum Point (max)
     * 
     * @return max the Point holding the maximum point
     */
    public Point getMax() {

        return max;
    }

    /**
     * returns the value of the minimum point
     * 
     * @return min the POint holding the minimum point
     */
    public Point getMin() {

        return min;
    }

    /**
     * @return the x coordinate of the maximum point
     */
    public int getMaxX() {

        return max.getX();
    }

    /**
     * @return the y coordinate of the maximum point
     */
    public int getMaxY() {

        return max.getY();
    }

    /**
     * @return the x coordinate of the minimum point
     */
    public int getMinX() {

        return min.getX();
    }

    /**
     * @return the y coordinate of the minimum point
     */
    public int getMinY() {

        return min.getY();
    }
    
    /**
     * prints out the maximum and minimum point values
     * @return String the maximum and minimum point values
     */
    @Override
    public String toString() {

        return "max: " + this.getMax().toString() + " min: "
                + this.getMin().toString();
    }

}