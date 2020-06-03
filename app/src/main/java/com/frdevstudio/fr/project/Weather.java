package com.frdevstudio.fr.project;

public class Weather {
    private String mHeadlineText;
    private String mCategory;
    private String mDayIconPhrase;
    private int mSeverity;
    private int mIcon;
    private long mMinTemp;
    private long mMaxTemp;

    public Weather(String hText,String cat, String dayIconPh, int sev,int icon, long minT, long maxT){
        mHeadlineText =hText;
        mCategory =cat;
        mDayIconPhrase =dayIconPh;
        mSeverity =sev;
        mIcon=icon;
        mMinTemp =minT;
        mMaxTemp =maxT;
    }
    public String getmHeadlineText() { return mHeadlineText; }
    public String getmCategory() { return mCategory; }
    public String getmDayIconPhrase() { return mDayIconPhrase; }
    public int getmIcon() { return mIcon; }
    public int getmSeverity() { return mSeverity; }
    public long getmMinTemp() { return mMinTemp; }
    public long getmMaxTemp() { return mMaxTemp; }
}
