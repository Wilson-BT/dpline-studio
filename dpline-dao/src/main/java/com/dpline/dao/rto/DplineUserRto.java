package com.dpline.dao.rto;

import com.dpline.dao.entity.User;

public class DplineUserRto extends GenericRto<User>{

    public DplineUserRto() {
        setVo(new User());
    }

    public User getUser() {
        return (User) getVo();
    }

    public void setUser(User vo) {
        setVo(vo);
    }

}
