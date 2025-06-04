package org.example.chatserver.api;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;

/**
 * FastDFS 测试类
 * 用于测试文件上传和下载功能
 */
public class FdfsTest {
    @Test
    void testUpload() {
        try {
            ClientGlobal.initByProperties("fastdfs-client.properties");
            TrackerClient tracker = new TrackerClient();
            TrackerServer trackerServer = tracker.getConnection();
            StorageServer storageServer = null;
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);
            NameValuePair nvp[] = null;
            //上传到文件系统
            String fileId = client.upload_file1("/Users/yanjq/Documents/def7fe327bc44bd8c208ea611ec76fb.jpg", "png",
                    nvp);
//            logger.info(fileId);
            System.out.println("上传成功，文件ID为：" + fileId);
        } catch (Exception e) {
            System.out.println("上传失败");
            e.printStackTrace();
        }
    }
    @Test
    void testDownload() {
        try {
            ClientGlobal.initByProperties("fastdfs-client.properties");
            TrackerClient tracker = new TrackerClient();
            TrackerServer trackerServer = tracker.getConnection();
            StorageServer storageServer = null;
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);
            byte[] bytes = client.download_file1("group1/M00/00/00/ZGQ1a2g3YxeAS0iQAAC-watWw_Y689.png");
            FileOutputStream fos = new FileOutputStream(new File("./123.png"));
            fos.write(bytes);
            System.out.println("下载成功，文件已保存为：666.png");
            fos.close();
        } catch (Exception e) {
            System.out.println("下载失败");
            e.printStackTrace();
        }
    }
}

