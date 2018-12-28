package com.jhc.daos;

import com.jhc.vo.Users;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/11/6  10:40
 */
public interface UserDAO extends JpaRepository<Users,Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    Users getUserByUserNameAndUserPassword(String userName, String userPassword);

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    Users getUsersByUserName(String userName);

    /**
     * 使用@Query和hql进行查询
     * @param userName
     * @return
     */
    @Query("from Users where username = :userName")
    List<Users> findByUserNameHQL(@Param(value = "userName") String userName);

    /**
     * 使用原生sql进行查询
     * @param name
     * @return
     */
    @Query(value = "select * from users where user_name = :name", nativeQuery = true)
    List<Users> findByNameSQL(@Param(value = "name") String name);

    Page<Object[]> getByCondition(Users u);

}
