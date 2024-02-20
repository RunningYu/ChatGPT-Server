package chatgptserver.bean.dto.WenXin;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/3 16:23
 */

import lombok.Data;

/**
 * Copyright 2024 json.cn
 */

@Data
public class Usage {

    private int prompt_tokens;

    private int completion_tokens;

    private int total_tokens;

}
