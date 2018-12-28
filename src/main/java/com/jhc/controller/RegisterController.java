package com.jhc.controller;

import com.jhc.services.UserService;
import com.jhc.vo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/11/15  17:13
 */
@RestController
public class RegisterController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/regist")
    public ResponseEntity addUser(@RequestBody Users user){
        return new ResponseEntity(userService.addUser(user), HttpStatus.OK);
    }
}
