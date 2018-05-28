package com.tencent.memory.model;

public class QQUserInfo {
    public int ret;
    public String msg;
    public int is_lost;
    public String nickname;
    public String gender;
    public String province;
    public String city;
    public String year;
    public String figureurl;
    public String figureurl_1;
    public String figureurl_2;
    public String figureurl_qq_1;
    public String figureurl_qq_2;
    public String is_yellow_vip;
    public String vip;
    public String yellow_vip_level;
    public String level;
    public String is_yellow_year_vip;

    @Override
    public String toString() {
        return "QQUserInfo{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                ", is_lost=" + is_lost +
                ", name='" + nickname + '\'' +
                ", gender='" + gender + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", year='" + year + '\'' +
                ", figureurl='" + figureurl + '\'' +
                ", figureurl_1='" + figureurl_1 + '\'' +
                ", figureurl_2='" + figureurl_2 + '\'' +
                ", figureurl_qq_1='" + figureurl_qq_1 + '\'' +
                ", figureurl_qq_2='" + figureurl_qq_2 + '\'' +
                ", is_yellow_vip='" + is_yellow_vip + '\'' +
                ", vip='" + vip + '\'' +
                ", yellow_vip_level='" + yellow_vip_level + '\'' +
                ", level='" + level + '\'' +
                ", is_yellow_year_vip='" + is_yellow_year_vip + '\'' +
                '}';
    }
}
