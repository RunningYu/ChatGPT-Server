package chatgptserver.bean.ao.ppt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/5/20
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReplyResponseAO {

    private Integer total;

    private List<ReplyAO> list;

}
