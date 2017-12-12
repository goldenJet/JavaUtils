package com.jet.utils.qrcode;

import com.swetake.util.Qrcode;
import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.data.QRCodeImage;
import jp.sourceforge.qrcode.exception.DecodingFailedException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @Author: Jet
 * @Description: 二维码工具类
 * @Date: 2017/12/11 13:29
 */
public class QRCodeUtil {

    /**
     * 生成二维码(QRCode)图片
     * @param content 存储内容
     * @param imgPath 图片路径
     */
    public static void encodeQRCode(String content, String imgPath) {
        encodeQRCode(content, imgPath, "png", 7);
    }

    /**
     * 生成二维码(QRCode)图片
     * @param content 存储内容
     * @param output 输出流
     */
    public static void encodeQRCode(String content, OutputStream output) {
        encodeQRCode(content, output, "png", 7);
    }

    /**
     * 生成二维码(QRCode)图片
     * @param content 存储内容
     * @param imgPath 图片路径
     * @param imgType 图片类型
     */
    public static void encodeQRCode(String content, String imgPath, String imgType) {
        encodeQRCode(content, imgPath, imgType, 7);
    }

    /**
     * 生成二维码(QRCode)图片
     * @param content 存储内容
     * @param output 输出流
     * @param imgType 图片类型
     */
    public static void encodeQRCode(String content, OutputStream output, String imgType) {
        encodeQRCode(content, output, imgType, 10);
    }

    /**
     * 生成二维码(QRCode)图片
     * @param content 存储内容
     * @param imgPath 图片路径
     * @param imgType 图片类型
     * @param size 二维码尺寸
     */
    public static void encodeQRCode(String content, String imgPath, String imgType, int size) {
        try {
            BufferedImage bufImg = qRCodeCommon(content, imgType, size);

            File imgFile = new File(imgPath);
            // 生成二维码QRCode图片
            ImageIO.write(bufImg, imgType, imgFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成二维码(QRCode)图片
     * @param content 存储内容
     * @param output 输出流
     * @param imgType 图片类型
     * @param size 二维码尺寸
     */
    public static void encodeQRCode(String content, OutputStream output, String imgType, int size) {
        try {
            BufferedImage bufImg = qRCodeCommon(content, imgType, size);
            // 生成二维码QRCode图片
            ImageIO.write(bufImg, imgType, output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 生成二维码图片的公共方法
     * @author Jet
     * @param content 存储内容
     * @param imgType 图片类型
     * @param size 二维码尺寸
     */
    private static BufferedImage  qRCodeCommon(String content, String imgType, int size){
        BufferedImage bufImg = null;
        //int width = 139;
        //int height = 139;
        //实例化Qrcode
        Qrcode qrcode = new Qrcode();
        //设置二维码的排错率L(7%) M(15%) Q(25%) H(35%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
        qrcode.setQrcodeErrorCorrect('M');
        qrcode.setQrcodeEncodeMode('B');
        //设置二维码尺寸(1~49)，值越大尺寸越大，可存储的信息越大
        qrcode.setQrcodeVersion(size);
        //qrcode.setQrcodeVersion(7);
        //设置图片尺寸
        int imageSize = 67 + 12 * (size - 1);
        bufImg = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_BGR);
        //BufferedImage bufImg=new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);

        //绘制二维码图片
        Graphics2D gs = bufImg.createGraphics();
        //设置二维码背景颜色
        gs.setBackground(Color.WHITE);
        //创建一个矩形区域
        gs.clearRect(0, 0, imageSize, imageSize);
        //gs.clearRect(0, 0, width, height);
        //设置二维码的图片颜色值 黑色
        gs.setColor(Color.BLACK);

        //获取内容的字节数组,设置编码集
        try {
            byte[] contentBytes=content.getBytes("utf-8");
            // 设置偏移量，不设置可能导致解析出错
            int pixoff=2;
            //输出二维码
            if(contentBytes.length > 0 && contentBytes.length < 120){
                boolean[][] codeOut = qrcode.calQrcode(contentBytes);
                for(int i = 0 ; i < codeOut.length ; i++){
                    for(int j = 0 ; j < codeOut.length ; j++){
                        if(codeOut[j][i]){
                            gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
                        }
                    }
                }
            }

            gs.dispose(); // 释放此图形的上下文以及它使用的所有系统资源。调用 dispose 之后，就不能再使用 Graphics 对象
            bufImg.flush(); // 刷新此 Image 对象正在使用的所有可重构的资源
            //生成二维码图片
            //File imgFile=new File(imgPath);
            //ImageIO.write(bufImg, "png", imgFile);

            //System.out.println("二维码生成成功，内容：" + content);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bufImg;

    }

    /**
     * 解析二维码，返回解析内容
     * @author Jet
     */
    public static String decodeQRCode(File imageFile) {
        String decodedData = null;
        QRCodeDecoder decoder = new QRCodeDecoder();
        BufferedImage image = null;
        try {
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            decodedData = new String(decoder.decode(new MyQRCodeImage(image)), "utf-8");
            System.out.println("Output Decoded Data is：" + decodedData);
        } catch (DecodingFailedException dfe) {
            System.out.println("Error: " + dfe.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decodedData;
    }
}

/**
 * @Author: Jet
 * @Description: 二维码实体类
 * @Date: 2017/12/12 14:19
 */
class MyQRCodeImage implements QRCodeImage {
    BufferedImage image;

    public MyQRCodeImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public int getPixel(int x, int y) {
        return image.getRGB(x, y);
    }
}
