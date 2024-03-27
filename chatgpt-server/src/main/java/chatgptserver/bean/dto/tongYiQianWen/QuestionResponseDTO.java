package chatgptserver.bean.dto.tongYiQianWen;

import lombok.Data;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/27
 */
@Data
public class QuestionResponseDTO {

    private QuestionOutput output;

    private TongYiUsage usage;

    private String request_id;

}
