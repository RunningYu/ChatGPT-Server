package chatgptserver.service.impl;

import chatgptserver.Common.MailUtil;
import chatgptserver.Mapping.ConvertMapping;
import chatgptserver.bean.ao.*;
import chatgptserver.bean.po.*;
import chatgptserver.dao.GptMapper;
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
    private GptMapper gptMapper;

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
        response.put("chatName", chatPO.getChatName());
        response.put("functionCode", chatPO.getFunctionCode());
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
        if (!(request.getUsername() != null && request.getUsername().length() <= 50)) {
            log.info("UserServiceImpl login 用户名不能为空，长度最大为50");
            return JsonResult.error("用户名不能为空，长度最大为50");
        }
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
                        log.info("UserServiceImpl login 输入的验证码错误");
                        return JsonResult.error("输入的验证码错误");
                    }
                    //数字+字母，6-20位. 返回true 否则false
                    boolean isLegal = request.getPassword().matches("/^(?=.*[0-9])(?=.*[a-zA-Z])[0-9a-zA-Z]{6,20}$/");
                    if (isLegal == false) {
                        log.info("UserServiceImpl login 密码请输入数字+字母，6-20位");
                        return JsonResult.error("密码请输入数字+字母，6-20位");
                    }
                    if (!request.getPassword().equals(request.getAgainPassword())) {
                        log.info("UserServiceImpl login 前后密码不对应");
                        return JsonResult.error("前后密码不对应");
                    }
                } else {
                    log.info("UserServiceImpl login 请输入验证码");
                    return JsonResult.error("请输入验证码");
                }
            } else {
                log.info("UserServiceImpl login 邮箱格式不正确！");
                return JsonResult.error("邮箱格式不正确！");
            }
        }
        // 登录
        else {
            String md5Password = MD5Util.encrypt(request.getPassword());
            if (!md5Password.equals(userPO.getPassword())) {
                log.info("UserServiceImpl login 密码错误！");
                return JsonResult.error("密码错误！");
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
//            MailUtil.sendEmailMessage("ChatGPT集成平台注册验证码", email, verifyCode);
            try {
                MailUtil.sendSimpleMail("ChatGPT集成平台注册验证码", email, verifyCode);
            } catch (Exception e) {
                return "发送邮件失败！";
            }

            return MD5Util.encrypt(verifyCode);
        } else {
            return "邮箱格式错误！";
        }
    }

    @Override
    public void chatDelete(String chatCode) {
        userMapper.chatDelete(chatCode);
    }

    @Override
    public String getUserCodeByToken(String token) {
        String userCode = null;
        if (token != null && !"".equals(token)) {
            UserPO userPO = jwtUtils.getUserFromToken(token);
            userCode = userPO.getUserCode();
        }
        return userCode;
    }

    @Override
    public JsonResult gptChatFunctionList(String gptCode) {
        List<ChatFunctionPO> list = userMapper.gptChatFunctionList(gptCode);
        List<ChatFunctionAO> response = new ArrayList<>();
        GptPO gptPO = gptMapper.getGptByCode(gptCode);
        for (ChatFunctionPO chatFunctionPO : list) {
            ChatFunctionAO chatFunctionAO = ConvertMapping.chatFunctionPO2ChatFunctionAO(chatFunctionPO);
            chatFunctionAO.setGptName(gptPO.getGptName());
            response.add(chatFunctionAO);

        }

        return JsonResult.success(response);
    }

    @Override
    public JsonResult userInfo(String userCode) {
        log.info("UserServiceImpl userInfo userCode:[{}]", userCode);
        if (userCode == null) {
            return JsonResult.error("token失效或过期");
        }
        UserPO userPO = userMapper.getUserByCode(userCode);

        return JsonResult.success(userPO);
    }

    @Override
    public JsonResult userInfoUpdate(String token, UserAO request) {
        log.info("UserServiceImpl userInfoUpdate token:[{}], request:[{}]", token, request);
        if (token == null || "".equals(token)) {
            return JsonResult.error("请先登录");
        }
        String userCode = getUserCodeByToken(token);
        if (userCode == null) {
            return JsonResult.error("token失效或过期");
        }
        if (!(request.getUsername() != null && request.getUsername().length() <= 50)) {
            log.info("UserServiceImpl userInfoUpdate 用户名不能为空，长度最大为50");
            return JsonResult.error("用户名不能为空，长度最大为50");
        }
        // 数字+字母，6-20位. 返回true 否则false
        boolean isLegal = request.getPassword().matches("/^(?=.*[0-9])(?=.*[a-zA-Z])[0-9a-zA-Z]{6,20}$/");
        if (isLegal == false) {
            log.info("UserServiceImpl userInfoUpdate 密码请输入数字+字母，6-20位");
            return JsonResult.error("密码请输入数字+字母，6-20位");
        }
        request.setUserCode(userCode);
        request.setPassword(MD5Util.encrypt(request.getPassword()));
        userMapper.userInfoUpdate(request);

        return JsonResult.success("update successfully");
    }

}
