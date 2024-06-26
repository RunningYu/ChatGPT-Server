package chatgptserver.service.impl;

import chatgptserver.Common.FileUtil;
import chatgptserver.Common.ImageUtil;
import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.MessagesAO;
import chatgptserver.bean.ao.UploadResponse;
import chatgptserver.bean.dto.WenXin.*;
import chatgptserver.bean.dto.WenXin.imageCreate.WenXinImageResponse;
import chatgptserver.bean.dto.WenXin.imageUnderstand.WenXinImageUnderstandDTO;
import chatgptserver.bean.dto.WenXin.imageUnderstand.WenXinImageUnderstandResponseDTO;
import chatgptserver.bean.po.MessagesPO;
import chatgptserver.dao.MessageMapper;
import chatgptserver.dao.UserMapper;
import chatgptserver.enums.CharacterConstants;
import chatgptserver.enums.GPTConstants;
import chatgptserver.enums.RoleTypeEnums;
import chatgptserver.service.MessageService;
import chatgptserver.service.OkHttpService;
import chatgptserver.service.UserService;
import chatgptserver.service.WenXinService;
import chatgptserver.utils.MessageUtils;
import chatgptserver.utils.MinioUtil;
import chatgptserver.utils.StorageUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private OkHttpService okHttpService;

    @Override
    public JsonResult getMessageFromWenXin(String userCode, String chatCode, String message, Boolean isRebuild, String cid) {
        log.info("MessageServiceImpl getMessageFromWenXin");
        Date questionTime = new Date();
        if (isRebuild == false && (message == null || "".equals(message))) {
            log.info("MessageServiceImpl getMessageFromWenXin 请先输入文字描述");

            return JsonResult.error(500, "请先输入文字描述");
        }
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
            // 再次检查是否要取消生成
            if (StorageUtils.stopRequestMap.containsKey(cid)) {
                StorageUtils.stopRequestMap.remove(cid);

                return JsonResult.success();
            }

            messageService.recordHistory(userCode, chatCode, message, wenXinRspDTO.getResult(), isRebuild, questionTime);
            String response = ( (wenXinRspDTO.getResult() == null || wenXinRspDTO.getResult().equals("")) ? "[没有生成相应的结果]" : wenXinRspDTO.getResult() );
            MessagesAO messagesAO = messageService.buildMessageAO(userCode, chatCode, message, response, questionTime);

            return JsonResult.success(messagesAO);
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    @Override
    public JsonResult wxImageCreate(String userCode, String chatCode, String content, Boolean isRebuild, String cid) {
        log.info("WenXinServiceImpl wxImageCreate userCode:[{}], chatCode:[{}], content:[{}], cid:[{}]", userCode, chatCode, content, cid);
        Date questionTime = new Date();
        if (isRebuild == false && (content == null || "".equals(content))) {
            log.info("MessageServiceImpl wxImageCreate 请先输入文字描述");

            return JsonResult.error(500, "请先输入文字描述");
        }
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
        // 检查是否要取消生成
        if (StorageUtils.stopRequestMap.containsKey(cid)) {
            StorageUtils.stopRequestMap.remove(cid);

            return JsonResult.success();
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
            String replication = uploadResponse.getMinIoUrl() + "\n\n" + GPTConstants.RESULT_CREATE_TAG;
            MessagesAO result = messageService.buildMessageAO(userCode, chatCode, content, replication, questionTime);
            // 再次检查是否要取消生成
            if (StorageUtils.stopRequestMap.containsKey(cid)) {
                StorageUtils.stopRequestMap.remove(cid);

                return JsonResult.success();
            }
            // 记录历史记录
            messageService.recordHistory(userCode, chatCode, content, replication, isRebuild, questionTime);
//            messageService.recordHistory(userCode, chatCode, content, uploadResponse.getMinIoUrl());

            return JsonResult.success(result);
        } catch (Exception e) {
            throw new RuntimeException("图片生成MinIO失败");
        }
    }

    @Override
    public JsonResult wenXinImageUnderstand(String token, String chatCode, MultipartFile image, String content, Boolean isRebuild, String cid) {
        Date questionTime = new Date();
        String realContent = content;
        if (image == null && (content == null || "".equals(content)) && isRebuild == false) {
            log.info("WenXinServiceImpl wenXinImageUnderstand 图片和文字不能同时为空");

            return JsonResult.error(500, "图片和文字不能同时为空");
        }
        String userCode = userService.getUserCodeByToken(token);
        if (userCode == null) {
            return JsonResult.error("token失效或过期！");
        }
        if (image == null && isRebuild == false) {
            return JsonResult.error(500, "文心一言的图片理解不支持多轮对话");
        }
        String url = String.format(GPTConstants.WEN_XIN_GET_ACCESS_TOKEN_URL, GPT_KEY_MAP.get(GPTConstants.WEN_XIN_API_KEY_NAME), GPT_KEY_MAP.get(GPTConstants.WEN_XIN_SECRET_KEY_NAME));
        // 获取accessToken
        String accessToken = null;
        try {
            accessToken = getWenXinAccessToken();
        } catch (Exception e) {
            return JsonResult.error("获取access_token异常！");
        }
        String base64Image = "";
        // 如果是重新生成，则需要将图片url转成base64
        if (image != null) {
            base64Image = ImageUtil.imageMultipartFileToBase64(image);
        } else {
            MessagesPO messagesPO = messageMapper.getUpdateMessagePO(chatCode);
            if (messagesPO.getImage() != null && messagesPO.getImage().contains("http")) {
                String iUrl = messagesPO.getImage();
                log.info("WenXinServiceImpl wenXinImageUnderstand iUrl:[{}]", iUrl);
//                String iUrl = content.split("\n")[0];
                base64Image = ImageUtil.imageUrlToBase64(iUrl);
            }
        }
        String question = MessageUtils.buildContent(content);
        content = question.equals("") ? CharacterConstants.DEFAULT_CONTENT : question;
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
        // 检查是否要取消生成
        if (StorageUtils.stopRequestMap.containsKey(cid)) {
            StorageUtils.stopRequestMap.remove(cid);

            return JsonResult.success();
        }
        WenXinImageUnderstandResponseDTO response = JSON.parseObject(responseStr, WenXinImageUnderstandResponseDTO.class);
        log.info("WenXinServiceImpl wenXinImageUnderstand response:[{}]", response);
        String imageUrl = "";
        question = content;
        if (image != null) {
            try {
                UploadResponse uploadResponse = minioUtil.uploadFile(image, "file");
                imageUrl = uploadResponse.getMinIoUrl();
                if (realContent == null || "".equals(realContent)) {
                    question = imageUrl;
                } else {
                    question = imageUrl + "\n\n" + realContent;
                }
            } catch (Exception e) {
                return JsonResult.error("图片处理异常！");
            }
        }
        // 再次检查是否要取消生成
        if (StorageUtils.stopRequestMap.containsKey(cid)) {
            StorageUtils.stopRequestMap.remove(cid);

            return JsonResult.success();
        }
        MessagesAO result = messageService.buildMessageAO(userCode, chatCode, question, response.getResult(), questionTime);
        if (isRebuild) {
            messageService.recordHistory(userCode, chatCode, question, response.getResult(), isRebuild, questionTime);
        } else {
            messageService.recordHistoryWithImage(userCode, chatCode, imageUrl, question, response.getResult(), questionTime);
        }

        return JsonResult.success(result);
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


}
