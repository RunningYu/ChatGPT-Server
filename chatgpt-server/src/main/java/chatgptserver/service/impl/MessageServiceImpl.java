package chatgptserver.service.impl;

import chatgptserver.Mapping.ConvertMapping;
import chatgptserver.bean.ao.*;
import chatgptserver.bean.po.ChatPO;
import chatgptserver.bean.po.GptPO;
import chatgptserver.bean.po.MessagesPO;
import chatgptserver.bean.po.UserPO;
import chatgptserver.dao.GptMapper;
import chatgptserver.dao.MessageMapper;
import chatgptserver.dao.UserMapper;
import chatgptserver.enums.RoleTypeEnums;
import chatgptserver.service.MessageService;
import chatgptserver.service.UserService;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/23
 */

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private UserService userService;

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
        String userName = "";
        ChatPO target = userMapper.getChatByCode(chatCode);
        if (userCode != null && !userCode.equals("")) {
            UserPO sender = userMapper.getUserByCode(userCode);
            userName = sender.getUsername();
        } else {
            chatCode = "x_" + chatCode;
        }

        MessagesPO messagesPO = new MessagesPO();
        messagesPO.setRole(RoleTypeEnums.WEN_XIN_USER.getType());
        messagesPO.setUserCode(userCode);
        messagesPO.setChatCode(chatCode);
        messagesPO.setUsername(userName);
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
        chatCode = (userCode.equals("") ? ("x_" + chatCode) : chatCode);
        messagesPO.setChatCode(chatCode);
        messagesPO.setImage(imageUrl);
        messagesPO.setQuestion(content);
        messagesPO.setReplication(result);

        messageMapper.insertMessage(messagesPO);
    }

