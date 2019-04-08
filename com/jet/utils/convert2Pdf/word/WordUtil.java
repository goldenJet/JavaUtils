package com.jet.util;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import lombok.extern.log4j.Log4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;


/**
 * @ClassName: WordUtil
 * @Description: 使用 Aspose 破解版来处理 word
 * @Author: Jet.Chen
 * @Date: 2019/4/8 11:01
 * @Version: 1.0
 **/
@Log4j
public class WordUtil {

    /**
    * @Description: 验证License
    * @Param: []
    * @return: boolean
    * @Author: Jet.Chen
    * @Date: 2019/4/8 11:52
    */
    public static boolean getLicense() {
        boolean result = false;
        try {

            InputStream is = WordUtil.class.getClassLoader().getResourceAsStream("static/download/license.xml"); //  license.xml路径
            if (is == null) return false;
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
            is.close();
        } catch (Exception e) {
            // license 验证不通过，暂时不进行处理
        }
        return result;
    }

    /**
    * @Description: doc 转 pdf
    * @Param: [stream]
    * @return: byte[]
    * @Author: Jet.Chen
    * @Date: 2019/4/8 13:35
    */
    public static byte[] doc2Pdf(InputStream stream) {

        byte[] bytes = null;

        // 验证License 若不验证则转化出的pdf文档有水印
        if (!getLicense()) {
            log.warn("com.aspose.words lic ERROR!");
        }

        try {
            long old = System.currentTimeMillis();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document doc = new Document(stream);
            doc.save(outputStream, SaveFormat.PDF);
            long now = System.currentTimeMillis();
            System.out.println("convert OK! " + ((now - old) / 1000.0) + "秒");

            bytes = outputStream.toByteArray();
        } catch (Exception e) {
            log.error(e);
        }

        return bytes;
    }

    /**
    * @Description: doc 转 png
    * @Param: [param]
    * @return: java.util.List<java.lang.String>
    * @Author: Jet.Chen
    * @Date: 2019/4/8 13:35
    */
    public static <T> List<String> doc2Png(T param) {
        byte[] bytes = null;
        if (param instanceof InputStream) {
            bytes = doc2Pdf((InputStream) param);
        } else if (param instanceof byte[]) {
            bytes = doc2Pdf(new ByteArrayInputStream((byte[]) param));
        }
        if (bytes == null) return null;
        return PDFUtil.transferPdf2Img(bytes, null, 100);
    }


}
