package chatgptserver.Mapping;

import chatgptserver.bean.ao.*;
import chatgptserver.bean.ao.ppt.PptUploadRequestAO;
import chatgptserver.bean.dto.ppt.PptColor;
import chatgptserver.bean.po.*;
import chatgptserver.utils.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;


/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 16:00
 */
@Slf4j
public class ConvertMapping {

    @Autowired
    private MinioUtil minioUtil;


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
        messagesAO.setReplyImage(messagesPO.getReplyImage());
        messagesAO.setIsDefault(messagesPO.getIsDefault());
        messagesAO.setCreateTime(messagesPO.getCreateTime());
        messagesAO.setReplyTime(messagesPO.getUpdateTime());

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

    public static ChatAO chatAddRequestAO2ChatAO(ChatAddRequestAO addRequestAO) {
        if (Objects.isNull(addRequestAO)) {
            return null;
        }
        ChatAO chatAO = new ChatAO();
        chatAO.setChatName(addRequestAO.getChatName());
        chatAO.setGptCode(addRequestAO.getGptCode());
        chatAO.setFunctionCode(addRequestAO.getFunctionCode());
        chatAO.setUserCode(addRequestAO.getUserCode());

        return chatAO;
    }

    public static DefaultAO defaultPO2DefaultAO(DefaultPO defaultPO) {
        if (Objects.isNull(defaultPO)) {
            return null;
        }
        DefaultAO defaultAO = new DefaultAO();
        defaultAO.setName(defaultPO.getName());
        defaultAO.setContent(defaultPO.getContent());
        defaultAO.setReplication(defaultPO.getReplication());
        defaultAO.setFunctionCode(defaultPO.getFunctionCode());
        defaultAO.setCreateTime(defaultPO.getCreateTime());

        return defaultAO;
    }

    public static PptColor pptColorPO2PptColor(PptColorPO pptColorPO) {
        if (Objects.isNull(pptColorPO)) {
            return null;
        }
        PptColor pptColor = new PptColor();
        pptColor.setKey(pptColorPO.getColorKey());
        pptColor.setName(pptColorPO.getColorName());
        pptColor.setThumbnail(pptColorPO.getThumbnail());

        return pptColor;
    }

    public static PptPO buildPptPO(PptUploadRequestAO request, String pptUrl, String coverUrl) {
        if (Objects.isNull(request)) {
            return null;
        }
        PptPO pptPO = new PptPO();
        pptPO.setFirstKind(request.getFirstKind());
        pptPO.setSecondKind(request.getSecondKind());
        pptPO.setTitle(request.getTitle());
        pptPO.setPptUrl(pptUrl);
        pptPO.setCoverUrl(coverUrl);
        pptPO.setUserCode(request.getUserCode());

        return pptPO;
    }
}
