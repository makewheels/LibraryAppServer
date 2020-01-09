/**
  * Copyright 2020 bejson.com 
  */
package com.eg.libraryappserver.book.bean.douban;

/**
 * Auto-generated: 2020-01-09 15:44:3
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Rating {

    private int max;
    private int numRaters;
    private String average;
    private int min;
    public void setMax(int max) {
         this.max = max;
     }
     public int getMax() {
         return max;
     }

    public void setNumRaters(int numRaters) {
         this.numRaters = numRaters;
     }
     public int getNumRaters() {
         return numRaters;
     }

    public void setAverage(String average) {
         this.average = average;
     }
     public String getAverage() {
         return average;
     }

    public void setMin(int min) {
         this.min = min;
     }
     public int getMin() {
         return min;
     }

}