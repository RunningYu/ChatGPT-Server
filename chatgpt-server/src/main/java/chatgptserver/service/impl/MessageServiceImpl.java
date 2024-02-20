package chatgptserver.service.impl;

import chatgptserver.bean.ao.ChatAddRequestAO;
import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.dto.WenXin.WXAccessTokenRspDTO;
import chatgptserver.bean.dto.WenXin.WenXinReqMessagesDTO;
import chatgptserver.bean.dto.WenXin.WenXinRequestBodyDTO;
import chatgptserver.bean.dto.WenXin.WenXinRspDTO;
import chatgptserver.bean.po.MessagesPO;
import chatgptserver.bean.po.UserPO;
import chatgptserver.dao.MessageMapper;
import chatgptserver.dao.UserMapper;
import chatgptserver.enums.GPTConstants;
import chatgptserver.enums.RoleTypeEnums;
import chatgptserver.service.MessageService;
import chatgptserver.service.OkHttpService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static chatgptserver.enums.GPTConstants.GPT_KEY_MAP;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:32
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

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
            String accessToken = okHttpService.makeGetRequest(url);
            WXAccessTokenRspDTO accessTokenResponse = JSON.parseObject(accessToken, WXAccessTokenRspDTO.class);
            log.info("MessageServiceImpl getMessageFromWenXin url:[{}], accessToken:[{}]", url, accessTokenResponse.getAccess_token());
            String url1 = String.format(GPTConstants.WEN_XIN_ASK_URL, accessTokenResponse.getAccess_token());
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

            recordHistory(userCode, chatCode, message, wenXinRspDTO.getResult());

            return wenXinRspDTO.getResult();
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public Map<String, String> wenXinAdd(ChatAddRequestAO request) {
        log.info("MessageServiceImpl getMessageFromWenXin request:[{}]", request);
        String code = userMapper.wenXinAdd(request);


        return null;
    }

    @Override
    public JsonResult xfImageUnderstand(String image) {
        log.info("MessageServiceImpl xfImageUnderstand image:[{}]", image);


        return null;
    }

    private void recordHistory(String userCode, String chatCode, String message, String result) {

        UserPO sender = userMapper.getUserByCode(userCode);
        UserPO target = userMapper.getUserByCode(chatCode);

        MessagesPO messagesPO = new MessagesPO();
        messagesPO.setRole(RoleTypeEnums.WEN_XIN_USER.getType());
        messagesPO.setUserCode(userCode);
        messagesPO.setChatCode(chatCode);
        messagesPO.setUsername(sender.getUsername());
        messagesPO.setChatName(target.getUsername());
        messagesPO.setQuestion(message);
        messagesPO.setReplication(result);

        messageMapper.insertMessage(messagesPO);



    }
}
