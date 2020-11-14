package com.example.yxz.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author : xilipeng
 * @create : 2020-11-14 15:06
 * @description ：用于处理视频时间/大小
 */
public class VideoUtil {


    static Logger log = LoggerFactory.getLogger(VideoUtil.class);

    /**
     * 获取视频时长(时分秒)
     *
     * @param ms
     * @return
     */
    public static String readVideoTime(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strDay = day < 10 ? "0" + day : "" + day; //天
        String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
        String strSecond = second < 10 ? "0" + second : "" + second;//秒
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;
        if (strHour.equals("00")) {
            return strMinute + ":" + strSecond;
        } else {
            return strDay + "." + strHour + ":" + strMinute + ":" + strSecond;
        }
    }

    /**
     * 获取 MultipartFile 视频时长(毫秒)
     *
     * @param multipartFile
     * @return
     */
    public static Long readMultipartFileVideoTimeMs(MultipartFile multipartFile) {
        long ls = 0;
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            // 获取文件后缀
            String suffix = originalFilename.indexOf(".") != -1 ?
                    originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length()) : null;
            // 用uuid作为文件名，防止生成的临时文件重复 suffix == null,文件后缀会是 .temp
            final File file = File.createTempFile(UUID.randomUUID().toString(), suffix);
//             MultipartFile to File
            multipartFile.transferTo(file);
//            VideoUtil.fileInfo(tempFile);
            ls = VideoUtil.readFileVideoTimeMs(file);
            //程序结束时，删除临时文件
            VideoUtil.deleteFile(file);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return ls;
    }

    /**
     * 获取 File 视频时长(毫秒)
     *
     * @param file
     * @return
     */
    public static Long readFileVideoTimeMs(File file) {
        long ls = 0;
        MultimediaObject instance = new MultimediaObject(file);
        MultimediaInfo result = null;
        try {
            result = instance.getInfo();
        } catch (EncoderException e) {
            log.error(e.toString());
        }
        ls = result.getDuration();
//            log.info("文件时长（秒）：" + ls);
        return ls;

    }

    /**
     * 删除
     *
     * @param files
     */
    private static void deleteFile(File... files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private static void fileInfo(File tempFile) {
                  /*
             getName:0828f325-1ac4-43dd-9c67-3870ba3c55e85090693610479876865.avi
             getAbsoluteFile:C:\Users\HEIZHI~1\AppData\Local\Temp\7839b1d6-1475-46d6-b47b-5495df7566184108998790169821144.avi
             getAbsolutePath:C:\Users\HEIZHI~1\AppData\Local\Temp\7839b1d6-1475-46d6-b47b-5495df7566184108998790169821144.avi
             getPath:C:\Users\HEIZHI~1\AppData\Local\Temp\7839b1d6-1475-46d6-b47b-5495df7566184108998790169821144.avi
             getCanonicalFile:C:\Users\heizhishi\AppData\Local\Temp\7839b1d6-1475-46d6-b47b-5495df7566184108998790169821144.avi
             getFreeSpace:12922769408
             isDirectory:false
             length:23679148
             getUsableSpace:12922769408
             getTotalSpace:64424505344
             文件时长（毫秒）：672600
             */
        String name = tempFile.getName();
        log.info("getName:" + name);
        File absoluteFile = tempFile.getAbsoluteFile();
        log.info("getAbsoluteFile:" + absoluteFile);
        String absolutePath = tempFile.getAbsolutePath();
        log.info("getAbsolutePath:" + absolutePath);
        String path = tempFile.getPath();
        log.info("getPath:" + path);
        File canonicalFile = null;
        try {
            canonicalFile = tempFile.getCanonicalFile();
        } catch (IOException e) {
            log.error(e.toString());
        }
        log.info("getCanonicalFile:" + canonicalFile);
        long freeSpace = tempFile.getFreeSpace();
        log.info("getFreeSpace:" + freeSpace);
        boolean directory = tempFile.isDirectory();
        log.info("isDirectory:" + directory);
        long length1 = tempFile.length();
        log.info("length:" + length1);
        long usableSpace = tempFile.getUsableSpace();
        log.info("getUsableSpace:" + usableSpace);
        long totalSpace = tempFile.getTotalSpace();
        log.info("getTotalSpace:" + totalSpace);
    }
}
