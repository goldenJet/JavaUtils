package com.wailian.util;


import com.aspose.pdf.Document;
import com.aspose.pdf.License;
import com.aspose.pdf.devices.PngDevice;
import com.aspose.pdf.devices.Resolution;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * @Description: PDF 处理类
 * @Author: Jet.Chen
 * @Date: 2018/12/28
 */
@Log4j
public class PDFUtil {

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

    public static List<String> transferPdf2Img(byte[] bytes, Integer dpi){

        // 验证License 若不验证则转化出的pdf文档有水印
        if (!getLicense()) {
            log.warn("com.aspose.words lic ERROR!");
        }


        try {
            long old = System.currentTimeMillis();
            System.out.println("begin..............");
            //Document pdfDocument = new Document(new ByteArrayInputStream(bytes));
            Document pdfDocument = new Document(new FileInputStream("D:\\temp\\201907251646115d396c530efc5.pdf"));
            //图片宽度：800
            //图片高度：100
            // 分辨率 130
            //Quality [0-100] 最大100
            //例： new JpegDevice(800, 1000, resolution, 90);
            Resolution resolution = new Resolution(130);
            PngDevice pngDevice = new PngDevice(resolution);
            for (int index=1;index<=pdfDocument.getPages().size();index++) {
                //for (int index=4;index<=7;index++) {
                File file = new File("D:\\temp\\jet"+index+".png");// 输出路径
                FileOutputStream fileOS = new FileOutputStream(file);
                pngDevice.process(pdfDocument.getPages().get_Item(index), fileOS);
                fileOS.close();
            }

            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        transferPdf2Img(null, 100);
    }


}
