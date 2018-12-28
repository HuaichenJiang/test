package com.jhc.controller;

import com.jhc.services.UserService;
import com.jhc.util.MD5Util;
import com.jhc.vo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/11/10  18:18
 */
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login")
    public ResponseEntity login(HttpServletRequest request, @RequestBody Users users){
        Map<String, String> result = new HashMap<>();
        String salt = userService.getSaltByUserName(users.getUserName());
        String password = MD5Util.string2MD5(users.getUserPassword() + salt);
        Users user = userService.getUserByUserNameAndPwd(users.getUserName(), password);
        if (null != user){
            request.getSession().setAttribute("userName", user.getUserName());  // 保存username到session看这里
            result.put("message", "success");
            return new ResponseEntity(result, HttpStatus.OK);
        } else {
            result.put("message", "failure");
            return new ResponseEntity(result, HttpStatus.FORBIDDEN);
        }
    }
}
