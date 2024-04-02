package chatgptserver.service.impl;

import chatgptserver.Common.MailUtil;
import chatgptserver.Mapping.ConvertMapping;
import chatgptserver.bean.ao.*;
import chatgptserver.bean.po.ChatPO;
import chatgptserver.bean.po.UserFeedbackPO;
import chatgptserver.bean.po.UserPO;
import chatgptserver.dao.UserMapper;
import chatgptserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
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

    @Override
    public JsonResult login(UserAO request) {
        log.info("UserServiceImpl login request:[{}]", request);
        // 查询数据库是否存在该用户（有则直接登录，并生成token）
        UserPO userPO = userMapper.getUserByEmail(request.getEmail());
        if (Objects.isNull(userPO)) {
            // 判断邮箱格式
            String userEmail = request.getEmail();
            if (userEmail != null && !userEmail.equals("") && userEmail.length() > 7
                    && userEmail.substring(userEmail.length() - 7, userEmail.length()).equals("@qq.com")) {
                // 发邮件
                String verifyCode = MailUtil.createVerifyCode();
                log.info("UserServiceImpl login verifyCode:[{}]", verifyCode);
                MailUtil.sendEmailMessage("ChatGPT集成平台注册验证码", request.getEmail(), verifyCode);
            }

        } else {

        }

        // 没有，则注册

        // 生成token，存token进redis

        return null;
    }

//    @Override
//    public JsonResult userLogin(UserDTO userDTO) {
//        System.out.println( userDTO );
//        // 先检查是否存在该学号
//        if ( !haveThisStudentId( userDTO.getStudentId() ) ) {
//            return JsonResult.error("There is no this studentId! Not CQUT student, can't login! ");
//        }
//        String mdCode = md5.encrypt(userDTO.getPassword());
//        UserDTO user = userDao.getUserByStudentId(userDTO.getStudentId());
//        if( ObjectUtil.isEmpty(user) ) {
//            userDTO.setPassword(mdCode);
//            userDao.insert(userDTO);
//        } else {
//            System.out.println("原密码：" + userDTO.getPassword());
//            System.out.println( "加密后的密码 ：" + mdCode );
//            // 判断密码是否正确
//            if ( !user.getPassword().equals(mdCode) ) {
//                log.info("UserServiceImpl userLogin -> result : (password is not right, can't login!)");
//                return JsonResult.success("password is not right, can't login! ");
//            }
//        }
//        UserDTO userDTO1 = userDao.getUserByStudentId(userDTO.getStudentId());
//        UserVO userVO = serverMapper.userDTO2UserVO(userDTO1);
//        // 登录成功后，生成token，生成 权限token
//        String token = jwtTokenManager.createToken(userDTO1);
//        userVO.setToken( token );
//        userDao.updateUserToken( userDTO.getStudentId(), token );
//        return JsonResult.success(userVO);
//    }
}
