package com.qcloud.image;

public class PornDetectData {
    public int result; //0 正常，1 黄图，2 疑似图片

    //封禁状态，0 表示正常，1 表示图片已被封禁（只有存储在腾讯云的图片才会被封禁）
    public int forbid_status;

    //识别为黄图的置信度，分值 0-100 ；是 normal_score , hot_score , porn_score 的综合评分
    public float confidence;

    //图片为性感图片的评分
    public float hot_score;

    //图片为正常图片的评分
    public float normal_score;

    //图片为色情图片的评分
    public float porn_score;
}
