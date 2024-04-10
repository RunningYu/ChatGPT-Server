package chatgptserver.bean.dto.WenXin.imageUnderstand;

import lombok.Data;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/10
 */
@Data
public class WenXinImageUnderstandResponseDTO {

    private String id;

    private String object;

    private Long created;

    private String result;

    private Integer is_safe;

    private PromptTokens prompt_tokens;

//    {
//        "id": "as-th7f8y0ckj",
//            "object": "chat.completion",
//            "created": 1702964273,
//            "result": "The image depicts a dining table with multiple bowls, containing various food items, including  rice and meat. The bowl s are placed on different sides of the table, and chopsticks can be seen placed near the bowls. In addition to the bowl s, there are two spoons, one closer to the  left side of the table and the other towards the center. The table is also accompanied by a cup , placed at the top left corner.",
//            "is_safe": 1,
//            "usage": {
//        "prompt_tokens": 3,
//                "completion_tokens": 98,
//                "total_tokens": 101
//    }
//    }
}


