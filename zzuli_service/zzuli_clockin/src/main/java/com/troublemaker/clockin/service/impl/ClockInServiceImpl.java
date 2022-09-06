package com.troublemaker.clockin.service.impl;


import com.troublemaker.clockin.config.InputDataConfiguration;
import com.troublemaker.clockin.entity.*;
import com.troublemaker.clockin.service.ClockInService;
import org.apache.http.Header;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.toJSONString;
import static com.troublemaker.clockin.ClockInRun.startTime;
import static com.troublemaker.utils.httputils.HttpClientUtils.*;

/**
 * @author Troublemaker
 * @date 2022- 04 28 21:08
 */
@Service
public class ClockInServiceImpl implements ClockInService {

    private final InputDataConfiguration inputDataConfig;

    @Autowired
    public ClockInServiceImpl(InputDataConfiguration inputDataConfig) {

        this.inputDataConfig = inputDataConfig;
    }

    @Override
    public String getCodeLink(CloseableHttpClient client, String url) {
        String entityStr = doGetForEntity(client, url);
        return Jsoup.parse(entityStr).select("a[data-href]").attr("data-href");
    }

    @Override
    public String getToken(CloseableHttpClient client, String url) {
        String headers = doGetForHeaders(client, url);
        String subHeader = headers.substring(0, headers.indexOf(";"));
        String token = subHeader.substring(subHeader.indexOf("="));
        return token.substring(1);
    }

    @Override
    public SchoolInputData getSchoolInfoFromServer(CloseableHttpClient client, String url) {
        String entityStr = doGetForEntity(client, url);
        return parseObject(entityStr, SchoolInputData.class);
    }

    @Override
    public HomeInputData getHomeInfoFromServer(CloseableHttpClient client, String url) {
        String entityStr = doGetForEntity(client, url);
        return parseObject(entityStr, HomeInputData.class);
    }

    @Override
    public SchoolInputData SchoolFinalData(SchoolInputData schoolInputData, School school) {
        if (school != null) {
            schoolInputData.setRegion(school.getRegion());
            schoolInputData.setArea(school.getArea());
            schoolInputData.setBuild(school.getBuild());
            schoolInputData.setDorm(school.getDorm());
            schoolInputData.setMobile(school.getMobile());
            schoolInputData.setJtMobile(school.getJtMobile());
            schoolInputData.setLat(school.getLat());
            schoolInputData.setLon(school.getLon());
            schoolInputData.setGcjLat(school.getLat());
            schoolInputData.setGcjLon(school.getLon());
            schoolInputData.setJzAddress(school.getJzAddress());
            schoolInputData.setJzProvince(school.getJzProvince());
            schoolInputData.setJzCity(school.getJzCity());
            schoolInputData.setJzDistrict(school.getJzDistrict());
        }
        schoolInputData.setLastTime(inputDataConfig.getCommon().getLastTime());
        return schoolInputData;
    }

    @Override
    public HomeInputData HomeFinalData(HomeInputData homeInputData, Home home) {
        if (home != null) {
            homeInputData.setMobile(home.getMobile());
            homeInputData.setJtMobile(home.getJtMobile());
            homeInputData.setLat(home.getLat());
            homeInputData.setLon(home.getLon());
            homeInputData.setGcjLat(home.getLat());
            homeInputData.setGcjLon(home.getLon());
            homeInputData.setJzAddress(home.getJzAddress());
            homeInputData.setJzProvince(home.getJzProvince());
            homeInputData.setJzCity(home.getJzCity());
            homeInputData.setJzDistrict(home.getJzDistrict());
        }
        homeInputData.setLastTime(inputDataConfig.getCommon().getLastTime());
        return homeInputData;
    }

    @Override
    public String submitData(CloseableHttpClient client, String url, Object data, Header header) {
        String params = toJSONString(data);
        //线程抵达时间
        long endTime = System.currentTimeMillis();
        //从程序启动到线程抵达所用时间与服务器的时时间的时差
        long difference = timeDifference(url) - (endTime - startTime);
        try {
            //比服务器慢多少就睡眠多少
            //否则直接执行
            Thread.sleep(difference > 0 ? difference : 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return doJsonPostWithHeader(client, url, params, header);
    }
}
