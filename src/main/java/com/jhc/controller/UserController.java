package com.jhc.controller;

import com.jhc.services.UserService;
import com.jhc.vo.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/11/6  10:33
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/getAll")
    public ResponseEntity getAllUser(){
        return new ResponseEntity(userService.getAllUser(), HttpStatus.OK);
    }

    @RequestMapping(value = "/add")
    public ResponseEntity addUser(@RequestBody Users user){
        return new ResponseEntity(userService.addUser(user), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete")
    public ResponseEntity deleteUser(String id){
        return new ResponseEntity(userService.deleteUser(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/update")
    public ResponseEntity updateUser(@RequestBody Users user){
        return new ResponseEntity(userService.addUser(user), HttpStatus.OK);
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public ResponseEntity getByCondition(){
        try {
            userService.getByCondition();
            return new ResponseEntity("success",HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity("failure",HttpStatus.OK);
        }
    }

}
