package com.jhc.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/11/10  16:25
 */
@Aspect
@Component
public class HttpAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpAspect.class);

    @Pointcut("within(com.jhc.controller..*) " +
            "&& !within(com.jhc.controller.LoginController) " +
            "&& !within(com.jhc.controller.RegisterController)")
    public void judgeLogin(){}

    @Around("judgeLogin()")
    public Object doBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String userName = (String) request.getSession().getAttribute("userName");
        String id = (String) request.getSession().getAttribute("id");
        if(null == userName){
            LOGGER.info("用户未登录");
            Map<String, String> result = new HashMap<>();
            result.put("message","用户未登录");
            return new ResponseEntity(result, HttpStatus.FORBIDDEN);
        }
        LOGGER.info("username: {}", userName);
        return joinPoint.proceed();
    }

}
