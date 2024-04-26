package chatgptserver.dao;

import chatgptserver.bean.dto.ppt.PptColor;
import chatgptserver.bean.po.PptColorPO;
import chatgptserver.bean.po.PptPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/22
 */
@Mapper
public interface PptMapper {

    void remainColor(PptColor color);

    List<PptColorPO> pptColorList();

    int pptUpload(PptPO pptPO);

    void updatePptCodeById(@Param("pptCode") String pptCode, @Param("id") Integer id);

    List<String> firstKindList();

    List<String> secondListByFirstKind(String firstKind);

    List<PptPO> pptListByKind(@Param("firstKind") String firstKind, @Param("secondKind") String secondKind,
                              @Param("startIndex") int startIndex, @Param("size") int size);

    int totalOfpptListByKind(@Param("firstKind") String firstKind, @Param("secondKind") String secondKind);

    Integer pptIsCollected(@Param("userCode") String userCode, @Param("pptCode") String pptCode);

    void pptCollect(@Param("userCode") String userCode, @Param("pptCode") String pptCode);

    void pptDisCollect(@Param("userCode") String userCode, @Param("pptCode") String pptCode);
}
