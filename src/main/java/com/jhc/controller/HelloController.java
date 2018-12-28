package com.jhc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/8/4  12:23
 */

@RestController
public class HelloController {


    private static final Logger LOG = LoggerFactory.getLogger(HelloController.class);

    @RequestMapping(value="/hello", method = RequestMethod.GET)
    public ResponseEntity say(@RequestParam(value = "id" ,required = false ,defaultValue = "0") String text){
        LOG.info(text);
        return new ResponseEntity("test", HttpStatus.OK);
    }

}
