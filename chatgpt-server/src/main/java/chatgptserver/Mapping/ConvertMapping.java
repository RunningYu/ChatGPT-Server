package chatgptserver.Mapping;

import chatgptserver.bean.po.MessagesPO;
import chatgptserver.bean.po.UserPO;
import chatgptserver.netty.command.ChatMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 16:00
 */
@Slf4j
public class ConvertMapping {


    public static MessagesPO buildMessage(ChatMessage chat, UserPO sendUserPO, UserPO targetUserPO) {

        if (Objects.isNull(chat)) {
            throw new RuntimeException("客户端信息有错误！");
        }
        if (Objects.isNull(sendUserPO)) {
            throw new RuntimeException("未找到发送方的实体信息，可能是senderCode有误");
        }
        if (Objects.isNull(targetUserPO)) {
            throw new RuntimeException("未找到接受方的实体信息，可能是targetCode有误");
        }

        MessagesPO messagesPO = new MessagesPO();
        messagesPO.setUserCode(chat.getSenderCode());
        messagesPO.setChatCode(chat.getTargetCode());
        messagesPO.setQuestion(chat.getContent());
        messagesPO.setUsername(sendUserPO.getUsername());
        messagesPO.setChatName(targetUserPO.getUsername());

        return messagesPO;
    }
}
