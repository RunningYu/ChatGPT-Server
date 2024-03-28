package chatgptserver.day;

import chatgptserver.bean.dto.tongYiQianWen.Input;
import chatgptserver.bean.dto.tongYiQianWen.TongYiImageUnderStandRequestDTO;
import chatgptserver.bean.dto.tongYiQianWen.TongYiImageUnderstandResponseDTO;
import chatgptserver.bean.dto.tongYiQianWen.TongYiMessages;
import chatgptserver.bean.po.MessagesPO;
import chatgptserver.dao.MessageMapper;
import chatgptserver.enums.GPTConstants;
import chatgptserver.service.MessageService;
import chatgptserver.service.OkHttpService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/27
 */
@SpringBootTest
public class TongYiQianWenTest {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private OkHttpService okHttpService;

    @Test
    public void test1() {
        String chatCode = "chat_9";
        MessagesPO messagesFistChat = messageMapper.getTongYiQuestionFistChat(chatCode);
        System.out.println("----------------------------------------------------------");
        System.out.println(messagesFistChat);
        System.out.println("----------------------------------------------------------");
    }

}
