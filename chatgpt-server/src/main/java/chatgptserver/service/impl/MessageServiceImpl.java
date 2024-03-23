package chatgptserver.service.impl;

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
}
