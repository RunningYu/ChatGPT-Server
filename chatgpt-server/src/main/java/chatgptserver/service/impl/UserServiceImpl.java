package chatgptserver.service.impl;

import chatgptserver.Mapping.ConvertMapping;
import chatgptserver.bean.ao.ChatAddRequestAO;
import chatgptserver.bean.ao.UserFeedbackAO;
import chatgptserver.bean.ao.UserFeedbackListResponseAO;
import chatgptserver.bean.ao.UserFeedbackRequestAO;
import chatgptserver.bean.po.ChatPO;
import chatgptserver.bean.po.UserFeedbackPO;
import chatgptserver.bean.po.UserPO;
import chatgptserver.dao.UserMapper;
import chatgptserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:31
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserPO getUserByCode(String senderCode) {
        log.info("UserServiceImpl getUserByCode senderCode:[{}]", senderCode);
        UserPO userPO = userMapper.getUserByCode(senderCode);
        log.info("UserServiceImpl getUserByCode userPO:[{}]", userPO);

        return userPO;
    }

    @Override
    public Map<String, String> createNewChat(ChatPO chatPO) {
        log.info("UserServiceImpl createNewChat chatPO:[{}]", chatPO);
        int id = userMapper.newChat(chatPO);
        String chatCode = "chat_" + chatPO.getId();
        userMapper.updateUserCode(chatCode, chatPO.getId());
        Map<String, String> response = new HashMap<>();
        response.put("chatCode", chatCode);
        log.info("UserServiceImpl createNewChat response:[{}]", response);

        return response;
    }

    @Override
    public void chatUserFeedback(UserFeedbackRequestAO request) {
        log.info("UserServiceImpl chatUserFeedback request:[{}]", request);
        userMapper.chatUserFeedback(request);
    }

    @Override
    public UserFeedbackListResponseAO chatUserFeedbackList(int page, int size) {
        log.info("UserServiceImpl chatUserFeedbackList page:[{}], size:[{}]", page, size);
        page = (page > 0) ? page : 1;
        int startIndex = (page - 1 ) * size;
        List<UserFeedbackPO> feedbackPOS = userMapper.chatUserFeedbackList(startIndex, size);
        List<UserFeedbackAO> list = new ArrayList<>();
        for (UserFeedbackPO feedback : feedbackPOS) {
            UserPO userPO = userMapper.getUserByCode(feedback.getUserCode());
            UserFeedbackAO feedbackAO = ConvertMapping.userFeedbackPO2UserFeedbackAO(feedback);
            feedbackAO.setUserName(userPO.getUsername());
            feedbackAO.setHeadshot(userPO.getHeadshot());
            list.add(feedbackAO);
        }
        int total = userMapper.getTotalOfchatUserFeedbackList();
        UserFeedbackListResponseAO response = new UserFeedbackListResponseAO(list, total);

        return response;
    }
}
