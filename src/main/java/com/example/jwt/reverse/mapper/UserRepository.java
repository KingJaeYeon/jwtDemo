package com.example.jwt.reverse.mapper;

import com.example.jwt.reverse.model.Token;
import com.example.jwt.reverse.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface UserRepository {

    @Insert("insert into member (id,username,password,roles) values(member_id_seq.NEXTVAL,#{username},#{password},'ROLE_USER')")
    void insertUser(User user);

    @Select("select * from member where username = #{username}")
    Optional<User> findUsername(String username);

    @Select("select * from member where username = #{username} and password = #{password}")
    Optional<User> findUser(User user);

    @Insert("insert into token (username,accessToken,refreshToken) values (#{username},#{accessToken},#{refreshToken})")
    void insertToken(Token token);
}
