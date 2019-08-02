package com.wailian.util;


import lombok.extern.log4j.Log4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @Description: PDF 处理类
 * @Author: Jet.Chen
 * @Date: 2018/12/28
 */
@Log4j
public class PDFUtil {


    /**
     * @Description: PDF 转 img
     * @Param: [bytes, page, dpi]
     * page：可选参数，如果有，则获取指定页面，从1开始
     * dpi：可选参数，数值越高，越清晰，但是效率会低
     * @return: java.util.List<java.lang.String>
     * @Author: Jet.Chen
     * @Date: 2018/12/28
     */
    public static List<String> transferPdf2Img(byte[] bytes, Integer page, Integer dpi){
        PDDocument doc = null;
        try {
            doc = PDDocument.load(bytes);
            int pageNum = doc.getNumberOfPages();
            if (page != null && (page < 1 || page > pageNum)) return null;
            page = page == null ? pageNum : page;
            List<String> result = new ArrayList<>();
            for (int i = 0; i < page; i++) {
                String s = transferPdf2ImgByPage(doc, i, null);
                if (s != null) result.add("data:image/png;base64," + s);
            }
            return result;
        } catch (IOException e) {
            log.error("pdf 读取失败");
        } finally {
            if (doc != null) {
                try {
                    doc.close();
                } catch (IOException e) {
                    log.error("PDDocument close error", e);
                }
            }
        }
        return null;
    }

    /**
     * @Description: PDF 单页转 img
     * @Param: [doc, pageNum, dpi]
     * dpi 越高，越清晰，但是效率会低
     * @return: java.lang.String
     * @Author: Jet.Chen
     * @Date: 2018/12/29
     */
    private static String transferPdf2ImgByPage(PDDocument doc, int pageNum, Integer dpi){
        dpi = dpi == null? 100: dpi;
//        PDPage page = doc.getPage(pageNum);
        PDFRenderer pdfRenderer = new PDFRenderer(doc);
        String result = null;
        try {
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageNum, dpi);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);
            byte[] bytes = outputStream.toByteArray();
            result = new String(Base64.getEncoder().encode(bytes));
        } catch (IOException e) {
            log.error("pdf单页转图片异常", e);
        }
        return result;
    }


}
