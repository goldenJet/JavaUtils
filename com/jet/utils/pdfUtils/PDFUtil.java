package com.jet.util;


import com.aspose.pdf.Document;
import com.aspose.pdf.License;
import com.aspose.pdf.PageCollection;
import com.aspose.pdf.devices.PngDevice;
import com.aspose.pdf.devices.Resolution;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.jet.entity.ContractOnlineSigningPosition;
import com.jet.pdf.ITextRenderer2;
import com.jet.pdf.PDFBuilder;
import com.jet.pdf.PdfHtml;
import com.jet.pojo.FileBean;
import lombok.extern.log4j.Log4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.xhtmlrenderer.pdf.ITextFontResolver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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
    @Deprecated
    public static List<String> transferPdf2Img2(byte[] bytes, Integer page, Integer dpi){
        PDDocument doc = null;
        try {
            doc = PDDocument.load(bytes);
            int pageNum = doc.getNumberOfPages();
            if (page != null && (page < 1 || page > pageNum)) return null;
            page = page == null ? pageNum : page;
            List<String> result = new ArrayList<>();
            for (int i = 0; i < page; i++) {
                String s = transferPdf2ImgByPage2(doc, i, null);
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
    @Deprecated
    private static String transferPdf2ImgByPage2(PDDocument doc, int pageNum, Integer dpi){
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

    public static boolean getLicense() {
        boolean result = false;
        try {

            InputStream is = WordUtil.class.getClassLoader().getResourceAsStream("static/download/license2.xml"); //  license.xml路径
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

    public static List<String> transferPdf2Img(byte[] bytes, Integer page, Integer dpi){
        // 验证License
        if (!getLicense()) {
            log.warn("com.aspose.words lic ERROR!");
            return null;
        }
        try {
            Document pdfDocument = new Document(new ByteArrayInputStream(bytes));
            //图片宽度：800
            //图片高度：100
            // 分辨率 130
            //Quality [0-100] 最大100
            Resolution resolution = new Resolution(130);
            PngDevice pngDevice = new PngDevice(resolution);
            PageCollection pdfPages = pdfDocument.getPages();
            int pdfPageSize = pdfPages.size();
            if (page != null && (page < 1 || page > pdfPageSize)) return null;
            List<String> result = new ArrayList<>();

            ByteArrayOutputStream outputStream;
            byte[] resultBytes;
            if (page != null) {
                outputStream = new ByteArrayOutputStream();
                pngDevice.process(pdfPages.get_Item(page), outputStream);
                resultBytes = outputStream.toByteArray();
                result.add("data:image/png;base64," + new String(Base64.getEncoder().encode(resultBytes)));
            } else {
                for (int index = 1; index <= pdfPages.size(); index++) {
                    outputStream = new ByteArrayOutputStream();
                    pngDevice.process(pdfPages.get_Item(index), outputStream);
                    resultBytes = outputStream.toByteArray();
                    result.add("data:image/png;base64," + new String(Base64.getEncoder().encode(resultBytes)));
                }
            }
            return result;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }



    /**
     * @Author : jet
     * @Description html 转 pdf
     * @Date 9:26  2019/5/13
     */
    public static FileBean exportHtml2Pdf(String html,String fileName){
        FileBean fileBean = new FileBean();
        try {
            ByteArrayOutputStream tempOs=new ByteArrayOutputStream();
            ITextRenderer2 renderer = new ITextRenderer2();

            html = PdfHtml.getTargetHtml(html);

            renderer.setDocumentFromString(html);
            // 解决中文支持问题
            ITextFontResolver fontResolver = renderer.getFontResolver();
            //data/crm/fonts

            String fontPathPre = "/data/crm/fonts/";
            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                //windows系统下面的字体,一般开发环境使用
                fontPathPre = "C:\\Windows\\Fonts\\";
            }
            fontResolver.addFont(fontPathPre+"simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            fontResolver.addFont(fontPathPre+"msyh.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            fontResolver.addFont(fontPathPre+"simhei.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

            renderer.setPdfPageEvent(new PDFBuilder());
            renderer.layout();
            renderer.createPDF(tempOs);

            fileBean.setBytes(tempOs.toByteArray());
            fileBean.setFileName(fileName);
            fileBean.setSize(tempOs.size());
            return fileBean;
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
    * @Description: 获取pdf文件中的关键字坐标
    * @Param: [keyWords, bytes]
    * @return: java.util.List<com.wailian.entity.ContractOnlineSigningPosition>
    * @Author: Jet.Chen
    * @Date: 2019/5/19 21:28
    */
    public static List<ContractOnlineSigningPosition> getPdfKeyWordPosition(String keyWords, byte[] bytes) {
        List<ContractOnlineSigningPosition> result = new ArrayList<>();
        try {
            PdfKeyWordPosition kwp = new PdfKeyWordPosition(keyWords, bytes);
            List<float[]> list = kwp.getCoordinate();
            for (float[] floats : list) {
                ContractOnlineSigningPosition position = new ContractOnlineSigningPosition(floats[0], floats[1], floats[2], floats[3], floats[4]);
                result.add(position);
            }
        } catch (IOException e) {
            log.error("获取pdf文件中关键字的坐标，公共方法异常, " , e);
        }
        return result;
    }


}
