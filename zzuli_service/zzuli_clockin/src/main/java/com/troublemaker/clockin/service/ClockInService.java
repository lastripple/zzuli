package com.troublemaker.clockin.service;

import com.troublemaker.clockin.entity.*;
import org.apache.http.Header;
import org.apache.http.impl.client.CloseableHttpClient;



/**
 * @author Troublemaker
 * @date 2022- 04 28 21:08
 */
public interface ClockInService {

    String getCodeLink(CloseableHttpClient client, String url);

    String getToken(CloseableHttpClient client, String url);

    SchoolInputData getSchoolInfoFromServer(CloseableHttpClient client, String url);

    HomeInputData getHomeInfoFromServer(CloseableHttpClient client, String url);

    SchoolInputData SchoolFinalData(SchoolInputData schoolInputData, School school);

    HomeInputData HomeFinalData(HomeInputData homeInputData, Home home);

    String submitData(CloseableHttpClient client, String url, Object data, Header header);
}
