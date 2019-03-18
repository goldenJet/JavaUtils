# 文件上传下载工具
## 上传
1. 文件上传时使用 MultipartFile multipartFile 去进行接收

## 下载
1. 下载使用的是 ResponseEntity<InputStreamResource>，虽然代码会复杂一点，但是这种方式是支持断点续传的
1. 下载的时候设置 
    1. 浏览器直接下载设置
        > Content-Disposition: attachment; filename=xxx
        
        > contentType: application/octet-stream
    1. 浏览器预览设置
        > Content-Disposition: inline; filename=xxx
        
        > contentType: application/pdf 等