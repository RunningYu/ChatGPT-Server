package chatgptserver.bean.dto.tongYiQianWen;

import lombok.Data;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/27
 */
@Data
public class TongYiUsage {

    private String output_tokens;

    private String input_tokens;

    private String image_tokens;

}
