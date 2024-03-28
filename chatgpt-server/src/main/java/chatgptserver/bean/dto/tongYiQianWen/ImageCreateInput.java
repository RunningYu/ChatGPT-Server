package chatgptserver.bean.dto.tongYiQianWen;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageCreateInput {

    @ApiModelProperty(value = "文本内容，仅支持英文，不超过75个单词，超过部分会自动截断。", required = true)
    private String prompt;

    @ApiModelProperty(value = "负向文本内容，仅支持英文。", required = false)
    private String negative_prompt;
}
