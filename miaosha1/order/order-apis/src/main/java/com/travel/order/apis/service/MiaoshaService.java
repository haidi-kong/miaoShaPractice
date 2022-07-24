package com.travel.order.apis.service;

import com.travel.common.resultbean.ResultGeekQ;
import com.travel.order.apis.entity.GoodsVo;
import com.travel.order.apis.entity.MiaoShaOrderVo;
import com.travel.order.apis.entity.OrderInfoVo;
import com.travel.users.apis.entity.MiaoShaUser;
import com.travel.users.apis.entity.MiaoShaUserVo;
import org.mengyun.tcctransaction.api.EnableTcc;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.image.BufferedImage;

public interface MiaoshaService {
    public ResultGeekQ<OrderInfoVo> miaosha(MiaoShaUserVo user, GoodsVo goods);

    public ResultGeekQ<Integer> insertMiaoshaOrder(MiaoShaOrderVo miaoshaOrder);

    public ResultGeekQ<MiaoShaOrderVo> getMiaoshaOrderByUserIdGoodsId(Long userId, Long goodsId);

    public ResultGeekQ<String> createMiaoshaPath(MiaoShaUserVo user, Long goodsId);

    public ResultGeekQ<Long> getMiaoshaResult(Long userId, Long goodsId);

    public ResultGeekQ<Boolean> checkPath(MiaoShaUserVo user, long goodsId, String path);

    public String getRandcode(MiaoShaUser user, long goodsId);

    public boolean checkVerifyCode(MiaoShaUserVo user, long goodsId, String verifyCode);

    public ResultGeekQ<Integer> miaoshaComfirm(MiaoShaUser user, long goodsId);

    public ResultGeekQ<Long> miaoshaResult(MiaoShaUser user, long goodsId);

    //@EnableTcc
    public ResultGeekQ<Long> completeOrder(MiaoShaUser user, long orderId);
}
