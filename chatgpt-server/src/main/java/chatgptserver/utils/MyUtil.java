//package chatgptserver.utils;
//
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpUriRequest;
//import org.apache.http.client.methods.RequestBuilder;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
//
//import java.net.URI;
//import java.nio.charset.StandardCharsets;
//import java.util.Map;
//
//public class MyUtil {
//    /**
//     * 1.发起post请求
//     */
//    public static String doPostJson(String url, Map<String, String> urlParams, String json) {
//        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
//        CloseableHttpResponse closeableHttpResponse = null;
//        String resultString = "";
//        try {
//            // 创建Http Post请求
//            String asciiUrl = URI.create(url).toASCIIString();
//            RequestBuilder builder = RequestBuilder.post(asciiUrl);
//            builder.setCharset(StandardCharsets.UTF_8);
//            if (urlParams != null) {
//                for (Map.Entry<String, String> entry : urlParams.entrySet()) {
//                    builder.addParameter(entry.getKey(), entry.getValue());
//                }
//            }
//            // 创建请求内容
//            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
//            builder.setEntity(entity);
//            HttpUriRequest request = builder.build();
//            // 执行http请求
//            closeableHttpResponse = closeableHttpClient.execute(request);
//            resultString = EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (closeableHttpResponse != null) {
//                    closeableHttpResponse.close();
//                }
//                if (closeableHttpClient != null) {
//                    closeableHttpClient.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return resultString;
//    }
//}
