package com.travel.admin.controller;

import com.travel.admin.config.mvc.UserCheckAndLimit;
import com.travel.common.resultbean.ResultGeekQ;
import com.travel.order.apis.entity.GoodsDetailVo;
import com.travel.order.apis.entity.GoodsVo;
import com.travel.order.apis.service.GoodsService;
import com.travel.users.apis.entity.MiaoShaUser;
import com.travel.users.apis.service.MiaoShaUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * @auther luo
 * @date 2019/11/10
 */
@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController extends BaseController {

    @DubboReference
    private GoodsService goodsService;

    @UserCheckAndLimit(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/to_detail")
    @ResponseBody
    public ResultGeekQ<GoodsDetailVo> goodsDetail(MiaoShaUser user, String goodsId) {
        return goodsService.goodsDetail(user, goodsId);
    }

    @RequestMapping(value = "/to_list")
    @ResponseBody
    public String goodsList(HttpServletRequest request, HttpServletResponse response,
                            MiaoShaUser user, Model model) {
        model.addAttribute("user", user);
        ResultGeekQ<List<GoodsVo>> goodsR = goodsService.goodsVoList();
        if(!ResultGeekQ.isSuccess(goodsR)){
            //todo 如何处理
            return null;
        }

        model.addAttribute("goodsList", goodsR.getData());
        return render(request, response, model, "goods_list");
    }
}
