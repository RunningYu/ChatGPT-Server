package chatgptserver.service.impl;

import chatgptserver.Common.MailUtil;
import chatgptserver.Mapping.ConvertMapping;
import chatgptserver.bean.ao.*;
import chatgptserver.bean.po.ChatPO;
import chatgptserver.bean.po.UserFeedbackPO;
import chatgptserver.bean.po.UserPO;
import chatgptserver.dao.UserMapper;
import chatgptserver.service.UserService;
import chatgptserver.utils.JwtUtils;
import chatgptserver.utils.MD5Util;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:31
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private Cache<String, Object> caffeineCache;

    @Autowired
    private JwtUtils jwtUtils;

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
        userMapper.updateChatCode(chatCode, chatPO.getId());
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

    @Override
    public JsonResult login(UserAO request) {
        log.info("UserServiceImpl login request:[{}]", request);
        // 查询数据库是否存在该用户（有则直接登录，并生成token）
        UserPO userPO = userMapper.getUserByEmail(request.getEmail());
        // 注册
        if (Objects.isNull(userPO)) {
            // 判断邮箱格式
            String userEmail = request.getEmail();
            if (userEmail != null && !userEmail.equals("") && userEmail.length() > 7
                    && userEmail.substring(userEmail.length() - 7, userEmail.length()).equals("@qq.com")) {
                if (request.getUserVerifyCode() != null && !request.getUserVerifyCode().equals("")) {
                        String md5Code = MD5Util.encrypt(request.getUserVerifyCode());
                        if (md5Code.equals(request.getVerifyCode())) {
                            request.setPassword(MD5Util.encrypt(request.getPassword()));
                            UserPO user = ConvertMapping.userAO2UserPO(request);
                            int id = userMapper.userAdd(user);
                            String userCode = "user_" + user.getId();
                            user.setUserCode(userCode);
                            userPO = user;
                            log.info("UserServiceImpl login user:[{}]", user);
                            userMapper.updateUserCode(userCode, user.getId());
                        } else {
                            throw new RuntimeException("输入的验证码错误");
                        }
                } else {
                    throw new RuntimeException("请输入验证码");
                }
            } else {
                throw new RuntimeException("邮箱格式不正确!");
            }
        }
        // 登录
        else {
            String md5Password = MD5Util.encrypt(request.getPassword());
            if (!md5Password.equals(userPO.getPassword())) {
                throw new RuntimeException("密码错误！");
            }
        }
        // 生成token，存token进redis
        String token = jwtUtils.createToken(userPO);
        UserLoginReqAO userLoginReqAO = ConvertMapping.userPO2UserLoginReqAO(userPO);
        userLoginReqAO.setToken(token);
        caffeineCache.put(token, userLoginReqAO);

        return JsonResult.success(userLoginReqAO);
    }

    @Override
    public String sendEmailVerifyCode(String email) {
        log.info("UserServiceImpl sendEmailVerifyCode email:[{}]", email);
        // 判断邮箱格式
        if (email != null && !email.equals("") && email.length() > 7
                && email.substring(email.length() - 7, email.length()).equals("@qq.com")) {
            // 发邮件
            String verifyCode = MailUtil.createVerifyCode();
            log.info("UserServiceImpl sendEmailVerifyCode verifyCode:[{}]", verifyCode);
            MailUtil.sendEmailMessage("ChatGPT集成平台注册验证码", email, verifyCode);

            return MD5Util.encrypt(email);
        } else {
            throw new RuntimeException("邮箱格式错误！");
        }
    }

}
