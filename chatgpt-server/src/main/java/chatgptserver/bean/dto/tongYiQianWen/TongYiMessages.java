package chatgptserver.bean.dto.tongYiQianWen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/27
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TongYiMessages {

    private String role;

    private List<Map<String, String>> content;

    public static TongYiMessages buildTongYiMessages(String user, String image, String question) {
        List<Map<String, String>> firstChatList = new ArrayList<>();
        Map<String, String> firstChatMap1 = new HashMap<>();
        firstChatMap1.put("text", image);
        Map<String, String> firstChatMap2 = new HashMap<>();
        firstChatMap2.put("text", question);
        firstChatList.add(firstChatMap1);
        firstChatList.add(firstChatMap2);
        TongYiMessages tongYiMessages = new TongYiMessages(user, firstChatList);

        return tongYiMessages;
    }

    public static TongYiMessages buildTongYiMessages(String assistant, String replication) {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> textMap = new HashMap<>();
        textMap.put("text", replication);
        list.add(textMap);
        TongYiMessages tongYiMessages = new TongYiMessages(assistant, list);

        return tongYiMessages;
    }
}
