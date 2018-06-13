package com.lynn.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lynn.common.encryption.Algorithm;
import com.lynn.common.encryption.MessageDigestUtils;
import com.lynn.common.redis.Redis;
import com.lynn.common.result.Code;
import com.lynn.common.result.SingleResult;
import com.lynn.common.utils.AesEncryptUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ApiFilter extends ZuulFilter {

    @Value("${api.encrypt.key}")
    private String salt;

//    @Autowired
//    private Redis redis;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        //这里写校验代码
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        try {
            request.setCharacterEncoding("UTF-8");
            response.setHeader("Content-type","text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            Map<String,List<String>> params = new HashMap<>();
            String url = request.getRequestURI();
            //这两种情况无需签名
            if (url.contains("encrypt") || url.contains("error")) {
                return null;
            }
            StringBuilder urlBuilder = getUrlAuthenticationApi(request);
            if (StringUtils.isEmpty(urlBuilder)) {
                return null;
            }
            String sign = MessageDigestUtils.encrypt(urlBuilder.toString() + salt, Algorithm.MD5);
            String signature = request.getHeader("signature");
            if (sign != null && sign.equals(signature)) {
                //开放接口和隐私接口 open
                if(url.contains("/close/")){
//                    redis.set(token,userId);
//                    String token = request.getHeader("token");
////                    String userId = redis.get(token)+"";
//                    if(StringUtils.isEmpty(userId)){
//                        response.getWriter().print(AesEncryptUtils.aesEncrypt(JSON.toJSONString(SingleResult.buildFailure(Code.NO_PERMISSION, "token is error")),salt));
//                        response.getWriter().close();
//                        return null;
//                    }
//                    request.setAttribute("userId",userId);
                }
                return null;
            } else {
                //签名错误
                response.getWriter().print(AesEncryptUtils.aesEncrypt(JSON.toJSONString(SingleResult.buildFailure(Code.NO_PERMISSION, "签名错误")),salt));
                response.getWriter().close();
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private StringBuilder getUrlAuthenticationApi(HttpServletRequest request)throws Exception {
        String requestBodyStr;
        try {
            requestBodyStr = requestBody(request);
        } catch (IOException e) {
            return null;
        }
        if (StringUtils.isEmpty(requestBodyStr)) {
            return null;
        }
        requestBodyStr = AesEncryptUtils.aesDecrypt(requestBodyStr,salt);
        JSONObject requestBodyJson = JSON.parseObject(requestBodyStr);
        List<String> nameList = new ArrayList<>();
        nameList.add("token");
        nameList.add("timestamp");
        nameList.addAll(requestBodyJson.keySet());
        StringBuilder urlBuilder = new StringBuilder();
        nameList.stream().sorted().forEach(name -> {
            if ("token".equals(name) || "timestamp".equals(name)) {
                if ("token".equals(name) && null == request.getHeader(name)) {
                    return;
                }
                urlBuilder.append('&');
                urlBuilder.append(name).append('=').append(request.getHeader(name));
            } else {
                urlBuilder.append('&');
                urlBuilder.append(name).append('=').append(requestBodyJson.getString(name));
            }
        });
        urlBuilder.deleteCharAt(0);
        return urlBuilder;
    }

    public static String requestBody(HttpServletRequest request) throws IOException {
        // 读取请求内容
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF-8"));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        // 将资料解码
        return sb.toString();
    }

}