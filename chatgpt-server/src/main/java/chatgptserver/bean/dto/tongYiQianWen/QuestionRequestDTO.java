package chatgptserver.bean.dto.tongYiQianWen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRequestDTO {

    private String model;

    private QuestionInput input;

}
