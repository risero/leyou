package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.FastClientImporter;
import com.leyou.upload.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/2 10:11
 */
@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class) // 启用自定义的配置属性类
public class UploadService {

    // private static final List<String> ALLOW_TYPES = Arrays.asList("image/jpeg", "image/png", "image/bmp", "image/jpg");

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private UploadProperties prop;

    /**
     * 图片上传
     *
     * @param file
     * @return
     */
    public String uploadImage(MultipartFile file) {
        try {
            //校验文件类型
            String contentType = file.getContentType();
            if(! prop.getAllowTypes().contains(contentType)){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            // 校验文件内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(image == null){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }

            // 截取文件后缀名
            String fileExtension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            // 上传到FastDFS图片服务器
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), fileExtension, null);
            // 返回路径
            return prop.getBase_url() + storePath.getFullPath();
        } catch (IOException e) {
            // 把异常信息记录到日志
            log.error("[文件上传] 文件上传失败!", e);
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }
    }
}
