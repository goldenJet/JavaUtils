package com.jet.api;

import com.jet.entity.WLFile;
import com.jet.pojo.FileBean;
import com.jet.pojo.MyResposeData;
import com.jet.service.AttachementService;
import com.jet.util.APIUtil;
import com.jet.util.ResponseEntityUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;

/**
 * @ClassName: FileInterface
 * @Description: 文件 api，即当做文件服务使用
 * @Author: Jet.Chen
 * @Date: 2019/3/11 18:21
 * @Version: 1.0
 **/
@Log4j
@Controller
@RequestMapping(value = "/api/file")
public class FileInterface {

    @Autowired
    AttachementService attachementService;

    /**
    * @Description: 文件上传
    * @Param: [multipartFile, desc, token, signed]
    * @return: com.jet.pojo.MyResposeData
    * @Author: Jet.Chen
    * @Date: 2019/3/12 15:36
    */
    @PostMapping("/upload")
    public @ResponseBody MyResposeData upload(@RequestParam("file") MultipartFile multipartFile, @RequestParam(value = "desc")String desc
            , @RequestParam(value = "token")String token, @RequestParam(value = "signed")String signed){

        MyResposeData myResposeData = new MyResposeData();

        do {
            if (StringUtils.isBlank(desc)) {
                myResposeData.initError(myResposeData, "请添加文件说明", "500");
                break;
            }

            // 鉴权
            String signCheckResult = APIUtil.checkSig4File(token, signed, true);
            if (signCheckResult != null) {
                myResposeData.initError(myResposeData, signCheckResult, "500");
                break;
            }

            // 验证ut（ut是用户标识）
            String tokenStr = new String(Base64Utils.decodeFromString(token));
            String ut = tokenStr.substring(0, tokenStr.length() - 14);
            if (!attachementService.checkUt(ut)) {
                myResposeData.initError(myResposeData, "ut 不存在，请联系管理员进行创建！", "500");
                break;
            }

            // 获取上传的文件
            if (multipartFile == null){
                myResposeData.initError(myResposeData, "文件不能为空", "500");
                break;
            }
            // 文件名
            String originalFilename = multipartFile.getOriginalFilename();

            // 再一次判断文件是否为空
            long size = multipartFile.getSize();
            if (size == 0) {
                myResposeData.initError(myResposeData, "文件不能为空", "500");
                break;
            }

            // 判断文件大小
            if (size/1024 > 10000) {
                myResposeData.initError(myResposeData, "上传的图片不能超过10M", "500");
                break;
            }

            // 校验文件名是否合格
            if (!originalFilename.matches("^.+\\.(?i)(jpg)$") && !originalFilename.matches("^.+\\.(?i)(jpeg)$")
                    && !originalFilename.matches("^.+\\.(?i)(bmp)$") && !originalFilename.matches("^.+\\.(?i)(png)$")
                    && !originalFilename.matches("^.+\\.(?i)(ico)$")  && !originalFilename.matches("^.+\\.(?i)(pdf)$")
                    && !originalFilename.matches("^.+\\.(?i)(docx)$")  && !originalFilename.matches("^.+\\.(?i)(doc)$")){
                myResposeData.initError(myResposeData, "请确认上传的图片格式是否正确", "500");
                break;
            }

            // 上传ftp
            String id = UUID.randomUUID().toString();

            String url = "fileServer\\" + ut + "\\" + id;

            // 存数据库的实体类
            WLFile wlFile = new WLFile(id, url, originalFilename, desc, ut);

            try {
                InputStream inputStream = multipartFile.getInputStream();
                // 文件上传
                attachementService.uploadWLFile(inputStream, wlFile);
                myResposeData.setData(new HashMap<String, Object>(){{put("id", wlFile.getId());}});
            } catch (Exception e) {
                log.error("文件服务器文件上传异常", e);
                myResposeData.initError(myResposeData, "服务器异常，请稍后再试！", "500");
                break;
            }

            break;
        } while (true);

        return myResposeData;
    }

    /**
    * @Description: 文件下载
    * @Param: [request, id, token, signed]
    * @return: org.springframework.http.ResponseEntity<org.springframework.core.io.InputStreamResource>
    * @Author: Jet.Chen
    * @Date: 2019/3/12 11:55
    */
    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> getFile(HttpServletRequest request, @PathVariable("id")String id
            , @RequestParam(value = "token", required = true)String token, @RequestParam(value = "signed", required = true)String signed
            , @RequestParam(value = "download", required = false)String download){

        ResponseEntity<InputStreamResource> resourceResponseEntity;
        try {
            log.info("文件下载，id："+id+"，token："+token+"signed："+signed);
            // 鉴权
            String signCheckResult = APIUtil.checkSig4File(token, signed, true);
            if (signCheckResult != null) throw new Exception(signCheckResult);
            // 验证ut
            String tokenStr = new String(Base64Utils.decodeFromString(token));
            String ut = tokenStr.substring(0, tokenStr.length() - 14);
            if (!attachementService.checkUt(ut)) throw new Exception("ut 不存在，请联系管理员进行创建！");
            // 查询数据库是否存在
            WLFile wlFile = attachementService.findWLFile(id, ut, false);
            if (wlFile == null) throw new Exception("文件不存在，请确认！");

            long pos = 0;
            if (null != request.getHeader("Range")) {
                // 断点续传
                try {
                    pos = Long.parseLong(request.getHeader("Range").replaceAll("bytes=", "").replaceAll("-", ""));
                } catch (NumberFormatException e) {
                }
            }

            // 获取到的文件实体类
            FileBean fileBean = attachementService.downloadFile(wlFile.getUrl(), wlFile.getName(), pos);

            if(fileBean == null) throw new Exception("文件不存在！");
            // 关键之作
            resourceResponseEntity = ResponseEntityUtil.genericDownLoadResponseEntity(request, fileBean, download != null && "true".equals(download));
        } catch (Exception e) {
            log.error("CRM系统错误" + e.getLocalizedMessage());
            resourceResponseEntity = ResponseEntityUtil.genericErrorResponseEntity2(e);

        }
        return resourceResponseEntity;
    }


}
