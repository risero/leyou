package com.leyou.upload;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * FdfsClient测试
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/3 19:06
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FastClientTest {

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    @Test
    public void testUpload() throws Exception {
        File file = new File("K:\\乐优商城项目\\Day59 - elasticsearch\\4.wmv");
        // 上传图片
        String fileExtension = FilenameUtils.getExtension(file.getName());
        StorePath storePath = this.storageClient.uploadFile(new FileInputStream(file), file.length(),
                fileExtension, null);
        // 带分组的路径
        System.out.println("http://image.leyou.com/"+storePath.getFullPath());
        // 不带分组的路径
        System.out.println(storePath.getPath());
    }

    /**
     * 上传并且生成缩略图
     *
     * @throws FileNotFoundException
     */
    @Test
    public void testUploadAndCreateThumb() throws FileNotFoundException {
        // 上传并且生成缩略图
        File file = new File("C:\\Users\\dell\\Pictures\\QQ图片20170306230143.png");
        String fileExtension = FilenameUtils.getExtension(file.getName());
        StorePath storePath = this.storageClient.uploadImageAndCrtThumbImage(new FileInputStream(file)
                , file.length(), fileExtension, null);
        // 带分组的路径
        System.out.println(storePath.getFullPath());
        // 不带分组的路径
        System.out.println(storePath.getPath());
        // 获取缩略图路径
        String thumbImagePath = thumbImageConfig.getThumbImagePath(storePath.getFullPath());
        System.out.println(thumbImagePath);
    }
}
