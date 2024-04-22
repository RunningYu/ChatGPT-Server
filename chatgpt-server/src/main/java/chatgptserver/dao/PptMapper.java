package chatgptserver.dao;

import chatgptserver.bean.dto.ppt.PptColor;
import chatgptserver.bean.po.PptColorPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/22
 */
@Mapper
public interface PptMapper {

    void remainColor(PptColor color);

    List<PptColorPO> pptColorList();
}
