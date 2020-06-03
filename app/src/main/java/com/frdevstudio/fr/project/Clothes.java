package com.frdevstudio.fr.project;

import android.graphics.Bitmap;

import java.util.Date;

public class Clothes {
    private String mId;
    private String mTag;
    private String mCategory;
    private String mSubCategory;
    private Date mCreateTime;
    private int mColor;
    private String mDownloadUri;

    public Clothes(){ }

    public Clothes(String id,String tag, String category, String subCategory, Date date, int color, String downloadUri){
        mId=id;
        mTag=tag;
        mCategory=category;
        mSubCategory=subCategory;
        mCreateTime=date;
        mColor=color;
        mDownloadUri=downloadUri;
    }

    public String getmId(){ return mId; }
    public String getmTag() { return mTag; }
    public String getmCategory() { return mCategory; }
    public String getmSubCategory() { return mSubCategory; }
    public int getmColor() { return mColor; }
    public Date getmCreateTime() { return mCreateTime; }
    public String getmDownloadUri() { return mDownloadUri; }
}