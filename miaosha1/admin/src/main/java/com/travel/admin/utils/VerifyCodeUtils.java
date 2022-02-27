package com.travel.admin.utils;

import com.travel.common.config.redis.RedisServiceImpl;
import com.travel.users.apis.entity.MiaoShaUser;
import com.travel.users.apis.entity.MiaoShaUserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class VerifyCodeUtils {


    //private String randString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";//随机产生只有字母的字符串
    //private String randString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";//随机产生数字与字母组合的字符串
    // 图片宽
    private int width = 95;
    // 图片高
    private int height = 25;
    // 干扰线数量
    private int lineSize = 40;
    // 随机产生字符数量
    private int stringNum = 4;

    @Autowired
    private RedisServiceImpl redisClient;


    private Random random = new Random();

    /**
     * 获得字体
     */
    private Font getFont() {
        return new Font("Fixedsys", Font.CENTER_BASELINE, 18);
    }

    /**
     * 获得颜色
     */
    private Color getRandColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 18);
        return new Color(r, g, b);
    }

    /**
     * 生成随机图片
     */
    public BufferedImage drawRandCode(String randomString) {
        // BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        // 产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
        Graphics g = image.getGraphics();
        //图片大小
        g.fillRect(0, 0, width, height);
        //字体大小
        g.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 18));
        //字体颜色
        g.setColor(getRandColor(110, 133));
        // 绘制干扰线
        for (int i = 0; i <= lineSize; i++) {
            drowLine(g);
        }
        // 绘制随机字符
        for (int i = 1; i <= stringNum; i++) {
            drowString(g, randomString.charAt(i-1), i);
        }
        g.dispose();
        return image;
    }

    /**
     * 绘制字符串
     */
    private void drowString(Graphics g, Character rand, int i) {
        g.setFont(getFont());
        g.setColor(new Color(random.nextInt(101), random.nextInt(111), random
                .nextInt(121)));

        g.translate(random.nextInt(3), random.nextInt(3));
        g.drawString(rand.toString(), 13 * i, 16);
    }

    /**
     * 绘制干扰线
     */
    private void drowLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x + xl, y + yl);
    }


}
