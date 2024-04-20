package chatgptserver.service.impl;

import chatgptserver.Common.MailUtil;
import chatgptserver.Common.SseUtils;
import chatgptserver.Mapping.ConvertMapping;
import chatgptserver.bean.ao.*;
import chatgptserver.bean.po.*;
import chatgptserver.dao.GptMapper;
import chatgptserver.dao.UserMapper;
import chatgptserver.service.MessageService;
import chatgptserver.service.UserService;
import chatgptserver.utils.JwtUtils;
import chatgptserver.utils.MD5Util;
import chatgptserver.utils.StorageUtils;
import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:31
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Lazy
    @Autowired
    private MessageService messageService;

    @Resource
    private RabbitTemplate rabbitTemplate;

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
    public Map<String, String> createNewChat(ChatAddRequestAO request) {
        log.info("UserServiceImpl createNewChat request:[{}]", request);
        ChatPO chatPO = ConvertMapping.ChatAddRequestAO2ChatPO(request);
        int id = userMapper.newChat(chatPO);
        String chatCode = "chat_" + chatPO.getId();
        userMapper.updateChatCode(chatCode, chatPO.getId());
        Map<String, String> response = new HashMap<>();
        response.put("chatCode", chatCode);
        response.put("chatName", chatPO.getChatName());
        response.put("functionCode", chatPO.getFunctionCode());
        log.info("UserServiceImpl createNewChat response:[{}]", response);
        if (request.getContent() != null && !"".equals(request.getContent())) {
            // 新增默认预设
            messageService.recordHistory(request.getUserCode(), chatCode, request.getContent(), request.getReplication());
        }

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
    public JsonResult register(UserAO request) {
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
            Boolean emailIsLegal = (userEmail != null && userEmail.matches("^[1-9][0-9]{4,10}@qq\\.com$"));
            if (emailIsLegal) {
                if (request.getVerifyCode() == null || "".equals(request.getVerifyCode())) {
                    return JsonResult.error(500, "验证码错误");
                }
                if (!request.getPreEmail().equals(MD5Util.encrypt(request.getEmail()))) {
                    log.info("UserServiceImpl register 邮箱有改动，不一致！");
                    return JsonResult.error("邮箱有改动，不一致！");
                }
                if (request.getUserVerifyCode() != null && !request.getUserVerifyCode().equals("")) {
                    String md5Code = MD5Util.encrypt(request.getUserVerifyCode());
                    if (!md5Code.equals(request.getVerifyCode())) {
                        log.info("UserServiceImpl login 输入的验证码错误");
                        return JsonResult.error("输入的验证码错误");
                    }
                    //数字+字母，6-20位. 返回true 否则false
                    boolean isLegal = request.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$");
                    if (isLegal == false) {
                        log.info("UserServiceImpl login 密码请输入数字+字母，6-20位");
                        return JsonResult.error("密码请输入数字+字母，6-20位");
                    }
                    if (!request.getPassword().equals(request.getAgainPassword())) {
                        log.info("UserServiceImpl login 前后密码不对应");
                        return JsonResult.error("前后密码不对应");
                    }
                    request.setPassword(MD5Util.encrypt(request.getPassword()));
                    UserPO user = ConvertMapping.userAO2UserPO(request);
                    int id = userMapper.userAdd(user);
                    String userCode = "user_" + user.getId();
                    user.setUserCode(userCode);
                    userPO = user;
                    log.info("UserServiceImpl login user:[{}]", user);
                    userMapper.updateUserCode(userCode, user.getId());
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
            log.info("UserServiceImpl login 该账号注册过了！");
            return JsonResult.error(500, "该账号注册过了！");
        }
        // 生成token，存token进redis
        String token = jwtUtils.createToken(userPO);
        UserLoginReqAO userLoginReqAO = ConvertMapping.userPO2UserLoginReqAO(userPO);
        userLoginReqAO.setToken(token);
        caffeineCache.put(token, userLoginReqAO);
        log.info("UserServiceImpl login 注册成功！");

        return JsonResult.success(200, "注册成功！");
    }

    /**
     * 通过密码的登录
     */
    @Override
    public JsonResult loginByPassword(UserAO request) {
        log.info("UserServiceImpl loginByPassword request:[{}]", request);
        // 查询数据库是否存在该用户（有则直接登录，并生成token）
        UserPO userPO = userMapper.getUserByEmail(request.getEmail());
        if (Objects.isNull(userPO)) {
            log.info("UserServiceImpl loginByPassword email:[{}] 该账号还没注册过，请先注册！", request.getEmail());
            return JsonResult.error("该账号还没注册过，请先注册！");
        }
        // 登录
        else {
            String md5Password = MD5Util.encrypt(request.getPassword());
            if (!md5Password.equals(userPO.getPassword())) {
                log.info("UserServiceImpl loginByPassword 密码错误！");
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

    /**
     * 通过验证码登录 或 注册
     */
    @Override
    public JsonResult loginByVerifyCode(UserAO request) {
        log.info("UserServiceImpl loginByVerifyCode request:[{}]", request);
        Boolean emailIsLegal = (request.getEmail() != null && request.getEmail().matches("^[1-9][0-9]{4,10}@qq\\.com$"));
        if (emailIsLegal == false) {
            log.info("UserServiceImpl loginByVerifyCode 邮箱格式不正确！");
            return JsonResult.error("邮箱格式不正确");
        }
        if (request.getVerifyCode() == null || "".equals(request.getVerifyCode())) {
            return JsonResult.error(500, "验证码错误");
        }
        if (!request.getPreEmail().equals(MD5Util.encrypt(request.getEmail()))) {
            log.info("UserServiceImpl loginByVerifyCode 邮箱有改动，不一致！");
            return JsonResult.error("邮箱有改动，不一致！");
        }
        // 查询数据库是否存在该用户（有则直接登录，并生成token）
        String md5Code = MD5Util.encrypt(request.getUserVerifyCode());
        if (!md5Code.equals(request.getVerifyCode())) {
            log.info("UserServiceImpl loginByVerifyCode 验证码错误！");
            return JsonResult.error(500, "验证码错误！");
        }
        UserPO userPO = userMapper.getUserByEmail(request.getEmail());
        // 如果没有注册过，则注册
        if (Objects.isNull(userPO)) {
            request.setPassword(MD5Util.encrypt("ChatGPT12345678"));
            request.setUsername(request.getEmail().split("@")[0]);
            UserPO user = ConvertMapping.userAO2UserPO(request);
            int id = userMapper.userAdd(user);
            String userCode = "user_" + user.getId();
            user.setUserCode(userCode);
            userPO = user;
            log.info("UserServiceImpl loginByVerifyCode user:[{}]", user);
            userMapper.updateUserCode(userCode, user.getId());
        }
        // 生成token，存token进redis
        String token = jwtUtils.createToken(userPO);
        UserLoginReqAO userLoginReqAO = ConvertMapping.userPO2UserLoginReqAO(userPO);
        userLoginReqAO.setToken(token);
        caffeineCache.put(token, userLoginReqAO);

        return JsonResult.success(userLoginReqAO);
    }

    @Override
    public JsonResult passwordForget(UserAO request) {
        log.info("UserServiceImpl passwordForget request:[{}]", request);
        UserPO userPO = userMapper.getUserByEmail(request.getEmail());
        Boolean emailIsLegal = (request.getEmail() != null && request.getEmail().matches("^[1-9][0-9]{4,10}@qq\\.com$"));
        if (emailIsLegal == false) {
            log.info("UserServiceImpl passwordForget 邮箱格式不正确！");
            return JsonResult.error(500, "邮箱格式不正确");
        }
        if (request.getVerifyCode() == null || "".equals(request.getVerifyCode())) {
            return JsonResult.error(500, "验证码错误");
        }
        if (!request.getPreEmail().equals(MD5Util.encrypt(request.getEmail()))) {
            log.info("UserServiceImpl passwordForget 邮箱有改动，不一致！");
            return JsonResult.error(500, "邮箱有改动，不一致！");
        }
        if (Objects.isNull(userPO)) {
            log.info("UserServiceImpl passwordForget email:[{}] 该账号还没注册过，请先注册！", request.getEmail());

            return JsonResult.error(500, "该账号还没注册过，请先注册！");
        }
        if (request.getUserVerifyCode() != null && !request.getUserVerifyCode().equals("")) {
            String md5Code = MD5Util.encrypt(request.getUserVerifyCode());
            if (!md5Code.equals(request.getVerifyCode())) {
                log.info("UserServiceImpl passwordForget 输入的验证码错误");
                return JsonResult.error(500, "输入的验证码错误");
            }
            //数字+字母，6-20位. 返回true 否则false
            boolean isLegal = request.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$");
            if (isLegal == false) {
                log.info("UserServiceImpl passwordForget 密码请输入数字+字母，6-20位");
                return JsonResult.error(500, "密码请输入数字+字母，6-20位");
            }
            if (!request.getPassword().equals(request.getAgainPassword())) {
                log.info("UserServiceImpl passwordForget 前后密码不对应");
                return JsonResult.error(500, "前后密码不对应");
            }
            String newPassword = MD5Util.encrypt(request.getPassword());
            userMapper.updatePassword(newPassword, request.getEmail());
        }

        return JsonResult.success(200, "密码重置成功！");
    }

    @Override
    public JsonResult loginByScan(String pid, String did) {
        // 查询数据库是否存在该用户（有则直接登录，并生成token）
        UserPO havaUser = userMapper.getUserByEmail(did);
        // 如果没有注册过，则注册
        if (Objects.isNull(havaUser)) {
            UserPO user = new UserPO();
            user.setEmail(did);
            user.setUsername(did);
            int id = userMapper.userAdd(user);
            String userCode = "user_" + user.getId();
            user.setUserCode(userCode);
            havaUser = user;
            log.info("UserServiceImpl loginByScan user:[{}]", user);
            userMapper.updateUserCode(userCode, user.getId());
        }
        // 生成token，存token进redis
        String token = jwtUtils.createToken(havaUser);
        UserLoginReqAO userLoginReqAO = ConvertMapping.userPO2UserLoginReqAO(havaUser);
        userLoginReqAO.setToken(token);
        caffeineCache.put(token, userLoginReqAO);
        // 存进中间缓存层
        StorageUtils.loginMap.put(pid, JsonResult.success(userLoginReqAO));

        return JsonResult.success(userLoginReqAO);
    }

    @Override
    public JsonResult loginByScanListen(Long threadId, String pid) {
        JsonResult response = null;
        while (true) {
            System.out.println("---------->" + StorageUtils.loginMap);
            // 监听pid的登录
            if (StorageUtils.loginMap.containsKey(pid)) {
                response = StorageUtils.loginMap.get(pid);
                StorageUtils.loginMap.remove(pid);
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }

        return response;
    }

    @Override
    public JsonResult sendEmailVerifyCode(String email) {
        log.info("UserServiceImpl sendEmailVerifyCode email:[{}]", email);
        Boolean emailIsLegal = (email != null && email.matches("^[1-9][0-9]{4,10}@qq\\.com$"));
        if (emailIsLegal == false) {
            log.info("UserServiceImpl sendEmailVerifyCode 邮箱格式不正确！");
            return JsonResult.error("邮箱格式不正确");
        }
        // 发邮件
        String verifyCode = MailUtil.createVerifyCode();
        log.info("UserServiceImpl sendEmailVerifyCode verifyCode:[{}]", verifyCode);
        try {
            MailUtil.sendSimpleMail("ChatGPT集成平台注册验证码", email, verifyCode);
        } catch (Exception e) {
            return JsonResult.error(500, "发送邮件失败！");
        }
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("preEmail", MD5Util.encrypt(email));
        responseMap.put("verifyCode", MD5Util.encrypt(verifyCode));
        log.info("UserServiceImpl sendEmailVerifyCode responseMap:[{}]", responseMap);

        return JsonResult.success(responseMap);
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
            return JsonResult.error(401, "token失效或过期");
        }
        UserPO userPO = userMapper.getUserByCode(userCode);

        return JsonResult.success(userPO);
    }

    @Override
    public JsonResult userInfoUpdate(String token, UserAO request) {
        log.info("UserServiceImpl userInfoUpdate token:[{}], request:[{}]", token, request);
        if (token == null || "".equals(token)) {
            return JsonResult.error(401, "请先登录");
        }
        String userCode = getUserCodeByToken(token);
        if (userCode == null) {
            return JsonResult.error(401, "token失效或过期");
        }
        Boolean emailIsLegal = request.getEmail().matches("^[1-9][0-9]{4,10}@qq\\.com$");
        if (emailIsLegal == false) {
            log.info("UserServiceImpl userInfoUpdate 邮箱格式不正确！");
            return JsonResult.error("邮箱格式不正确");
        }
        if (!(request.getUsername() != null && request.getUsername().length() <= 50)) {
            log.info("UserServiceImpl userInfoUpdate 用户名不能为空，长度最大为50");
            return JsonResult.error("用户名不能为空，长度最大为50");
        }
        // 数字+字母，6-20位. 返回true 否则false
        boolean isLegal = request.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$");
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
