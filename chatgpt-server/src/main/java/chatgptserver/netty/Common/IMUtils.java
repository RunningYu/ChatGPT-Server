package chatgptserver.netty.Common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2023/5/23
 */
@Component
@Slf4j
public class IMUtils {

//    @Autowired
//    private ServerMapper serverMapper;
//
//    @Autowired
//    private UserDao userDao;
//
//    @Autowired
//    private MessageService messageService;

    private static volatile IMUtils imUtils;

    public IMUtils(){}

    public static IMUtils getInstance() {
        if (imUtils == null) {
            synchronized (IMUtils.class) {
                if (imUtils == null) {
                    imUtils = new IMUtils();
                }
            }
        }
        return imUtils;
    }

//    public MessageDTO chatMessageMessageDTO(ChatMessage chat) {
//        log.info("IMUtils chatMessageMessageDTO chat:{}", chat);
////        MessageDTO messageDTO = serverMapper.chatMessage2MessageDTO(chat);
//        MessageDTO messageDTO = new MessageDTO();
//        chat.setStudentId("12003990130");
//        messageDTO.setStudentId(chat.getStudentId());
//        UserDTO userDTO = userDao.getUserByStudentId(chat.getStudentId());
////        messageDTO.setUsername(userDTO.getUsername());
//        messageDTO.setUsername("Letitbe");
//        messageDTO.setHeadPicture(userDTO.getHeadPicture());
//        messageService.insertMessage(messageDTO);
//        System.out.println("-------->" + messageDTO);
//        return messageDTO;
//    }
}
