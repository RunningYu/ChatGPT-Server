package chatgptserver.dao;

import chatgptserver.bean.dto.ppt.PptColor;
import chatgptserver.bean.po.*;
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

    List<PptPO> pptListByKind(@Param("keyword") String keyword, @Param("firstKind") String firstKind, @Param("secondKind") String secondKind,
                              @Param("startIndex") int startIndex, @Param("size") int size);

    int totalOfpptListByKind(@Param("keyword") String keyword, @Param("firstKind") String firstKind, @Param("secondKind") String secondKind);

    Integer pptIsCollected(@Param("folderCode") String folderCode, @Param("userCode") String userCode, @Param("pptCode") String pptCode);

    Integer isCollected(@Param("userCode") String userCode, @Param("pptCode") String pptCode);

    void pptCollect(@Param("folderCode") String folderCode, @Param("userCode") String userCode, @Param("pptCode") String pptCode);

    void pptDisCollect(@Param("folderCode") String folderCode, @Param("userCode") String userCode, @Param("pptCode") String pptCode);

    List<PptPO> pptCollectList(@Param("folderCode") String folderCode, @Param("userCode") String userCode, @Param("startIndex") int startIndex, @Param("size") int size);

    int pptCollectListTotal(@Param("folderCode") String folderCode, String userCode);

    void folderCreate(FolderPO folderPO);

    void updateFolderCode(@Param("folderCode") String folderCode, @Param("id") Integer id);

    List<FolderPO> folderList(@Param("userCode") String userCode);

    void updateCollectAmount(@Param("pptCode") String pptCode, @Param("score") int score);

    void folderUpdate(@Param("folderCode") String folderCode, @Param("folder") String folder);

    int folderDelete(@Param("folderCode") String folderCode);

    int countAmount(String folderCode);

    /**
     * collectAmount -1
     */
    void updateCollectAmountAfterFolderDelete(@Param("folderCode") String folderCode);

    void collectRecordDelete(String folderCode);

    List<PptPO> pptMeList(@Param("userCode") String userCode, @Param("startIndex") int startIndex, @Param("size") int size);

    int totalOfPptMeList(String userCode);

    void pptDelete(String pptCode);

    void collectRecordDeleteByPptCode(String pptCode);

    double getTotalScore(String pptCode);

    void updateScore(@Param("pptCode") String pptCode, @Param("score") double score, @Param("totalScore") double totalScore);

    void pptSeeAmountAdd(@Param("pptCode") String pptCode);

    int pptComment(CommentPO request);

    void updateCommentCode(@Param("id") Integer id, @Param("commentCode") String commentCode);

    void pptCommentAmountAdd(String pptCode);

    int pptReply(ReplyPO request);

    void updateReplyCode(@Param("id") Integer id, @Param("replyCode") String replyCode);

    void pptReplyAmountAdd(String commentCode);

    List<CommentPO> pptCommentList(@Param("pptCode") String pptCode, @Param("startIndex") int startIndex, @Param("size") int size);

    int commentTotal(@Param("pptCode") String pptCode);

    List<ReplyPO> relyList(@Param("commentCode") String commentCode, @Param("startIndex") int startIndex, @Param("size") int size);

    int replyTotal(@Param("commentCode") String commentCode);
}
