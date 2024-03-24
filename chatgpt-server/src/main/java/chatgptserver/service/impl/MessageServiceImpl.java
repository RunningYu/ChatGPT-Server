package chatgptserver.service.impl;

import chatgptserver.Mapping.ConvertMapping;
import chatgptserver.bean.ao.MessagesAO;
import chatgptserver.bean.ao.MessagesResponseAO;
import chatgptserver.bean.po.MessagesPO;
import chatgptserver.bean.po.UserPO;
import chatgptserver.dao.MessageMapper;
import chatgptserver.dao.UserMapper;
import chatgptserver.enums.RoleTypeEnums;
import chatgptserver.service.MessageService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void recordHistory(String userCode, String chatCode, String message, String result) {
        log.info("MessageServiceImpl recordHistory userCode:[{}], chatCode:[{}], message:[{}], result:[{}]", userCode, chatCode, message, result);
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
        UserPO chatUser= userMapper.getUserByCode(chatCode);
        UserPO user= userMapper.getUserByCode(historyList.get(0).getUserCode());
        for (MessagesPO messagesPO : historyList) {
            MessagesAO messagesAO =  ConvertMapping.messagesPO2MessagesResponseAO(messagesPO);
            messagesAO.setUserHeadshot(user.getHeadshot());
            messagesAO.setChatHeadshot(chatUser.getHeadshot());
            messagesAO.setChatName(chatUser.getUsername());
            messagesAO.setUsername(user.getUsername());
            list.add(messagesAO);
        }
        int total = messageMapper.getToalMessages(chatCode);
        MessagesResponseAO response = new MessagesResponseAO(list, total);

        return response;
    }

}
