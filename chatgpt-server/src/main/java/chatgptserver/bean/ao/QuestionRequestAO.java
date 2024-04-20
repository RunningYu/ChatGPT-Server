package chatgptserver.bean.ao;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/24
 */

@Data
public class QuestionRequestAO {

    @ApiModelProperty("问题内容")
    private String content;

    @ApiModelProperty("用户唯一code")
    private String userCode;

    @ApiModelProperty("聊天code")
    private String chatCode;

    @ApiModelProperty("是否重新生成")
    private Boolean isRebuild;

    @ApiModelProperty("停止生成的标识")
    private String cid;

}
