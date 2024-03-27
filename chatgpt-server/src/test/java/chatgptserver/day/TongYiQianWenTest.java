package chatgptserver.day;

import chatgptserver.bean.dto.tongYiQianWen.Input;
import chatgptserver.bean.dto.tongYiQianWen.TongYiImageUnderStandRequestDTO;
import chatgptserver.bean.dto.tongYiQianWen.TongYiImageUnderstandResponseDTO;
import chatgptserver.bean.dto.tongYiQianWen.TongYiMessages;
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
@Slf4j
@SpringBootTest
public class TongYiQianWenTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private OkHttpService okHttpService;

    @Test
    public String tyImageUnderstand() {
        String responseStr = "";
        String resquestStr = "{\n" +
                "    \"model\": \"qwen-vl-plus\",\n" +
                "    \"input\":{\n" +
                "        \"messages\":[\n" +
                "            {\n" +
                "                \"role\": \"system\",\n" +
                "                \"content\": [\n" +
                "                    {\"text\": \"You are a helpful assistant.\"}\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"role\": \"user\",\n" +
                "                \"content\": [\n" +
                "                    {\"image\": \"https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg\"},\n" +
                "                    {\"text\": \"这个图片是哪里？\"}\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"role\": \"system\",\n" +
                "                \"content\": [\n" +
                "                    {\"text\": \"这是一只猫。从图片上看，它似乎是一只短毛猫，有着橘色或蜜糖色的毛发。猫咪正坐在电脑前，看起来像是在使用笔记本电脑。\"}\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"role\": \"user\",\n" +
                "                \"content\": [\n" +
                "                    {\"text\": \"那你觉得这个猫是什么情绪状态？它是什么毛色？\"}\n" +
                "                ]\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"parameters\": {\n" +
                "    }\n" +
                "}";
        try {
            responseStr = okHttpService.makePostRequest(
                    GPTConstants.TONG_YI_QIAN_WEN_IMAGE_UNDERSTAND_URL,
                    resquestStr,
                    GPTConstants.TONG_YI_QIAN_WEN_API_KEY);
//            log.info("WenXinServiceImpl tyImageUnderstand responseStr:[{}]", responseStr);
            System.out.println("--------------------------------------------------------------");
            System.out.println("responseStr: " + responseStr);
            System.out.println("----------------------------------------------------------------");
        } catch (IOException e) {
            throw new RuntimeException("请求通义千问接口异常");
        }
        TongYiImageUnderstandResponseDTO responseDTO = JSON.parseObject(responseStr, TongYiImageUnderstandResponseDTO.class);
        String response = responseDTO.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
        log.info("TongYiServiceImpl tyImageUnderstand response:[{}]", response);
        messageService.recordHistory("123", "chat_7", "那你觉得这个猫是什么情绪状态？它是什么毛色？", response);
        System.out.println("--------------------------------------------------------------");
        System.out.println("response: " + response);
        System.out.println("----------------------------------------------------------------");

        return response;
    }


    public TongYiImageUnderStandRequestDTO buildTongyiImageUnderstandRequestDTO(String imageUrl, String content) {
        Map<String, String> imageMap = new HashMap<>();
        imageMap.put("image", imageUrl);
        Map<String, String> textMap = new HashMap<>();
        textMap.put("text", content);
        List<Map<String, String>> contentList = new ArrayList<>();
        contentList.add(imageMap);
        contentList.add(textMap);
        TongYiMessages messages = new TongYiMessages("user", contentList);
        log.info("WenXinServiceImpl tyImageUnderstand messages:[{}]", messages);
        List<TongYiMessages> list = new ArrayList<>();
        list.add(messages);



        Input input = new Input(list);
        TongYiImageUnderStandRequestDTO request = new TongYiImageUnderStandRequestDTO("qwen-vl-plus");
        request.setInput(input);

        return request;
    }
}
