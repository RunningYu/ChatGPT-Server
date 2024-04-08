package chatgptserver.service.impl;

import chatgptserver.Mapping.ConvertMapping;
import chatgptserver.bean.ao.ChatAO;
import chatgptserver.bean.ao.ChatAddRequestAO;
import chatgptserver.bean.ao.MessagesAO;
import chatgptserver.bean.ao.MessagesResponseAO;
import chatgptserver.bean.po.ChatPO;
import chatgptserver.bean.po.GptPO;
import chatgptserver.bean.po.MessagesPO;
import chatgptserver.bean.po.UserPO;
import chatgptserver.dao.GptMapper;
import chatgptserver.dao.MessageMapper;
import chatgptserver.dao.UserMapper;
import chatgptserver.enums.RoleTypeEnums;
import chatgptserver.service.MessageService;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/23
 */

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private Cache<String, Object> caffeineCache;

    @Autowired
    private GptMapper gptMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void recordHistory(String userCode, String chatCode, String message, String result) {
        log.info("MessageServiceImpl recordHistory userCode:[{}], chatCode:[{}], message:[{}], result:[{}]", userCode, chatCode, message, result);
        UserPO sender = userMapper.getUserByCode(userCode);
        ChatPO target = userMapper.getChatByCode(chatCode);

        MessagesPO messagesPO = new MessagesPO();
        messagesPO.setRole(RoleTypeEnums.WEN_XIN_USER.getType());
        messagesPO.setUserCode(userCode);
        messagesPO.setChatCode(chatCode);
        messagesPO.setUsername(sender.getUsername());
        messagesPO.setChatName(target.getChatName());
        messagesPO.setQuestion(message);
        result = (result == null || result.equals("")) ? "没有生成相应的结果" : result;
        messagesPO.setReplication(result);

        messageMapper.insertMessage(messagesPO);
    }

    @Override
    public void recordHistoryWithImage(String userCode, String chatCode, String imageUrl, String content, String result) {
        log.info("MessageServiceImpl recordHistoryWithImage userCode:[{}], chatCode:[{}], content:[{}], result:[{}]", userCode, chatCode, content, result);
        MessagesPO messagesPO = new MessagesPO();
        messagesPO.setRole(RoleTypeEnums.WEN_XIN_USER.getType());
        messagesPO.setUserCode(userCode);
        messagesPO.setChatCode(chatCode);
        messagesPO.setImage(imageUrl);
        messagesPO.setQuestion(content);
        messagesPO.setReplication(result);

        messageMapper.insertMessage(messagesPO);
    }

    @Override
    public List<ChatAO> chatCreateList(String userCode, String gptCode) {
        log.info("MessageServiceImpl chatBoxList userCode:[{}], gptCode:[{}]", userCode, gptCode);
        List<ChatPO> list = messageMapper.chatCreateList(userCode, gptCode);
        List<ChatAO> response = new ArrayList<>();
        for (ChatPO chatPO : list) {
            // 统计对话的总数量
            int chatAmount = messageMapper.getChatAmount(chatPO.getChatCode());
            ChatAO chatAO = ConvertMapping.chatPO2ChatAO(chatPO);
            chatAO.setChatAmount(chatAmount);
            Date lastChatTime = messageMapper.getLastChatTime(chatPO.getChatCode());
            chatAO.setLastChatTime(lastChatTime);
            response.add(chatAO);
        }
        // 根据时间排序
        for(int i = 0; i < response.size() - 1; i++) {
            for(int j = 0; j < response.size() - 1 - i; j ++) {
                if(response.get(j).getLastChatTime().compareTo(response.get(j + 1).getLastChatTime()) < 0) {
                    ChatAO temp = response.get(j);
                    response.set(j, response.get(j + 1));
                    response.set(j + 1, temp);
                }
            }
        }
        log.info("MessageServiceImpl chatBoxList response:[{}]", response);

        return response;
    }

    @Override
    public MessagesResponseAO historyList(String chatCode, int page, int size) {
        log.info("MessageServiceImpl historyList chatCode:[{}], page:[{}], size:[{}]", chatCode, page, size);
        page = (page > 0 ? page : 1);
        int startIndex = (page - 1) * size;
        List<MessagesPO> historyList = messageMapper.getHistoryList(chatCode, startIndex, size);
        if (Objects.isNull(historyList) || historyList.size() == 0) {
            return null;
        }
        List<MessagesAO> list = new ArrayList<>();
        ChatPO chat = userMapper.getChatByCode(chatCode);
        UserPO user = userMapper.getUserByCode(historyList.get(0).getUserCode());
        GptPO gpt = gptMapper.getGptByCode(chat.getGptCode());
        String headshot = gpt.getHeadshot();
        for (MessagesPO messagesPO : historyList) {
            MessagesAO messagesAO =  ConvertMapping.messagesPO2MessagesResponseAO(messagesPO);
            messagesAO.setUserHeadshot(user.getHeadshot());
            messagesAO.setChatHeadshot(headshot);
            messagesAO.setChatName(chat.getChatName());
            messagesAO.setUsername(user.getUsername());
            list.add(messagesAO);
        }
        int total = messageMapper.getToalMessages(chatCode);
        boolean hasMore = (startIndex + size) < total ? true : false;
        MessagesResponseAO response = new MessagesResponseAO(list, total, hasMore);

        return response;
    }

}
