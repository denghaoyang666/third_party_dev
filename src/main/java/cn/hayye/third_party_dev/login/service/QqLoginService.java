package cn.hayye.third_party_dev.login.service;

import cn.hayye.third_party_dev.Utils.WebUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class QqLoginService {
    //code请求URL
    private static final String CODE_REQUEST_URL = "https://graph.qq.com/oauth2.0/authorize";

    //accessToken请求URL
    private static final String ACCESS_TOKEN_REQUEST_URL = "https://graph.qq.com/oauth2.0/token";

    // openId请求URL
    private static final String OPEN_ID_REQUEST_URL = "https://graph.qq.com/oauth2.0/me";

    // state在Session中的的名字
    private static final String STATE_ATTRIBUTE_NAME = "QQ_LOGIN_STATE";

    @Value("${login.qq.appid}")
    private String client_id;

    @Value("${login.qq.appkey}")
    private String client_secret;

    @Value("${login.qq.redirect}")
    private String redirect_uri;

    public void signInHandle( HttpServletRequest request, Model model) {
        String state = DigestUtils.md5DigestAsHex(UUID.randomUUID().toString().getBytes()).toUpperCase();
        request.getSession().setAttribute(STATE_ATTRIBUTE_NAME, state);

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("response_type", "code");
        parameterMap.put("client_id", client_id);
        parameterMap.put("redirect_uri", redirect_uri);
        parameterMap.put("state", state);

        model.addAttribute("requestUrl", CODE_REQUEST_URL);
        model.addAttribute("parameterMap", parameterMap);
    }

    public Boolean isLoginSuccess( HttpServletRequest request) {
        String state = (String) request.getSession().getAttribute(STATE_ATTRIBUTE_NAME);
        if (StringUtils.isNotEmpty(state) && StringUtils.equals(state, request.getParameter("state")) && StringUtils.isNotEmpty(request.getParameter("code"))) {
            request.getSession().removeAttribute(STATE_ATTRIBUTE_NAME);
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("grant_type", "authorization_code");
            parameterMap.put("client_id", client_id);
            parameterMap.put("client_secret", client_secret);
            parameterMap.put("redirect_uri", redirect_uri);
            parameterMap.put("code", request.getParameter("code"));
            String content = WebUtils.get(ACCESS_TOKEN_REQUEST_URL, parameterMap);
            String accessToken = WebUtils.parse(content,null).get("access_token");
            if (StringUtils.isNotEmpty(accessToken)) {
                request.setAttribute("accessToken", accessToken);
                return true;
            }
        }
        return false;
    }

    public String getOpenID(HttpServletRequest request){
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("access_token",request.getAttribute("accessToken"));
        String content = WebUtils.get(OPEN_ID_REQUEST_URL, parameterMap);
        String callback = content.replace("callback(","").replace(");","").trim();
        return JSON.parseObject(callback).getString("openid");
    }
}
