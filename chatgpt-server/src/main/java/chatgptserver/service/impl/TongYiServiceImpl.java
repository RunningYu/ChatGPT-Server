package chatgptserver.service.impl;

import chatgptserver.Common.ImageUtil;
import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.MessagesAO;
import chatgptserver.bean.ao.UploadResponse;
import chatgptserver.bean.dto.XunFeiXingHuo.imageCreate.Text;
import chatgptserver.bean.dto.tongYiQianWen.*;
import chatgptserver.bean.po.MessagesPO;
import chatgptserver.bean.po.UserPO;
import chatgptserver.dao.MessageMapper;
import chatgptserver.enums.GPTConstants;
import chatgptserver.service.MessageService;
import chatgptserver.service.OkHttpService;
import chatgptserver.service.TongYiService;
import chatgptserver.service.UserService;
import chatgptserver.utils.JwtUtils;
import chatgptserver.utils.MessageUtils;
import chatgptserver.utils.MinioUtil;
import chatgptserver.utils.StorageUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/27
 */
@Slf4j
@Service
public class TongYiServiceImpl implements TongYiService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private OkHttpService okHttpService;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public JsonResult tyImageUnderstand(MultipartFile image, String content, String token, String chatCode, Boolean isRebuild, String cid) {
        log.info("TongYiServiceImpl tyImageUnderstand image:[{}] content:[{}], token:[{}], chatCode:[{}], isRebuild:[{}], cid:[{}]", image, content, token, chatCode, isRebuild, cid);
        if (content == null || "".equals(content)) {
            log.info("TongYiServiceImpl tyImageUnderstand 请输入图片理解的问题");

            return JsonResult.error(500, "请输入图片理解的问题");
        }
        String imageUrl = "";
        if (image != null) {
            imageUrl = minioUtil.upLoadFileToURL(image);
        } else if (isRebuild){
//            imageUrl = content.split("\n")[0];
            MessagesPO messagesPO = messageMapper.getUpdateMessagePO(chatCode);
            if (messagesPO.getImage() != null && messagesPO.getImage().contains("http")) {
                imageUrl = messagesPO.getImage();
            }
        }
        log.info("TongYiServiceImpl tyImageUnderstand imageUrl:[{}]", imageUrl);
        String userCode = userService.getUserCodeByToken(token);
        content = MessageUtils.buildContent(content);
        // 构建多轮对话请求体
        TongYiImageUnderStandRequestDTO request = buildTongYiImageUnderstandRequestDTO(chatCode, imageUrl, content);
        log.info("WenXinServiceImpl tyImageUnderstand request:[{}]", request);
        String responseStr = "";
        try {
            responseStr = okHttpService.makePostRequest(
                        GPTConstants.TONG_YI_QIAN_WEN_IMAGE_UNDERSTAND_URL,
                        JSON.toJSONString(request),
                        GPTConstants.TONG_YI_QIAN_WEN_API_KEY);
            log.info("WenXinServiceImpl tyImageUnderstand responseStr:[{}]", responseStr);
        } catch (IOException e) {
            throw new RuntimeException("请求通义千问接口异常");
        }
        // 检查是否要取消生成
        if (StorageUtils.stopRequestMap.containsKey(cid)) {
            StorageUtils.stopRequestMap.remove(cid);

            return JsonResult.success();
        }
        TongYiImageUnderstandResponseDTO responseDTO = JSON.parseObject(responseStr, TongYiImageUnderstandResponseDTO.class);
        String response = responseDTO.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
        log.info("TongYiServiceImpl tyImageUnderstand response:[{}]", response);
        String question = imageUrl.equals("") ? content : (imageUrl + "\n\n" + content);
        MessagesAO result = messageService.buildMessageAO(userCode, chatCode, question, response);
        // 再次检查是否要取消生成
        if (StorageUtils.stopRequestMap.containsKey(cid)) {
            StorageUtils.stopRequestMap.remove(cid);

            return JsonResult.success();
        }
        if (isRebuild) {
            messageService.recordHistory("", chatCode, "", response, isRebuild);
        } else {
            messageService.recordHistoryWithImage(userCode, chatCode, imageUrl.equals("") ? "0" : imageUrl, question, response);
        }

