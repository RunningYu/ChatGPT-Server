package chatgptserver.service.impl;

import chatgptserver.Common.FileUtil;
import chatgptserver.Common.ImageUtil;
import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.UploadResponse;
import chatgptserver.bean.dto.WenXin.*;
import chatgptserver.bean.dto.WenXin.imageCreate.WenXinImageResponse;
import chatgptserver.bean.dto.WenXin.imageUnderstand.WenXinImageUnderstandDTO;
import chatgptserver.bean.dto.WenXin.imageUnderstand.WenXinImageUnderstandResponseDTO;
import chatgptserver.bean.po.MessagesPO;
import chatgptserver.dao.MessageMapper;
import chatgptserver.dao.UserMapper;
import chatgptserver.enums.GPTConstants;
import chatgptserver.enums.RoleTypeEnums;
import chatgptserver.service.MessageService;
import chatgptserver.service.OkHttpService;
import chatgptserver.service.UserService;
import chatgptserver.service.WenXinService;
import chatgptserver.utils.MinioUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static chatgptserver.enums.GPTConstants.GPT_KEY_MAP;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:32
 */
@Slf4j
@Service
public class WenXinServiceImpl implements WenXinService {

    @Autowired
    private UserService userService;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private OkHttpService okHttpService;

    @Override
    public String getMessageFromWenXin(String userCode, String chatCode, String message) {
        log.info("MessageServiceImpl getMessageFromWenXin");
        String url = String.format(GPTConstants.WEN_XIN_GET_ACCESS_TOKEN_URL, GPT_KEY_MAP.get(GPTConstants.WEN_XIN_API_KEY_NAME), GPT_KEY_MAP.get(GPTConstants.WEN_XIN_SECRET_KEY_NAME));

        try {
            // 获取accessToken
            String accessToken = getWenXinAccessToken();
            log.info("MessageServiceImpl getMessageFromWenXin url:[{}], accessToken:[{}]", url, accessToken);
            String url1 = String.format(GPTConstants.WEN_XIN_ASK_URL, accessToken);
            List<WenXinReqMessagesDTO> messagesList = new ArrayList<>();

            List<MessagesPO> historyLis = messageMapper.getWenXinHistory(chatCode);
            for (MessagesPO history : historyLis) {
                WenXinReqMessagesDTO replication = new WenXinReqMessagesDTO();
                replication.setRole(RoleTypeEnums.getRole(RoleTypeEnums.WEN_XIN_ASSISTANT.getType()));
                replication.setContent(history.getReplication());
                messagesList.add(0, replication);

                WenXinReqMessagesDTO question = new WenXinReqMessagesDTO();
                question.setRole(RoleTypeEnums.getRole(RoleTypeEnums.WEN_XIN_USER.getType()));
                question.setContent(history.getQuestion());
                messagesList.add(0, question);
            }
            WenXinReqMessagesDTO messagesDTO = new WenXinReqMessagesDTO();
            messagesDTO.setRole(RoleTypeEnums.getRole(1));
            messagesDTO.setContent(message);
            messagesList.add(messagesDTO);
            WenXinRequestBodyDTO body = new WenXinRequestBodyDTO(messagesList);
            log.info("MessageServiceImpl getMessageFromWenXin history body:[{}]", body);

            String requestBody = JSON.toJSONString(body);
            System.out.println("requestBody：" + requestBody);
            log.info("MessageServiceImpl getMessageFromWenXin requestBody:[{}]", requestBody);
            String responseStr = okHttpService.makePostRequest(url1, requestBody);
            WenXinRspDTO wenXinRspDTO = JSON.parseObject(responseStr, WenXinRspDTO.class);
            log.info("MessageServiceImpl getMessageFromWenXin response:[{}]", wenXinRspDTO);

            messageService.recordHistory(userCode, chatCode, message, wenXinRspDTO.getResult());
            String response = ( (wenXinRspDTO.getResult() == null || wenXinRspDTO.getResult().equals("")) ? "[没有生成相应的结果]" : wenXinRspDTO.getResult() );
            return response;
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    @Override
    public String wxImageCreate(String userCode, String chatCode, String content) {
        log.info("WenXinServiceImpl wxImageCreate userCode:[{}], chatCode:[{}], content:[{}]", userCode, chatCode, content);
        String accessToken = null;
        try {
//            accessToken = getAccessToken();
            accessToken = getWenXinAccessToken();
        } catch (Exception e) {
            JsonResult.error("获取accessToken异常");
        }
        String url = String.format(GPTConstants.WEN_XIN_IMAGE_CREATE_URL, accessToken);
        log.info("WenXinServiceImpl wxImageCreate url:[{}]", url);
        WenXinImageCreateRequestDTO requestBody = new WenXinImageCreateRequestDTO();
        requestBody.setPrompt(content);
        log.info("WenXinServiceImpl wxImageCreate requestBody:[{}]", requestBody);
        String responseStr = "";
        try {
            responseStr = okHttpService.makePostRequest(url, JSON.toJSONString(requestBody));
        } catch (IOException e) {
            throw new RuntimeException("图片生成失败");
        }
        log.info("WenXinServiceImpl wxImageCreate responseStr:[{}]", responseStr);
        WenXinImageResponse response = JSON.parseObject(responseStr, WenXinImageResponse.class);
        String B64_image = response.getData().get(0).getB64_image();
        log.info("WenXinServiceImpl wxImageCreate B64_image:[{}]", B64_image);
        File file = ImageUtil.convertBase64StrToImage(B64_image, "文心一言生图" + System.currentTimeMillis() + ".jpg");
        MultipartFile multipartFile = FileUtil.ConvertFileToMultipartFile(file);
        try {
            // 将AI生成的图片上传到MinIO返回Url
            UploadResponse uploadResponse = minioUtil.uploadFile(multipartFile, "file");
            // 记录历史记录
            messageService.recordHistory(userCode, chatCode, content, uploadResponse.getMinIoUrl());

            return uploadResponse.getMinIoUrl();
        } catch (Exception e) {
            throw new RuntimeException("图片生成MinIO失败");
        }
    }

    @Override
    public JsonResult wenXinImageUnderstand(String token, String chatCode, MultipartFile image, String content) {
        String userCode = userService.getUserCodeByToken(token);
        if (userCode == null) {
            return JsonResult.error("token失效或过期！");
        }
        String url = String.format(GPTConstants.WEN_XIN_GET_ACCESS_TOKEN_URL, GPT_KEY_MAP.get(GPTConstants.WEN_XIN_API_KEY_NAME), GPT_KEY_MAP.get(GPTConstants.WEN_XIN_SECRET_KEY_NAME));
        // 获取accessToken
        String accessToken = null;
        try {
            accessToken = getWenXinAccessToken();
        } catch (Exception e) {
            return JsonResult.error("获取access_token异常！");
        }
        String base64Image = ImageUtil.imageMultipartFileToBase64(image);
        WenXinImageUnderstandDTO request = new WenXinImageUnderstandDTO(content, base64Image);
        log.info("WenXinServiceImpl wenXinImageUnderstand request:[{}]", request);
        String requestJson = JSON.toJSONString(request);
        String responseStr = "";
        try {
            String url1 = GPTConstants.WEN_XIN_IMAGE_UNDERSTAND_URL + accessToken;
            responseStr = okHttpService.makePostRequest(url1, requestJson);
            log.info("WenXinServiceImpl wenXinImageUnderstand responseStr:[{}]", responseStr);

        } catch (IOException e) {
            return JsonResult.error("调用文心一言接口异常！");
        }
        WenXinImageUnderstandResponseDTO response = JSON.parseObject(responseStr, WenXinImageUnderstandResponseDTO.class);
        log.info("WenXinServiceImpl wenXinImageUnderstand response:[{}]", response);
        String imageUrl = "";
        try {
            UploadResponse uploadResponse = minioUtil.uploadFile(image, "file");
            imageUrl = uploadResponse.getMinIoUrl();
        } catch (Exception e) {
            return JsonResult.error("图片处理异常！");
        }
        messageService.recordHistoryWithImage(userCode, chatCode, imageUrl, content, response.getResult());

        return JsonResult.success(response.getResult());
    }


    public String getWenXinAccessToken() {
        log.info("MessageServiceImpl getWenXinAccessToken API_ID:[{}], Secret_Key:[{}]", GPT_KEY_MAP.get(GPTConstants.WEN_XIN_API_KEY_NAME), GPT_KEY_MAP.get(GPTConstants.WEN_XIN_SECRET_KEY_NAME));
        String accessTokenUrl = String.format(GPTConstants.WEN_XIN_GET_ACCESS_TOKEN_URL, GPT_KEY_MAP.get(GPTConstants.WEN_XIN_API_KEY_NAME), GPT_KEY_MAP.get(GPTConstants.WEN_XIN_SECRET_KEY_NAME));
        String accessToken = "";
        try {
            accessToken = okHttpService.makeGetRequest(accessTokenUrl);
        } catch (IOException e) {
            throw new RuntimeException("获取accessToken异常!");
        }
        WXAccessTokenRspDTO accessTokenResponse = JSON.parseObject(accessToken, WXAccessTokenRspDTO.class);
        log.info("MessageServiceImpl getWenXinAccessToken accessTokenUrl:[{}], accessToken:[{}]", accessTokenUrl, accessTokenResponse.getAccess_token());

        return accessTokenResponse.getAccess_token();
    }

//    OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
//
//    public String getTokenResponse() throws IOException{
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, "");
//        Request request = new Request.Builder()
//                .url("https://aip.baidubce.com/oauth/2.0/token?client_id=8n7iZg2LgIZiSoFsycaGkb2z&client_secret=y4KeIGGnVAQ0nDBRjy6p3HuBGdRHk0Dv&grant_type=client_credentials")
//                .method("POST", body)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Accept", "application/json")
//                .build();
//        Response response = HTTP_CLIENT.newCall(request).execute();
//        String tokenResponse = response.body().string();
//        System.out.println(tokenResponse);
//        WXAccessTokenRspDTO accessTokenResponse = JSON.parseObject(tokenResponse, WXAccessTokenRspDTO.class);
//        log.info("MessageServiceImpl getTokenResponse tokenResponse:[{}]", tokenResponse);
//        log.info("MessageServiceImpl getTokenResponse access_token:[{}]", accessTokenResponse.getAccess_token());
//        System.out.println("——————————————————————————————————————————————————————————————————————————");
//        System.out.println(accessTokenResponse.getAccess_token());
//        System.out.println("——————————————————————————————————————————————————————————————————————————");
//
//        return accessTokenResponse.getAccess_token();
//    }
//
//    final String API_KEY = "aHZkdH****Sf655D";
//    final String SECRET_KEY = "cVkrqC****wwICLs";
//
//    /**
//     * 从用户的AK，SK生成鉴权签名（Access Token）
//     *
//     * @return 鉴权签名（Access Token）
//     * @throws IOException IO异常
//     */
//    public String getAccessToken() throws IOException, JSONException {
//        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + GPTConstants.WEN_XIN_API_KEY
//                + "&client_secret=" + GPTConstants.WEN_XIN_SECRET_KEY);
//        Request request = new Request.Builder()
//                .url("https://aip.baidubce.com/oauth/2.0/token")
//                .method("POST", body)
//                .addHeader("Content-Type", "application/x-www-form-urlencoded")
//                .build();
//        Response response = HTTP_CLIENT.newCall(request).execute();
//        return new JSONObject(response.body().string()).getString("access_token");
//    }

}
