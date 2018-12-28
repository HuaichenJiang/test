package com.jhc.daos.impl;

import com.jhc.vo.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/11/8  10:19
 */
public class UserDAOImpl {
    @PersistenceContext
    private EntityManager em;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDAOImpl.class);

    public Page<Object[]> getByCondition(Users u){
        String hql = "from Users as u where 1=1 and u.userName=:userName";
        Query q = em.createQuery(hql);
        q.setParameter("userName", u.getUserName());
        q.setFirstResult(0);
        q.setMaxResults(1);
        Page<Object[]> page = new PageImpl<Object[]>(q.getResultList(),new PageRequest(0,1),3);
        LOGGER.info(page.toString());
        return page;
    }
}
