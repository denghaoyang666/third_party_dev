package cn.hayye.third_party_dev.login.controller;

import cn.hayye.third_party_dev.login.service.QqLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private QqLoginService qqLoginService;

    @RequestMapping("/doLogin")
    public String doLogin(HttpServletRequest request, Model model){
        try {
            qqLoginService.signInHandle(request,model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/login/third_login.html";
    }

    @RequestMapping("/afterLogin")
    @ResponseBody
    public String afterLogin(HttpServletRequest request){
        String openID = "";
        if(qqLoginService.isLoginSuccess(request)){
            openID = qqLoginService.getOpenID(request);
            System.out.println("OPEN_ID:"+openID);
        }
        return openID;
    }
}