        return JsonResult.success(result);
    }

    @Override
    public JsonResult tyQuestion(String token, String chatCode, String content, Boolean isRebuild, String cid) {
        log.info("TongYiServiceImpl getMessageFromWenXin token:[{}] chatCode:[{}], content:[{}], isRebuild:[{}], cid:[{}]", token, chatCode, content, isRebuild, cid);
        Text text = new Text("user", content);
        List<Text> textList = new ArrayList<>();
        textList.add(text);
        String userCode = "";
        if (token != null && !"".equals(token)) {
            UserPO userPO = jwtUtils.getUserFromToken(token);
            userCode = userPO.getUserCode();
            // 获取历史聊天记录
            List<MessagesPO> historyLis = messageMapper.getWenXinHistory(chatCode);
            for (MessagesPO history : historyLis) {
                Text replication = new Text("assistant", history.getReplication());
                textList.add(0, replication);
                Text question = new Text("user", history.getQuestion());
                textList.add(0, question);
            }

        }
        QuestionInput input = new QuestionInput(textList);
        QuestionRequestDTO request = new QuestionRequestDTO("qwen-14b-chat", input);
        log.info("TongYiServiceImpl getMessageFromWenXin input:[{}]", input);
        String responseStr = "";
        try {
            responseStr = okHttpService.makePostRequest(GPTConstants.TONG_YI_QIAN_WEN_QUESTION_URL, JSON.toJSONString(request), GPTConstants.TONG_YI_QIAN_WEN_API_KEY);
            log.info("TongYiServiceImpl getMessageFromWenXin responseStr:[{}]", responseStr);
        } catch (IOException e) {
            throw new RuntimeException("通义千问文本问答接口调用异常");
        }
        QuestionResponseDTO responseDTO = JSON.parseObject(responseStr, QuestionResponseDTO.class);
        String response = responseDTO.getOutput().getText();

        // 检查是否要取消生成
        if (StorageUtils.stopRequestMap.containsKey(cid)) {
            StorageUtils.stopRequestMap.remove(cid);

            return JsonResult.success();
        }
        messageService.recordHistory(userCode, chatCode, content, response, isRebuild);
        MessagesAO result = messageService.buildMessageAO(userCode, chatCode, content, response);

        return JsonResult.success(result);
    }

    @Override
    public JsonResult tyImageCreate(String userCode, String chatCode, String content, Boolean isRebuild, String cid) {
        log.info("TongYiServiceImpl tyImageCreate userCode:[{}] chatCode:[{}], content:[{}], isRebuild:[{}], cid:[{}]", userCode, chatCode, chatCode, isRebuild, cid);
        TongYiImageCreateRequestDTO request = TongYiImageCreateRequestDTO.buildTongYiImageCreateRequestDTO(content);
        log.info("TongYiServiceImpl tyImageCreate request:[{}]", JSON.toJSONString(request));

        String responseStr = "";
        String authorization = "Bearer " + GPTConstants.TONG_YI_QIAN_WEN_API_KEY;
        try {
            responseStr = okHttpService.makePostRequest(GPTConstants.TONG_YI_QIAN_WEN_IMAGE_CREATE_POST_URL, JSON.toJSONString(request), authorization, "enable");
            log.info("TongYiServiceImpl tyImageCreate responseStr:[{}]", responseStr);
        } catch (IOException e) {
            throw new RuntimeException("调用大模型接口异常");
        }
        ImageCreateTaskIdRes resultOutput = JSON.parseObject(responseStr, ImageCreateTaskIdRes.class);
        String task_id = resultOutput.getOutput().getTask_id();
        String response = "";
        while (true) {
            // 检查是否要取消生成
            if (StorageUtils.stopRequestMap.containsKey(cid)) {
                StorageUtils.stopRequestMap.remove(cid);

                return JsonResult.success();
            }
            String url = String.format(GPTConstants.TONG_YI_QIAN_WEN_IMAGE_CREATE_GET_URL, task_id);
            String res = "";
            try {
                res = okHttpService.makeGetRequest(url, authorization);
            } catch (IOException e) {
                throw new RuntimeException("获取作业结果接口异常！");
            }
            TongYiImageCreateResponseDTO responseDTO = JSON.parseObject(res, TongYiImageCreateResponseDTO.class);
            if (responseDTO.getOutput().getTask_status().equals("SUCCEEDED")) {
                log.info("TongYiServiceImpl tyImageCreate res:[{}]", res);
                response = responseDTO.getOutput().getResults().get(0).get("url");
                log.info("TongYiServiceImpl tyImageCreate before minio response:[{}]", response);
                try {
                    MultipartFile multipartFile = ImageUtil.imageUrlToMultipartFile(response);
                    UploadResponse uploadResponse = minioUtil.uploadFile(multipartFile, "file");
                    response = uploadResponse.getMinIoUrl();
                    log.info("TongYiServiceImpl tyImageCreate after minio response:[{}]", response);
                } catch (Exception e) {
                    return JsonResult.error(500, "图片处理异常");
                }
                break;
            } else {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }
        }
        log.info("TongYiServiceImpl tyImageCreate response:[{}]", response);
        String replication = response + "\n\n" + GPTConstants.RESULT_CREATE_TAG;
        // 再次检查是否要取消生成
        if (StorageUtils.stopRequestMap.containsKey(cid)) {
            StorageUtils.stopRequestMap.remove(cid);

            return JsonResult.success();
        }
        messageService.recordHistory(userCode, chatCode, content, replication, isRebuild);
        MessagesAO result = messageService.buildMessageAO(userCode, chatCode, content, replication);

        return JsonResult.success(result);
    }


    public TongYiImageUnderStandRequestDTO buildTongYiImageUnderstandRequestDTO(String chatCode, String imageUrl, String content) {
        List<TongYiMessages> list = new ArrayList<>();
        // 如果没有图片链接，则表示是开始了多轮对话
        if (imageUrl == null || imageUrl.equals("")) {
            // 获取 通义千问：文本问答 的第一轮对话
            MessagesPO messagesFistChat = messageMapper.getTongYiQuestionFistChat(chatCode);
            log.info("TongYiServiceImpl TongYiImageUnderStandRequestDTO messagesFistChat:[{}]", messagesFistChat);
            String question = messagesFistChat.getQuestion().split("\n")[messagesFistChat.getQuestion().split("\n").length - 1];
            TongYiMessages tongYiMessagesFirstMap = TongYiMessages.buildTongYiMessages("user", messagesFistChat.getImage(), question);
            TongYiMessages tongYiMessagesFirstRplMap = TongYiMessages.buildTongYiMessages("assistant", messagesFistChat.getReplication());
            list.add(tongYiMessagesFirstMap);
            list.add(tongYiMessagesFirstRplMap);
            System.out.println(tongYiMessagesFirstMap);
            System.out.println(tongYiMessagesFirstRplMap);

            // 获取历史聊天记录
            List<MessagesPO> historyLis = messageMapper.getTongYiMultipleQuestionHistory(chatCode, messagesFistChat.getId());
            for (MessagesPO history : historyLis) {

                question = history.getQuestion().split("\n")[history.getQuestion().split("\n").length - 1];
                TongYiMessages messagesQuestion = TongYiMessages.buildTongYiMessages("user", question);
                TongYiMessages messagesReplication = TongYiMessages.buildTongYiMessages("assistant", history.getReplication());
                list.add(messagesQuestion);
                list.add(messagesReplication);
                System.out.println(messagesQuestion);
                System.out.println(messagesReplication);
            }
            TongYiMessages messagesNew = TongYiMessages.buildTongYiMessages("user", content);
            list.add(messagesNew);
        }

        // 如果有图片链接，则表示新的一轮 图片理解 对话
        else {
            Map<String, String> imageMap = new HashMap<>();
            imageMap.put("image", imageUrl);
            Map<String, String> textMap = new HashMap<>();
            textMap.put("text", content);
            List<Map<String, String>> contentList = new ArrayList<>();
            contentList.add(imageMap);
            contentList.add(textMap);
            TongYiMessages messages = new TongYiMessages("user", contentList);
            log.info("WenXinServiceImpl tyImageUnderstand messages:[{}]", messages);
            list.add(messages);
        }

        Input input = new Input(list);
        TongYiImageUnderStandRequestDTO request = new TongYiImageUnderStandRequestDTO("qwen-vl-plus");
        request.setInput(input);

        return request;
    }
}
