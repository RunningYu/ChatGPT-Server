package chatgptserver.service.impl;

import chatgptserver.bean.dto.WenXin.WenXinReqMessagesDTO;
import chatgptserver.bean.dto.XunFeiXingHuo.imageCreate.Text;
import chatgptserver.bean.dto.tongYiQianWen.*;
import chatgptserver.bean.po.MessagesPO;
import chatgptserver.dao.MessageMapper;
import chatgptserver.enums.GPTConstants;
import chatgptserver.enums.RoleTypeEnums;
import chatgptserver.service.MessageService;
import chatgptserver.service.OkHttpService;
import chatgptserver.service.TongYiService;
import chatgptserver.utils.MinioUtil;
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
    private OkHttpService okHttpService;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public String tyImageUnderstand(MultipartFile image, String content, String userCode, String chatCode) {
        log.info("TongYiServiceImpl tyImageUnderstand image:[{}] content:[{}], userCode:[{}], chatCode:[{}]", image, content, userCode, chatCode);
//        String imageUrl = minioUtil.upLoadFileToURL(image);
        String imageUrl = "http://124.71.110.30:9000/file/20240327_1711528457_165.jpg";
        TongYiImageUnderStandRequestDTO request = buildTongyiImageUnderstandRequestDTO(imageUrl, content);
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
        TongYiImageUnderstandResponseDTO responseDTO = JSON.parseObject(responseStr, TongYiImageUnderstandResponseDTO.class);
        String response = responseDTO.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
        log.info("TongYiServiceImpl tyImageUnderstand response:[{}]", response);
        messageService.recordHistoryWithImage(userCode, chatCode, imageUrl, content, response);

        return response;
    }

    @Override
    public String getMessageFromWenXin(String userCode, String chatCode, String content) {
        log.info("TongYiServiceImpl getMessageFromWenXin userCode:[{}] chatCode:[{}], content:[{}]", userCode, chatCode, content);
        Text text = new Text("user", content);
        List<Text> textList = new ArrayList<>();
        textList.add(text);

//        List<MessagesPO> historyLis = messageMapper.getWenXinHistory(chatCode);
//        for (MessagesPO history : historyLis) {
//            Text replication = new Text("system", history.getReplication());
//            textList.add(0, replication);
//            Text question = new Text("user", history.getQuestion());
//            textList.add(0, question);
//        }

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
        messageService.recordHistory(userCode, chatCode, content, response);

        return response;
    }

    public TongYiImageUnderStandRequestDTO buildTongyiImageUnderstandRequestDTO(String imageUrl, String content) {
        Map<String, String> imageMap = new HashMap<>();
        imageMap.put("image", imageUrl);
        Map<String, String> textMap = new HashMap<>();
        textMap.put("text", content);
        List<Map<String, String>> contentList = new ArrayList<>();
        contentList.add(imageMap);
        contentList.add(textMap);
        TongYiMessages messages = new TongYiMessages("user", contentList);
        log.info("WenXinServiceImpl tyImageUnderstand messages:[{}]", messages);
        List<TongYiMessages> list = new ArrayList<>();
        list.add(messages);
//--------------------------------------------------------------------------------------------------
//        List<Map<String, String>> contentList2 = new ArrayList<>();
//        Map<String, String> textMap2 = new HashMap<>();
//        textMap2.put("text", "这张照片中有一只小猫正坐在电脑前，看起来像是在使用笔记本电脑。这只小猫的毛色是棕白相间的，它用两只前爪抓住了电脑的边缘，身体则靠在桌子上。它的头抬起来看向镜头，表情显得很好奇或者专注。背景是一张木制桌子和一扇有窗帘的窗户，整体上给人一种温馨的家庭氛围感。");
//        contentList2.add(textMap2);
//        TongYiMessages messages2 = new TongYiMessages("system", contentList2);
//
//        List<Map<String, String>> contentList3 = new ArrayList<>();
//        Map<String, String> textMap3 = new HashMap<>();
//        textMap3.put("text", "你觉得它可爱吗？");
//        contentList3.add(textMap3);
//        TongYiMessages messages3 = new TongYiMessages("user", contentList3);
//
//        list.add(messages2);
//        list.add(messages3);
//--------------------------------------------------------------------------------------------------
        Input input = new Input(list);
        TongYiImageUnderStandRequestDTO request = new TongYiImageUnderStandRequestDTO("qwen-vl-plus");
        request.setInput(input);

        return request;
    }
}
