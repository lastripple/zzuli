package com.troublemaker.clockin.service;

import com.troublemaker.clockin.entity.ResultResponse;
import com.troublemaker.clockin.entity.User;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.Map;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.service
 * @Author: troublemaker
 * @CreateTime: 2022-08-27  23:49
 * @Version: 1.0
 */
public interface LoginService {

    Map<String, String> loginMap(User user, String lt);

    String getLt(CloseableHttpClient client, String url);

    String login(CloseableHttpClient client, String url, Map<String, String> map);

    boolean verifyLogin(CloseableHttpClient client, User user);

    ResultResponse verifyUserAndISToken(CloseableHttpClient client, User user);

    void cleanClient();

}
