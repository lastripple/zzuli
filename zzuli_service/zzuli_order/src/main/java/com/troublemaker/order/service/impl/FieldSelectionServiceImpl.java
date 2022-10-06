package com.troublemaker.order.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.troublemaker.order.entity.FieldInfo;
import com.troublemaker.order.entity.FieldType;
import com.troublemaker.order.entity.Booker;
import com.troublemaker.order.entity.TimePeriod;
import com.troublemaker.order.exception.MyException;
import com.troublemaker.order.mapper.BookerMapper;
import com.troublemaker.order.service.FieldSelectionService;

import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static com.alibaba.fastjson.JSON.*;
import static com.alibaba.fastjson.JSON.parseObject;
import static com.troublemaker.order.OrderRun.startTime;
import static com.troublemaker.utils.encryptionutils.EncryptionUtil.getBase64Password;
import static com.troublemaker.utils.httputils.HttpClientUtils.*;

/**
 * @author Troublemaker
 * @date 2022- 04 25 17:55
 */
@Service
public class FieldSelectionServiceImpl extends ServiceImpl<BookerMapper, Booker> implements FieldSelectionService {

    @Override
    public Integer addBooker(Booker booker) {
        return baseMapper.insert(booker);
    }

    @Override
    public List<Booker> getBookers() {
        return baseMapper.selectList(null);
    }

    @Override
    public Map<String, String> loginMap(Booker booker, String lt) {
        //Base64加密
        booker.setPassword(getBase64Password(booker.getPassword()));
        //转为map
        HashMap<String, String> loginMap = new HashMap<>(1);
        loginMap.put("username", booker.getUsername());
        loginMap.put("password", booker.getPassword());
        loginMap.put("secret", "");
        loginMap.put("accountLogin", "");
        loginMap.put("lt", lt);
        loginMap.put("execution", "e2s1");
        loginMap.put("_eventId", "submit");
        return loginMap;
    }

    public String getHost(CloseableHttpClient client, String url) {
        String host = null;
        String entityStr = doGetForEntity(client, url);

        String[] split = Objects.requireNonNull(Jsoup.parse(entityStr).select("script").first()).toString().split("var");
        for (String s : split) {
            if (s.contains("__vpn_app_hostname_data")) {
                host = s;
            }
        }
        if (host == null) {
            throw new RuntimeException("没有获取到Host");
        } else return host.split("\"")[1];

    }

    @Override
    public String getLt(CloseableHttpClient client, String url) {
        String entityStr = doGetForEntity(client, url);
        return Jsoup.parse(entityStr).select("[name='lt']").attr("value");
    }

    @Override
    public void login(CloseableHttpClient client, String url, Map<String, String> map) {
        doApplicationPost(client, url, map);
    }

    @Override
    public String getOrderHost(CloseableHttpClient client) {
        String host = null;
        String url = "https://webvpn.zzuli.edu.cn/user/portal_groups?_t=" + System.currentTimeMillis();
        String entityStr = doGetForEntity(client, url);
        JSONObject jsonObject = parseObject(entityStr);
        JSONArray data = jsonObject.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            JSONArray resource = data.getJSONObject(i).getJSONArray("resource");
            for (int j = 0; j < resource.size(); j++) {
                // 科学校区体育馆预约
                if (resource.getJSONObject(j).getString("name").equals("科学校区体育馆预约")) {
                    host = resource.getJSONObject(j).getString("redirect");
                }
            }
        }
        return Objects.requireNonNull(host).split("/")[2];
    }

    @Override
    public void getHomePage(CloseableHttpClient client, String url) {
        doGetForEntity(client, url);
    }

    @Override
    public List<FieldInfo> getOptionalFieldInfo(CloseableHttpClient client, FieldType fieldTypeNo, TimePeriod timePeriod) throws NullPointerException {
        String selectUrl = "http://cgyy.zzuli.edu.cn/Field/GetVenueState?dateadd=0&TimePeriod=" +
                timePeriod.getTimePeriodNo() +
                "&VenueNo=001&FieldTypeNo=" +
                fieldTypeNo.getFieldTypeNo() +
                "&_=" + System.currentTimeMillis();
        String entityStr = doGetForEntity(client, selectUrl);
        String resultData = parseObject(entityStr).getString(("resultdata"));
        List<FieldInfo> list = parseArray(resultData, FieldInfo.class);
        list.removeIf(fieldInfo -> fieldInfo.getFieldState() != 0 || fieldInfo.getTimeStatus() != 1);
        if (list.size() == 0) {
            throw new NullPointerException("当前无可选场所，请重试。");
        }
        return list;
    }

    @Override
    public String objToJsonString(FieldInfo fieldInfo) {
        String selectStr = null;
        String str = "[" + JSONObject.toJSONString(fieldInfo) + "]";
        try {
            selectStr = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return selectStr;
    }

    @Override
    public String orderInvariableField(FieldInfo fieldInfo) {
        return objToJsonString(fieldInfo);
    }

    @Override
    public String orderChangeableField(List<FieldInfo> list) {
        return objToJsonString(list.get(list.size() - 1));
    }

    @Override
    public String order(CloseableHttpClient client, String url) throws MyException {
        long endTime = System.currentTimeMillis();
        long difference = timeDifference(url) - (endTime - startTime);
        try {
            Thread.sleep(difference > 0 ? difference : 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String jsonStr = doGetForEntity(client, url);
        JSONObject jsonObject = parseObject(jsonStr);
        String resultData = "resultdata";
        if (jsonObject.get(resultData) == null) {
            throw new MyException("message: " + jsonObject.get("message"));
        }
        return jsonObject.get(resultData).toString();
    }

    @Override
    public String subMit(CloseableHttpClient client, String url) {
        JSONObject jsonObject = parseObject(doGetForEntity(client, url));
        return jsonObject.get("message").toString();
    }

    @Override
    public String getOrdered(CloseableHttpClient client) {
        String url = "http://cgyy.zzuli.edu.cn/Field/GetFieldOrder?PageNum=1&PageSize=6&Condition=" +
                "&_=" + System.currentTimeMillis();
        String entityStr = doGetForEntity(client, url);
        JSONObject jsonObject = parseObject(entityStr);
        JSONArray datatable = (JSONArray) jsonObject.get("datatable");
        JSONObject o = (JSONObject) datatable.get(0);
        return "预约信息: " + o.get("Field").toString() + " 预约时间: " + o.get("PayTime").toString();
    }

}
