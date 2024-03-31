package chatgptserver.bean.ao;

import chatgptserver.bean.po.UserFeedbackPO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/28
 */
@ApiModel("用户反馈列表返回体")
@Data
public class UserFeedbackListResponseAO {

    List<UserFeedbackAO> list;

    Integer total;

    public UserFeedbackListResponseAO(List<UserFeedbackAO> list, int total) {
        this.list = list;
        this.total = total;
    }
}
