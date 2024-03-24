package chatgptserver.Mapping;

import chatgptserver.bean.ao.MessagesAO;
import chatgptserver.bean.po.MessagesPO;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 16:00
 */
@Slf4j
public class ConvertMapping {


    public static MessagesAO messagesPO2MessagesResponseAO(MessagesPO messagesPO) {
        if (Objects.isNull(messagesPO)) {
            return null;
        }
        MessagesAO messagesAO = new MessagesAO();
        messagesAO.setChatCode(messagesPO.getChatCode());
        messagesAO.setUserCode(messagesPO.getUserCode());
        messagesAO.setQuestion(messagesPO.getQuestion());
        messagesAO.setReplication(messagesPO.getReplication());
        messagesAO.setCreateTime(messagesPO.getCreateTime());

        return messagesAO;
    }
}
