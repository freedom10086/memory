package com.tencent.memory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.memory.config.Config;
import com.tencent.memory.model.MyException;
import com.tencent.memory.model.QQUserInfo;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QQTest {

    @Test
    public void testQQLogin() {
        String openId = "233E8232A49AC1E46BF51B3F5AD36EFE";
        String accessToken = "3A1476ADF5AFD0058E76CC518E7D40B8";

        OkHttpClient client = new OkHttpClient();
        String url = String.format("https://graph.qq.com/user/get_simple_userinfo?" +
                        "access_token=%s&" +
                        "oauth_consumer_key=%s&" +
                        "openid=%s&format=json",
                accessToken, Config.qqIosAppId, openId);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);

        Response response;
        try {
            response = call.execute();
            if (response.isSuccessful()) {
                QQUserInfo userInfo = new ObjectMapper().readValue(response.body().bytes(), QQUserInfo.class);
                if (userInfo.ret == 0) {
                    System.out.println(userInfo.toString());
                } else {
                    throw new MyException(HttpStatus.UNAUTHORIZED, userInfo.msg);
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


}
