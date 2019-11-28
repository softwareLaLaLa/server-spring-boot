package com.example.paperservice.controller;

import com.example.paperservice.Entity.UserEntity;
import com.example.paperservice.database.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        System.out.println("登录，用户名：" + s);
        UserEntity systemUser = userDao.findByName(s);
        if (systemUser == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        //String password = new BCryptPasswordEncoder().encode(systemUser.getPassword());
        //System.out.println("加密后密码："+password);
        User user = new User(systemUser.getName(), systemUser.getPassword(),
                AuthorityUtils.commaSeparatedStringToAuthorityList(systemUser.getRole()));
        return user;
    }
}
