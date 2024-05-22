package chatgptserver.Mapping;

import chatgptserver.bean.ao.*;
import chatgptserver.bean.ao.ppt.*;
import chatgptserver.bean.dto.ppt.PptColor;
import chatgptserver.bean.po.*;
import chatgptserver.dao.UserMapper;
import chatgptserver.service.UserService;
import chatgptserver.utils.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Objects;


/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 16:00
 */
@Slf4j
public class ConvertMapping {

    @Autowired
    private UserMapper userMapper;

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
        defaultAO.setKind(defaultPO.getKind());
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
        pptPO.setDescription(request.getDescription());
        pptPO.setPptUrl(pptUrl);
        pptPO.setCoverUrl(coverUrl);
        pptPO.setUserCode(request.getUserCode());

        return pptPO;
    }

    public static PptAO pptPO2PptAO(PptPO pptPO) {
        if (Objects.isNull(pptPO)) {
            return null;
        }
        PptAO pptAO = new PptAO();
        pptAO.setPptCode(pptPO.getPptCode());
        pptAO.setPptUrl(pptPO.getPptUrl());
        pptAO.setFirstKind(pptPO.getFirstKind());
        pptAO.setSecondKind(pptPO.getSecondKind());
        pptAO.setUserCode(pptPO.getUserCode());
        pptAO.setTitle(pptPO.getTitle());
        pptAO.setCoverUrl(pptPO.getCoverUrl());
        pptAO.setCreateTime(pptPO.getCreateTime());
        pptAO.setUpdateTime(pptPO.getUpdateTime());
        pptAO.setDescription(pptPO.getDescription());
        pptAO.setScore(pptPO.getScore());
        pptAO.setCollectAmount(pptPO.getCollectAmount());
        pptAO.setSeeAmount(pptPO.getSeeAmount());
        pptAO.setCommentAmount(pptPO.getCommentAmount());

        return pptAO;
    }

    public static FolderAO folderPO2FolderAO(FolderPO folderPO) {
        if (Objects.isNull(folderPO)) {
            return null;
        }
        FolderAO folderAO = new FolderAO();
        folderAO.setFolder(folderPO.getFolder());
        folderAO.setFolderCode(folderPO.getFolderCode());
        folderAO.setUserCode(folderPO.getUserCode());
        folderAO.setCreateTime(folderPO.getCreateTime());
        folderAO.setUpdateTime(folderPO.getUpdateTime());
        folderAO.setIsDefault(folderPO.getIsDefault());

        return folderAO;
    }

    public static CommentAO commentPO2CommentAO(CommentPO commentPO) {
        if (Objects.isNull(commentPO)) {
            return null;
        }
        CommentAO commentAO = new CommentAO();
        commentAO.setCommentCode(commentPO.getCommentCode());
        commentAO.setUserCode(commentPO.getUserCode());
        commentAO.setPptCode(commentPO.getPptCode());
        commentAO.setContent(commentPO.getContent());
        commentAO.setReplyAmount(commentPO.getReplyAmount());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(commentPO.getCreateTime());
        String[] times = date.split(" ")[0].split("-");
        String createTime = times[1] + "-" + times[2];
        commentAO.setCreateTime(createTime);

        return commentAO;
    }

    public static ReplyAO replyPO2ReplyAO(ReplyPO replyPO) {
        if (Objects.isNull(replyPO)) {
            return null;
        }
        ReplyAO replyAO = new ReplyAO();
        replyAO.setReplyCode(replyPO.getReplyCode());
        replyAO.setCommentCode(replyPO.getCommentCode());
        replyAO.setUserCode(replyPO.getUserCode());
        replyAO.setPptCode(replyPO.getPptCode());
        replyAO.setContent(replyPO.getContent());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(replyPO.getCreateTime());//注意这里返回的是string类型
        String[] times = date.split(" ")[0].split("-");
        String createTime = times[1] + "-" + times[2];
        replyAO.setCreateTime(createTime);

        return replyAO;
    }
}
