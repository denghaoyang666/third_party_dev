package cn.hayye.third_party_dev.Utils;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebUtils {
    private static final String URL_IS_NULL="请求URL为空！";

    private static final PoolingHttpClientConnectionManager HTTP_CLIENT_CONNECTION_MANAGER;
    private static final CloseableHttpClient HTTP_CLIENT;
    static {
        HTTP_CLIENT_CONNECTION_MANAGER = new PoolingHttpClientConnectionManager(RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", SSLConnectionSocketFactory.getSocketFactory()).build());
        HTTP_CLIENT_CONNECTION_MANAGER.setDefaultMaxPerRoute(100);
        HTTP_CLIENT_CONNECTION_MANAGER.setMaxTotal(200);
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(60000).setConnectTimeout(60000).setSocketTimeout(60000).build();
        HTTP_CLIENT = HttpClientBuilder.create().setConnectionManager(HTTP_CLIENT_CONNECTION_MANAGER).setDefaultRequestConfig(requestConfig).build();
    }

    /**
     * 发送Get请求
     * @param url
     * @param paramMap
     * @return
     */
    public static String get(String url, Map<String,Object> paramMap){
        Assert.hasText(url,WebUtils.URL_IS_NULL);
        String result = null;
        try {
            List<NameValuePair> nameValuePairs = WebUtils.mapToParam(paramMap);
            HttpGet httpGet = new HttpGet(url + (StringUtils.contains(url, "?") ? "&" : "?") + EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairs, "UTF-8")));
            CloseableHttpResponse httpResponse = HTTP_CLIENT.execute(httpGet);
            try {
                HttpEntity httpEntity = httpResponse.getEntity();
                if (httpEntity != null) {
                    result = EntityUtils.toString(httpEntity);
                    EntityUtils.consume(httpEntity);
                }
            } finally {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 发送Post请求
     * @param url
     * @param paramMap
     * @return
     */
    public static String post(String url,Map<String,Object> paramMap){
        Assert.hasText(url,WebUtils.URL_IS_NULL);
        String result=null;
        try{
            List<NameValuePair> nameValuePairs = WebUtils.mapToParam(paramMap);
            HttpPost httpPost =new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            CloseableHttpResponse httpResponse = HTTP_CLIENT.execute(httpPost);
            try {
                HttpEntity httpEntity = httpResponse.getEntity();
                if (httpEntity != null) {
                    result = EntityUtils.toString(httpEntity);
                    EntityUtils.consume(httpEntity);
                }
            } finally {
                httpResponse.close();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Map转Http4.5参数NameValuePair
     * @param paramMap
     * @return
     */
    protected static List mapToParam(Map<String,Object> paramMap){
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        if (paramMap != null) {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                String name = entry.getKey();
                String value = ConvertUtils.convert(entry.getValue());
                if (StringUtils.isNotEmpty(name)) {
                    nameValuePairs.add(new BasicNameValuePair(name, value));
                }
            }
        }
        return nameValuePairs;
    }

    /**
     * 参数解析
     *
     * @param query
     *            查询字符串
     * @param encoding
     *            编码格式
     * @return 参数
     */
    public static Map<String, String> parse(String query, String encoding) {
        Assert.hasText(query);

        Charset charset;
        if (StringUtils.isNotEmpty(encoding)) {
            charset = Charset.forName(encoding);
        } else {
            charset = Charset.forName("UTF-8");
        }
        List<NameValuePair> nameValuePairs = URLEncodedUtils.parse(query, charset);
        Map<String, String> parameterMap = new HashMap<>();
        for (NameValuePair nameValuePair : nameValuePairs) {
            parameterMap.put(nameValuePair.getName(), nameValuePair.getValue());
        }
        return parameterMap;
    }
}