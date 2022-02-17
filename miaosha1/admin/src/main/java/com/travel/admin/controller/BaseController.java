package com.travel.admin.controller;

import com.travel.common.config.redis.RedisKeyPrefix;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Controller
public class BaseController {




    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    public String render(HttpServletRequest request, HttpServletResponse response, Model model, String tplName) {

        String html = "";
        if(!StringUtils.isEmpty(html)) {
            out(response, html);
            return null;
        }
        //手动渲染
        WebContext ctx = new WebContext(request,response,
                request.getServletContext(),request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process(tplName, ctx);
        out(response, html);
        return null;
    }

    public static void out(HttpServletResponse res, String html){
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");
        try{
            OutputStream out = res.getOutputStream();
            out.write(html.getBytes("UTF-8"));
            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
