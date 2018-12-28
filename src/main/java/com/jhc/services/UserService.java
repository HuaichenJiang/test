package com.jhc.services;

import com.jhc.vo.Users;

import java.util.List;
import java.util.Map;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/11/6  10:40
 */
public interface UserService {

    List<Users> getAllUser();

    String getSaltByUserName(String userName);

    Map addUser(Users users);

    Map deleteUser(String id);

    List<Users> update(List<Users> users);

    Users getUserByUserNameAndPwd(String userName, String password);

    void getByCondition();

}
