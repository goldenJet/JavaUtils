package com.jet.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jet.pojo.FileBean;
import com.jet.pojo.MyResposeData;
import com.jet.pojo.ResponseResult;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by jet.chen on 2017/5/10.
 */
public class ResponseEntityUtil {


    public static ResponseEntity<InputStreamResource> genericErrorResponseEntity(Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseResult responseResult = new ResponseResult();
        responseResult.setSuccess(false);
        responseResult.setInfo(e.getLocalizedMessage());
        byte[] bytes = new byte[0];
        try {
            bytes = objectMapper.writeValueAsBytes(responseResult);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }
        return genericErrorResponseEntityCommon(e, bytes);
    }

    public static ResponseEntity<InputStreamResource> genericErrorResponseEntity2(Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
        MyResposeData myResposeData = new MyResposeData();
        myResposeData.initError(myResposeData, e.getLocalizedMessage(), "500");
        byte[] bytes = new byte[0];
        try {
            bytes = objectMapper.writeValueAsBytes(myResposeData);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }
        return genericErrorResponseEntityCommon(e, bytes);
    }

    private static ResponseEntity<InputStreamResource> genericErrorResponseEntityCommon(Exception e, byte[] bytes) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Content-Type", "application/json;charset=UTF-8");
        ResponseEntity responseEntity = ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .headers(headers)
                .body(new InputStreamResource(new ByteArrayInputStream(bytes)));
        return responseEntity;
    }


    public static ResponseEntity<InputStreamResource> genericDownLoadResponseEntity(HttpServletRequest request, FileBean fileBean, boolean downloadFlag) {
        long pos = 0;
        long lRemoteSize = fileBean.getSize();
        byte[] bytes = fileBean.getBytes();
        ResponseEntity responseEntity;
        String fileName = fileBean.getFileName();
        if (null != request.getHeader("Range")) {
            // 断点续传
            try {
                pos = Long.parseLong(request.getHeader("Range").replaceAll("bytes=", "").replaceAll("-", ""));
            } catch (NumberFormatException e) {
            }
        }
        String userAgent = request.getHeader("User-Agent");
        //针对IE或者以IE为内核的浏览器：
        try {
            if (userAgent.contains("MSIE") || userAgent.contains("Trident") || userAgent.contains("Edge")) {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            } else {
                //非IE浏览器的处理：
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }
        }catch (UnsupportedEncodingException e) {

        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept-Ranges", "bytes");
        headers.add("Content-Length", String.valueOf(lRemoteSize));
        MediaType mediaType = MediaType.parseMediaType("application/octet-stream");
        if (downloadFlag) {
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
        } else {
            // 设置文件预览
            MediaType mediaTypeTemp = initMediaType(fileName);
            if (mediaTypeTemp != null) {
                headers.add("Content-Disposition", String.format("inline; filename=\"%s\"", fileName));
                mediaType = mediaTypeTemp;
            } else {
                headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
            }
        }
        String contentRange = new StringBuffer("bytes ").append(String.valueOf(pos)).append("-").append(String.valueOf(lRemoteSize - 1)).append("/").append(String.valueOf(lRemoteSize)).toString();
        headers.add("Content-Range", contentRange);
        if (null != request.getHeader("Range")) {
            responseEntity = ResponseEntity
                    .status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .contentLength(lRemoteSize)
                    .contentType(mediaType)
                    .body(new InputStreamResource(new ByteArrayInputStream(bytes)));
        } else {
            responseEntity = ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(lRemoteSize)
                    .contentType(mediaType)
                    .body(new InputStreamResource(new ByteArrayInputStream(bytes)));
        }
        return responseEntity;
    }

    public static MediaType initMediaType(String fileName){
        // TODO
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
        if ("PDF".equals(fileType)) {
            return MediaType.parseMediaType("application/pdf");
        } else if ("GIF".equals(fileType)) {
            return MediaType.parseMediaType("image/gif");
        } else if ("JPEG".equals(fileType) || "JPG".equals(fileType)) {
            return MediaType.parseMediaType("image/jpeg");
        } else if ("PNG".equals(fileType)) {
            return MediaType.parseMediaType("image/png");
        } else {
            return null;
        }
    }


}
