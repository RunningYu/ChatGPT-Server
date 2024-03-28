package chatgptserver.bean.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/28
 */
@ApiModel("用户反馈请求体")
@Data
public class UserFeedbackRequestAO {

    private String userCode;

    private String content;

}
