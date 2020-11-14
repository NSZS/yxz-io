package com.example.yxz.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.yxz.util.VideoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * FileManage 接口
 */
@RestController
@ResponseBody
@RequestMapping(value = "/fileManage")
public class FileManageController {

    Logger log = LoggerFactory.getLogger(getClass());


    /**
     * 获取某路径下 所有视频文件信息
     *
     * @param param
     * @return
     */
    @PostMapping("/getVedioTimes")
    public Map<String, Object> getVedioTimes(@RequestBody Map<String, String> param) {
        List<Map<String, Object>> list = new ArrayList<>();

        long countTime = 0;
        long countLength = 0;
        try {
            String directory = param.get("directory");
            File dir = new File(directory);
            File[] files = dir.listFiles();
            int i = 0;
            for (File file : files) {
                //如果是非视频文件 也会计数
                if (file.isFile()) {
                    Long ls = VideoUtil.readFileVideoTimeMs(file);
                    String name = file.getName();
//                File absoluteFile = file.getAbsoluteFile();
                    long length = file.length();
                    Map<String, Object> map = new HashMap<>();
                    map.put("文件大小:", (length / 1024 / 1024) + "M");
                    map.put("文件名称:", name);
                    String time = VideoUtil.readVideoTime(ls);
                    map.put("文件时长：", time);
                    countTime = countTime + ls;
                    countLength = countLength + length;
                    log.info(++i + JSONObject.toJSONString(map));

                    list.add(map);
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        String totalTime = VideoUtil.readVideoTime(countTime);
//        String s1 = String.valueOf(countTime);
//        String s1 = countTime + "";
//        log.info(s1);
//        double v = Double.parseDouble(s1);
//        log.info(v + "");
//        String s = String.valueOf(v / 1.4);
//        log.info(s);
//        String substring = s.substring(0, s.lastIndexOf("."));
//        log.info(substring);
//        long l = Long.parseLong(divide);
//        log.info(l + "");

        BigDecimal divide = new BigDecimal(countTime).divide(new BigDecimal(1.4), 0);
        long l = divide.longValue();
        String speed = VideoUtil.readVideoTime(l);

        Map<String, Object> result = new HashMap<>();
        result.put("总 时 长", totalTime);
        result.put("1.4 倍速", speed);
        result.put("文件大小", (countLength / 1024 / 1024) + " M（" + countLength + "字节)");
        result.put("视频数量", list.size());
//        result.put("filesInfo", list);
        return result;
    }


    /**
     * 获取单个文件信息
     *
     * @param file
     * @return
     */
    @PostMapping(value = "/getVedioTime", produces = "application/json;charset=UTF-8")
    public Map<String, Object> getVedioTime(@RequestParam MultipartFile file) {

        Map<String, Object> map = new HashMap<>();
        try {
//            MultipartFile file
            String originalFilename = file.getOriginalFilename();
            log.info("文件原名:" + originalFilename);
//            String name = file.getName();
//            log.info("属性（参数）名:" + name);
            long size = file.getSize();
            log.info("文件大小B:" + size);
            log.info("文件大小M:" + size / 1024 / 1024);
            String contentType = file.getContentType();
            log.info("文件类型:" + contentType);

            Long ls = VideoUtil.readMultipartFileVideoTimeMs(file);
            log.info("文件时长（毫秒）：" + ls);
            String time = VideoUtil.readVideoTime(ls);
            log.info("文件时长：" + time);

            map.put("文件原名:", originalFilename);
//            map.put("属性（参数）名:", name);
            map.put("文件大小:", (size / 1024 / 1024) + "M");
            map.put("文件类型:", contentType);
            map.put("文件时长（秒）：", ls);
            map.put("文件时长：", time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    /***
     * file upload
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/fileUpload", method = {RequestMethod.POST})
    public Map<String, Object> fileUpload(@RequestParam MultipartFile file, HttpServletRequest request) {
//        HdbResponse hr = new HdbResponse();
        // List<Object> resultList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        try {
//	        if (files != null && files.length > 0){
//	        	for (int i = 0; i < files.length; i++) {
//	        		MultipartFile file = files[i];
            String fileName = file.getOriginalFilename();
            // \HDB_BACKEND\src\main\webapp\
            String uploadPath = request.getServletContext().getRealPath("");
            map = fileNameManage(fileName, uploadPath);
//			String fileSuffix = fileName.split("\\.")[fileName.split("\\.").length - 1];
//			if (fileSuffix.equals("xml")) {
//				fileName = "Model" + fileName;
//			}
            map.put("fileName", fileName);
            saveFile(file.getInputStream(), map);// 保存到服务器的路径
            // resultList.set(i, map);
            // }
            // }
            if (request.getParameter("type") != null) {
                map.put("fileType", request.getParameter("type"));
            }
        } catch (Exception e) {
//            throw new HdbException(HdbExceptionConstant.HDB_COMMON_UPLOAD_FILE_EXCEPTION);
        }
//        hr.setValue("result", map);
        return map;
    }

    /**
     * save file
     */
    public void saveFile(InputStream stream, Map<String, Object> map) {
        try {
            FileOutputStream fs = new FileOutputStream(map.get("fileUploadPath").toString());
            byte[] buffer = new byte[1024 * 1024];
            int byteread = 0;
            while ((byteread = stream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
                fs.flush();
            }
            fs.close();
            stream.close();
        } catch (Exception e) {
//            throw new HdbException(HdbExceptionConstant.HDB_COMMON_UPLOAD_FILE_EXCEPTION);
        }
    }

    /**
     * upload file name manage
     *
     * @param fileName
     * @param uploadPath
     * @return
     */
    public Map<String, Object> fileNameManage(String fileName, String uploadPath) {
        Map<String, Object> result = new HashMap<>();
        // Generate folder
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        uploadPath = uploadPath + year;
        File uploadPathFile = new File(uploadPath);
        if (!uploadPathFile.exists() && !uploadPathFile.isDirectory()) {
            uploadPathFile.mkdir();
        }
        uploadPath = uploadPath + File.separator + month;
        uploadPathFile = new File(uploadPath);
        if (!uploadPathFile.exists() && !uploadPathFile.isDirectory()) {
            uploadPathFile.mkdir();
        }
        // generate file name
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String dateString = simpleDateFormat.format(date);
        Random random = new Random();
        int randomNum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;
        String fileNewName = randomNum + dateString;
        // Splicing filename suffix
        String fileSuffix = fileName.split("\\.")[fileName.split("\\.").length - 1];
        fileNewName = fileNewName + "." + fileSuffix;
        result.put("fileUploadPath", uploadPathFile + File.separator + fileNewName);
        return result;
    }

    /**
     * file download
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/fileDownload", method = {RequestMethod.POST})
    public void fileDownload(HttpServletRequest request, HttpServletResponse response) {
        download(response);
    }

    /**
     * down function
     */
    public void download(HttpServletResponse response) {
        String fileName = null;
        try {
            fileName = new String("111".getBytes("GBK"), "ISO-8859-1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        /// home/tomcat/apache-tomcat-9.0.1/files
        String realPath = "D:" + File.separator + "apache-tomcat-8.5.15" + File.separator + "files";
//        String realPath=File.separator+"home"+File.separator+"tomcat"+File.separator+"apache-tomcat-9.0.1"+File.separator+"files";
        String path = realPath + File.separator + "111";
        File file = new File(path);
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
