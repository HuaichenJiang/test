package com.jhc.services.impl;

import com.jhc.daos.UserDAO;
import com.jhc.services.UserService;
import com.jhc.util.MD5Util;
import com.jhc.vo.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/11/6  10:42
 */
@Service(value = "userService")
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDAO userDAO;

    @Override
    public List<Users> getAllUser() {
        return userDAO.findAll();
    }

    @Override
    public String getSaltByUserName(String userName){
        return userDAO.getUsersByUserName(userName).getSalt();
    }

    @Override
    public Map addUser(Users users) {
        Map<String, String> result = new HashMap<>();
        Date date = new Date();
        users.setState((byte)0);
        users.setCreatDate(date);
        users.setModifyDate(date);
        String salt = UUID.randomUUID().toString().replace("-","");
        users.setSalt(salt);
        users.setUserPassword(MD5Util.string2MD5(users.getUserPassword()+salt));
        try {
            userDAO.save(users);
            result.put("message", "success");
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("message", "failure");
            return result;
        }
    }

    @Override
    public Map deleteUser(String id) {
        Map<String, String> result = new HashMap<>();
        try {
            userDAO.delete(Integer.parseInt(id));
            result.put("message", "success");
            return result;
        } catch (Exception e){
            e.printStackTrace();
            result.put("message", "failure");
            return result;
        }
    }

    @Override
    public List<Users> update(List<Users> users) {
        List<Users> userList = new ArrayList<>();
        for(Users user : users){
            try{
                userDAO.save(user);
            } catch (Exception e){
                userList.add(user);
            }
        }
        return userList;
    }

    @Override
    public Users getUserByUserNameAndPwd(String userName, String userPassword) {
        Users user = userDAO.getUserByUserNameAndUserPassword(userName, userPassword);
        if (null != user){
            return user;
        }
        return user;
    }

    @Override
    public void getByCondition(){
        Users users = new Users();
        users.setUserName("JXM");
        Page u = userDAO.getByCondition(users);
        for(Object users1 : u){
            Users users2 = (Users) users1;
            LOG.info(users2.getUserName());
        }
    }

}
