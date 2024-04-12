package chatgptserver.Mapping;

import chatgptserver.bean.ao.*;
import chatgptserver.bean.po.*;
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
        messagesAO.setImage(messagesPO.getImage());
        messagesAO.setQuestion(messagesPO.getQuestion());
        messagesAO.setReplication(messagesPO.getReplication());
        messagesAO.setCreateTime(messagesPO.getCreateTime());

        return messagesAO;
    }

    public static ChatPO ChatAddRequestAO2ChatPO(ChatAddRequestAO request) {
        if (Objects.isNull(request)) {
            return null;
        }
        ChatPO chatPO = new ChatPO();
        chatPO.setUserCode(request.getUserCode());
        chatPO.setChatName(request.getChatName());
        chatPO.setGptCode(request.getGptCode());
        chatPO.setFunctionCode(request.getFunctionCode());

        return chatPO;
    }

    public static UserFeedbackAO userFeedbackPO2UserFeedbackAO(UserFeedbackPO feedbackPO) {
        if (Objects.isNull(feedbackPO)) {
            return null;
        }
        UserFeedbackAO feedbackAO = new UserFeedbackAO();
        feedbackAO.setUserCode(feedbackPO.getUserCode());
        feedbackAO.setCreateTime(feedbackPO.getCreateTime());
        feedbackAO.setContent(feedbackPO.getContent());

        return feedbackAO;
    }

    public static UserPO userAO2UserPO(UserAO userAO) {
        if (Objects.isNull(userAO)) {
            return null;
        }
        UserPO userPO = new UserPO();
        userPO.setUserCode(userAO.getUserCode());
        userPO.setUsername(userAO.getUsername());
        userPO.setEmail(userAO.getEmail());
        userPO.setPassword(userAO.getPassword());
        userPO.setPhone(userAO.getPhone());

        return userPO;
    }

    public static UserLoginReqAO userPO2UserLoginReqAO(UserPO userPO) {
        if (Objects.isNull(userPO)) {
            return null;
        }
        UserLoginReqAO reqAO = new UserLoginReqAO();
        reqAO.setUserCode(userPO.getUserCode());
        reqAO.setUsername(userPO.getUsername());
        reqAO.setEmail(userPO.getEmail());
        reqAO.setPassword(userPO.getPassword());
        reqAO.setHeadshot(userPO.getHeadshot());
        reqAO.setPhone(userPO.getPhone());

        return reqAO;
    }

    public static ChatAO chatPO2ChatAO(ChatPO chatPO) {
        if (Objects.isNull(chatPO)) {
            return null;
        }
        ChatAO chatAO = new ChatAO();
        chatAO.setChatCode(chatPO.getChatCode());
        chatAO.setChatName(chatPO.getChatName());
        chatAO.setGptCode(chatPO.getGptCode());
        chatAO.setFunctionCode(chatPO.getFunctionCode());
        chatAO.setUserCode(chatPO.getUserCode());
        chatAO.setCreateTime(chatPO.getCreateTime());

        return chatAO;
    }

    public static ChatFunctionAO chatFunctionPO2ChatFunctionAO(ChatFunctionPO chatFunctionPO) {
        if (Objects.isNull(chatFunctionPO)) {
            return null;
        }
        ChatFunctionAO functionAO = new ChatFunctionAO();
        functionAO.setFunctionName(chatFunctionPO.getFunctionName());
        functionAO.setGptCode(chatFunctionPO.getGptCode());
        functionAO.setFunctionCode(chatFunctionPO.getFunctionCode());
        functionAO.setCreateTime(chatFunctionPO.getCreateTime());

        return functionAO;
    }
}
