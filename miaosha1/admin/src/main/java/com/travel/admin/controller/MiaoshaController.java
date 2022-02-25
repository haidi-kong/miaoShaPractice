package com.travel.admin.controller;

import com.travel.admin.config.mvc.UserCheckAndLimit;
import com.travel.admin.utils.VerifyCodeUtils;
import com.travel.common.enums.ResultStatus;
import com.travel.common.resultbean.ResultGeekQ;
import com.travel.order.apis.service.MiaoshaService;
import com.travel.users.apis.entity.MiaoShaUser;
import com.travel.users.apis.entity.MiaoShaUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

@Controller
@RequestMapping("/miaosha")
@Slf4j
public class MiaoshaController {

    @DubboReference
    MiaoshaService miaoshaService;

    @Autowired
    VerifyCodeUtils verifyCodeUtils;


    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public ResultGeekQ<Long> miaoshaResult(Model model, MiaoShaUser user,
                                           @RequestParam("goodsId") long goodsId) {
        ResultGeekQ result = ResultGeekQ.build();
        model.addAttribute("user", user);
        try {
            if (user == null) {
                result.withError(ResultStatus.SESSION_ERROR.getCode(), ResultStatus.SESSION_ERROR.getMessage());
                return result;
            }

            return miaoshaService.miaoshaResult(user, goodsId);
        } catch (Exception e) {
            result.withErrorCodeAndMessage(ResultStatus.MIAOSHA_FAIL);
            return result;
        }
    }


    @UserCheckAndLimit(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/{path}/confirm", method = RequestMethod.POST)
    @ResponseBody
    public ResultGeekQ<Integer> miaosha(MiaoShaUser user, @PathVariable("path") String path,
                                        @RequestParam("goodsId") long goodsId) {
        ResultGeekQ<Integer> result = ResultGeekQ.build();
        if (user == null) {
            result.withError(ResultStatus.SESSION_ERROR.getCode(), ResultStatus.SESSION_ERROR.getMessage());
            return result;
        }
        //验证path
        MiaoShaUserVo userVo = new MiaoShaUserVo();
        BeanUtils.copyProperties(user, userVo);
        ResultGeekQ<Boolean> check = miaoshaService.checkPath(userVo, goodsId, path);
        if (!ResultGeekQ.isSuccess(check)) {
            result.withError(ResultStatus.REQUEST_ILLEGAL.getCode(), ResultStatus.REQUEST_ILLEGAL.getMessage());
            return result;
        }
        return miaoshaService.miaoshaComfirm(user, goodsId);
    }


    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public ResultGeekQ<String> getMiaoshaVerifyCod(HttpServletResponse response, MiaoShaUser user,
                                                   @RequestParam("goodsId") long goodsId) {
        ResultGeekQ<String> result = ResultGeekQ.build();
        if (user == null) {
            result.withError(ResultStatus.SESSION_ERROR.getCode(), ResultStatus.SESSION_ERROR.getMessage());
            return result;
        }
        try {
            String randcodes = miaoshaService.getRandcode(user, goodsId);
            BufferedImage image = verifyCodeUtils.drawRandCode(randcodes);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return result;
        } catch (Exception e) {
            log.error("生成验证码错误-----goodsId:{}", goodsId, e);
            result.withError(ResultStatus.MIAOSHA_FAIL.getCode(), ResultStatus.MIAOSHA_FAIL.getMessage());
            return result;
        }
    }

    @UserCheckAndLimit(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public ResultGeekQ<String> getMiaoshaPath(HttpServletRequest request, MiaoShaUser user,
                                              @RequestParam("goodsId") long goodsId,
                                              @RequestParam(value = "verifyCode", defaultValue = "0") String verifyCode
    ) {
        ResultGeekQ<String> result = ResultGeekQ.build();
        if (user == null) {
            result.withError(ResultStatus.SESSION_ERROR.getCode(), ResultStatus.SESSION_ERROR.getMessage());
            return result;
        }

        MiaoShaUserVo userVo = new MiaoShaUserVo();
        BeanUtils.copyProperties(user, userVo);
        boolean check = miaoshaService.checkVerifyCode(userVo, goodsId, verifyCode);
        if (!check) {
            result.withError(ResultStatus.REQUEST_ILLEGAL.getCode(), ResultStatus.REQUEST_ILLEGAL.getMessage());
            return result;
        }
        ResultGeekQ<String> pathR = miaoshaService.createMiaoshaPath(userVo, goodsId);
        if (!ResultGeekQ.isSuccess(pathR)) {
            result.withError(pathR.getCode(), pathR.getMessage());
            return result;
        }
        result.setData(pathR.getData());
        return result;
    }

}