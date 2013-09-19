package com.wit.page2reader.model;

import java.util.ArrayList;

import com.wit.page2reader.Constants.PageUrlObjStatus;

@SuppressWarnings("serial")
public class PageUrlArrayList extends ArrayList<PageUrlObj> {

    /**
     * Count of elements with specific status.
     */
    public int sizeWithStatus(PageUrlObjStatus status) {
        int size = 0;
        for (PageUrlObj pageUrlObj : this) {
            if (pageUrlObj.status == status) {
                size += 1;
            }
        }
        return size;
    }
    
    public int sizeWithNormalStatus() {
        return sizeWithStatus(PageUrlObjStatus.NORMAL);
    }
    
    public boolean isEmptyWithStatus(PageUrlObjStatus status) {
        return sizeWithStatus(status) == 0;
    }
    
    public boolean isEmptyWithNormalStatus() {
        return sizeWithStatus(PageUrlObjStatus.NORMAL) == 0;
    }

    /**
     * Get an element with specific status.
     */
    public PageUrlObj getWithStatus(int index, PageUrlObjStatus status) {
        int i = 0;
        for (PageUrlObj pageUrlObj : this) {
            if (pageUrlObj.status == status) {
                if (i == index) {
                    return pageUrlObj;
                }
                i += 1;
            }
        }
        return null;
    }
    
    public PageUrlObj getWithNormalStatus(int index) {
        return getWithStatus(index, PageUrlObjStatus.NORMAL);
    }

    public int indexOfWithStatus(PageUrlObj obj, PageUrlObjStatus status) {
        int i = 0;
        for (PageUrlObj pageUrlObj : this) {
            if (pageUrlObj.status == status) {
                if (pageUrlObj.equals(obj)) {
                    return i;
                }
                i += 1;
            }
        }
        return -1;
    }
    
    public int indexOfWithNormalStatus(PageUrlObj obj) {
        return indexOfWithStatus(obj, PageUrlObjStatus.NORMAL);
    }
}
