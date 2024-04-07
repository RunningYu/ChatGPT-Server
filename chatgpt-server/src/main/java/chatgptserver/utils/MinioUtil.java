package chatgptserver.utils;


import chatgptserver.bean.ao.UploadResponse;
import chatgptserver.config.MinIO.MinioProperties;
import chatgptserver.enums.MinIoConstands;
import cn.hutool.core.date.DateUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2022/12/1
 */

@ApiModel(description = "文件操作工具类")
@Component
@Slf4j
public class MinioUtil {

    @Autowired
    private MinioProperties minioProperties;

    @Autowired
    private MinioClient minioClient;

    private final Long maxSize = (long) (1024 * 1024);

    @ApiOperation("创建bucket")
    public void createBucket(String bucketName) throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    @ApiOperation("上传文件")
    public UploadResponse uploadFile(MultipartFile file, String bucketName) throws Exception {
        log.info("MinioUtil uploadFile bucketName:[{}]", bucketName);
        //判断文件是否为空
        if (null == file || 0 == file.getSize()) {
            return null;
        }
        //判断存储桶是否存在  不存在则创建
        createBucket(bucketName);
        //文件名
        String originalFilename = file.getOriginalFilename();
        //新的文件名 = 时间戳_随机数.后缀名
        assert originalFilename != null;
        long now = System.currentTimeMillis() / 1000;
        String fileName = DateUtil.format(DateUtil.date(),"yyyyMMdd")+"_"+ now + "_" + new Random().nextInt(1000) +
                originalFilename.substring(originalFilename.lastIndexOf("."));

        //开始上传
        log.info("file压缩前大小:{}",file.getSize());

        if (file.getSize() > maxSize) {
            FileItemFactory fileItemFactory = new DiskFileItemFactory();
            FileItem fileItem = fileItemFactory.createItem(fileName, "text/plain", true, fileName);
            OutputStream outputStream = fileItem.getOutputStream();
            Thumbnails.of(file.getInputStream()).scale(1f).outputFormat(originalFilename.substring(originalFilename.lastIndexOf(".")+1)).outputQuality(0.25f).toOutputStream(outputStream);
        }

        log.info("file压缩后大小:{}",file.getSize());

        minioClient.putObject(
                PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                                file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());

        String url = minioProperties.getEndpoint() + "/" + bucketName + "/" + fileName;
        String urlHost = minioProperties.getNginxHost() + "/" + bucketName + "/" + fileName;
        return new UploadResponse(url, urlHost);
    }


    @ApiOperation("获取全部bucket")
    public List<Bucket> getAllBuckets() throws Exception {
        return minioClient.listBuckets();
    }

    @ApiOperation("根据 bucket 名称获取信息")
    public Optional<Bucket> getBucket(String bucketName) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidResponseException, InternalException, ErrorResponseException, ServerException, XmlParserException, ServerException {
        return minioClient.listBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
    }

    @ApiOperation("根据bucketName删除信息")
    public void removeBucket(String bucketName) throws Exception {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * 获取⽂件外链
     *
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @param expires    过期时间 <=7
     * @return url
     */
    @ApiOperation("获取⽂件外链")
    public String getObjectURL(String bucketName, String objectName, Integer expires) throws Exception {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(objectName).expiry(expires).build());
    }

    /**
     * 获取⽂件
     *
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @return ⼆进制流
     */
    @ApiOperation("获取⽂件")
    public InputStream getObject(String bucketName, String objectName) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 上传⽂件
     *
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @param stream     ⽂件流
     * @throws Exception https://docs.minio.io/cn/java-minioClient-api-reference.html#putObject
     */
    public void putObject(String bucketName, String objectName, InputStream stream) throws
            Exception {
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(stream, stream.available(), -1).contentType(objectName.substring(objectName.lastIndexOf("."))).build());
        stream.close();
    }

    /**
     * 上传⽂件
     *
     * @param bucketName  bucket名称
     * @param objectName  ⽂件名称
     * @param stream      ⽂件流
     * @param size        ⼤⼩
     * @param contextType 类型
     * @throws Exception https://docs.minio.io/cn/java-minioClient-api-reference.html#putObject
     */
    public void putObject(String bucketName, String objectName, InputStream stream, long
            size, String contextType) throws Exception {
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(stream, size, -1).contentType(contextType).build());
        stream.close();
    }

    /**
     * 获取⽂件信息
     *
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @throws Exception https://docs.minio.io/cn/java-minioClient-api-reference.html#statObject
     */
    public StatObjectResponse getObjectInfo(String bucketName, String objectName) throws Exception {
        return minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 删除⽂件
     *
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @throws Exception https://docs.minio.io/cn/java-minioClient-apireference.html#removeObject
     */
    @ApiOperation("删除⽂件")
    public void removeObject(String bucketName, String objectName) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }


    public String upLoadFileToURL(MultipartFile image) {
        try {
            UploadResponse uploadResponse = uploadFile(image, MinIoConstands.BUCKET_NAME);
            return uploadResponse.getMinIoUrl();
        } catch (Exception e) {
            throw new RuntimeException("图片上传MinIO失败，获取图片URL失败");
        }
    }
}
