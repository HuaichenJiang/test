package com.jhc.aop;

import com.jhc.util.JsonUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/11/9  12:00
 */
@Aspect
@Component
public class WebLogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebLogAspect.class);

    @Pointcut("execution(public * com.jhc.controller.*.*(..))")
    public void webLog(){}

    /**
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable{
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();

        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        String queryString = request.getQueryString();
        Object[] args = joinPoint.getArgs();
        String params = "";
        //获取请求参数集合并进行遍历拼接
        if(args.length>0){
            if("POST".equals(method)){
                Object object = args[0];
                Map map = getKeyAndValue(object);
                params = map.toString();
            }else if("GET".equals(method)){
                params = queryString;
            }
        }
        LOGGER.info("URL : {}", url);
        LOGGER.info("HTTP_METHOD : {}", method);
        LOGGER.info("IP : {}", ip);
        LOGGER.info("URI : {}", uri);
        LOGGER.info("PARAMS : {}", JsonUtil.serializeToJson(params));
//        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
//        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
//        LOGGER.info("URL : {}", httpServletRequest.getRequestURL().toString());
//        LOGGER.info("HTTP_METHOD : {}", httpServletRequest.getMethod());
//        LOGGER.info("IP : {}", httpServletRequest.getRemoteAddr());
//        Enumeration<String> enu = httpServletRequest.getParameterNames();
//        while(enu.hasMoreElements()){
//            String name = enu.nextElement();
//            LOGGER.info("name : {},value : {}", name, httpServletRequest.getParameter(name));
//        }
    }

    /**
     *
     * @param ret
     * @throws Throwable
     */
    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable{
        LOGGER.info("RESPONSE : {}", JsonUtil.serializeToJson(ret));
    }


    public static Map<String, Object> getKeyAndValue(Object obj) {
        Map<String, Object> map = new HashMap<>();
        // 得到类对象
        Class userCla = (Class) obj.getClass();
        /* 得到类中的所有属性集合 */
        Field[] fs = userCla.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            f.setAccessible(true); // 设置些属性是可以访问的
            Object val = new Object();
            try {
                val = f.get(obj);
                // 得到此属性的值
                map.put(f.getName(), val);// 设置键值
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return map;
    }


}
