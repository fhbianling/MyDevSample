package com.bian.image.selector.internal.bean;

/**
 * Image bean
 * Created by Yancy on 2015/12/2.
 */
public class Image {

    public String path;

    public Image(String path) {
        this.path = path;
    }

    public Image() {
    }

    @Override
    public boolean equals(Object o) {
        try {
            Image other = (Image) o;
            return this.path.equalsIgnoreCase(other.path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }
}