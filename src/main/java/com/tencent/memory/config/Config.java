package com.tencent.memory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component //使用@Component是让该类能够在其他地方被依赖使用，即使用@Autowired注释来创建实例
@ConfigurationProperties(prefix = "config")
public class Config {

    public static final long HOUR = 3600;
    public static final long DAY = HOUR * 24;

    public static final long appId = 10132346;
    public static final String secretId = "AKIDfQJ5xiZp7FBwY04MGzXv891jNp6Fwzb2";
    public static final String secretKey = "QfkmeLqCNNDuSp12xiIiU7ZszwjKjwzr";
    public static final String tokenSecretKey = "QfkmeLCNNDSp12iIiU7ZswjKjwzr";
    public static final String inviteSecreateKey = "jA8/}~-)n?'z#!`+jdha";
    public static final String bucketName = "image";
    public static final String bucketNameWithId = "image-1251023989";

    public static final String uploadPrefix = "image/";
    public static final String bucketPathPrefix = "https://" + bucketNameWithId + ".cosgz.myqcloud.com/" + uploadPrefix;
    public static final String bucketPathCdnPrefix = "https://" + bucketNameWithId + ".file.myqcloud.com/" + uploadPrefix;


    public static final String qqAndroidAppId = "1106924490";
    public static final String qqAndroidAppKey = "x7eg1wJDzGOifzo6";

    public static final String qqIosAppId = "1106849099";
    public static final String qqIosAppKey = "oC2PgYz6lOkzalLh";
}
