package chatgptserver.bean.ao.ppt;

import chatgptserver.bean.po.PptPO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PptCollectListResponseAO {

    private Integer total;

    private List<PptAO> list;
}