//    @Override
//    public Map<String, String> createNewChat(ChatPO chatPO) {
//        log.info("UserServiceImpl createNewChat chatPO:[{}]", chatPO);
//        int id = userMapper.newChat(chatPO);
//        String chatCode = "chat_" + chatPO.getId();
//        userMapper.updateChatCode(chatCode, chatPO.getId());
//        Map<String, String> response = new HashMap<>();
//        response.put("chatCode", chatCode);
//        log.info("UserServiceImpl createNewChat response:[{}]", response);
//
//        return response;
//    }

    @Override
    public List<ChatAO> chatCreateList(String token, String gptCode, String functionCode) {
        log.info("MessageServiceImpl chatCreateList token:[{}], gptCode:[{}], functionCode:[{}]", token, gptCode, functionCode);
        String userCode = userService.getUserCodeByToken(token);
        List<ChatPO> list = new ArrayList<>();
        if (userCode != null && !"".equals(userCode)) {
            list = messageMapper.chatCreateList(userCode, gptCode, functionCode);
        }
        List<ChatAO> response = new ArrayList<>();
        if (list.size() == 0) {
            // 创建默认的聊天
            ChatPO chatPO = new ChatPO();
            chatPO.setChatName("默认聊天");
            chatPO.setGptCode(gptCode);
            chatPO.setUserCode(userCode);
            chatPO.setFunctionCode(functionCode);
            chatPO.setFunctionCode("function_1");
            Map<String, String> map = userService.createNewChat(chatPO);
            String chatCode = map.get("chatCode");
            ChatAO chatAO = ConvertMapping.chatPO2ChatAO(chatPO);
            chatAO.setChatCode(chatCode);
            chatAO.setChatAmount(0);
            chatAO.setLastChatTime(new Date());
            chatAO.setCreateTime(new Date());
            response.add(chatAO);
            return response;
        }
        for (ChatPO chatPO : list) {
            // 统计对话的总数量
            int chatAmount = messageMapper.getChatAmount(chatPO.getChatCode());
            ChatAO chatAO = ConvertMapping.chatPO2ChatAO(chatPO);
            chatAO.setChatAmount(chatAmount);
            Date lastChatTime = messageMapper.getLastChatTime(chatPO.getChatCode());
            lastChatTime = lastChatTime == null ? new Date() : lastChatTime;
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
    public MessagesAO buildMessageAO(String userCode, String chatCode, String content, String totalResponse) {
        MessagesAO response = new MessagesAO();
        response.setQuestion(content);
        response.setReplication(totalResponse);
        if (userCode != null) {
            response.setUserCode(userCode);
            UserPO userPO = userService.getUserByCode(userCode);
            response.setUsername(userPO.getUsername());
            response.setUserHeadshot(userPO.getHeadshot());
        }
        ChatPO chat = userMapper.getChatByCode(chatCode);
        response.setChatName(chat.getChatName());
        GptPO gptPO = gptMapper.getGptByCode(chat.getGptCode());
        if (!Objects.isNull(gptPO)) {
            response.setChatHeadshot(gptPO.getHeadshot());
        }
        response.setChatCode(chatCode);
        response.setCreateTime(new Date());

        return response;
    }

    @Override
    public void recordHistoryWithReplyImage(String userCode, String chatCode, String content, String replication, String replyImage) {
        log.info("MessageServiceImpl recordHistoryWithReplyImage userCode:[{}], chatCode:[{}], content:[{}], result:[{}], replyImage:[{}]", userCode, chatCode, content, replication, replyImage);
        MessagesPO messagesPO = new MessagesPO();
        messagesPO.setRole(RoleTypeEnums.WEN_XIN_USER.getType());
        messagesPO.setUserCode(userCode);
        chatCode = ((userCode != null && !userCode.equals("")) ? chatCode : ("x_" + chatCode));
        messagesPO.setChatCode(chatCode);
        messagesPO.setQuestion(content);
        messagesPO.setReplication(replication);
        messagesPO.setReplyImage(replyImage);

        messageMapper.insertMessage(messagesPO);
    }

    @Override
    public MessagesResponseAO historyList(String chatCode, int page, int size) {
        log.info("MessageServiceImpl historyList chatCode:[{}], page:[{}], size:[{}]", chatCode, page, size);
        page = (page > 0 ? page : 1);
        int startIndex = (page - 1) * size;
        ChatPO chat = userMapper.getChatByCode(chatCode);
        List<MessagesPO> historyList = messageMapper.getHistoryList(chatCode, startIndex, size);
        if (Objects.isNull(historyList) || historyList.size() == 0) {
            MessagesResponseAO response = buildDefaultResponse(chat.getChatName(), chat.getFunctionCode());

            return response;
        }
        List<MessagesAO> list = new ArrayList<>();
        UserPO user = userMapper.getUserByCode(historyList.get(0).getUserCode());
        GptPO gpt = gptMapper.getGptByCode(chat.getGptCode());
        String headshot = gpt.getHeadshot();
        for (MessagesPO messagesPO : historyList) {
            MessagesAO messagesAO =  ConvertMapping.messagesPO2MessagesResponseAO(messagesPO);
            messagesAO.setUserHeadshot(user.getHeadshot());
            messagesAO.setChatHeadshot(headshot);
            messagesAO.setChatName(chat.getChatName());
            messagesAO.setUsername(user.getUsername());
            list.add(0, messagesAO);
        }
        int total = messageMapper.getToalMessages(chatCode);
        boolean hasMore = (startIndex + size) < total ? true : false;
        MessagesResponseAO response = new MessagesResponseAO(list, total, hasMore, chat.getChatName(), chat.getFunctionCode());

        return response;
    }

    public MessagesResponseAO buildDefaultResponse(String chatName, String functionCode) {
        MessagesResponseAO response = new MessagesResponseAO();
        response.setChatName(chatName);
        response.setHasMore(false);
        response.setFunctionCode(functionCode);
        response.setTotal(0);
        response.setList(new ArrayList<>());

        return response;
    }

}
