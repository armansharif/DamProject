package com.dam.modules.user.repository;

import com.dam.modules.user.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Users findByEmail(String email);

    Users findByMobile(String mobile);

    @Query(nativeQuery = true, value = "select * from users where mobile =:username OR username=:username ")
    Users findByUsernameOrMobile(String username);

    Users findByMobileAndAdminPasswordAndAdminPasswordIsNotNull(String mobile, String adminPassword);

    Users findByUsernameAndAdminPasswordAndAdminPasswordIsNotNull(String username, String adminPassword);


    @Query(nativeQuery = true, value = "select * from users where mobile =:mobile and password=:code and  NOW()  <= DATE_ADD(updated_at, INTERVAL 5 MINUTE) ")
    Optional<Users> validateVerificationCode(@Param("mobile") String mobile, @Param("code") String code);

    @Query(nativeQuery = true, value = "select * from users where (mobile =:mobile OR  email =:email ) and password=:code and  NOW()  <= DATE_ADD(updated_at, INTERVAL 5 MINUTE) ")
    Users validateVerificationCode(@Param("mobile") String mobile,@Param("email") String email, @Param("code") String code);


    @Query(nativeQuery = true, value = "select * from users where email =:email and password=:code and  NOW()  <= DATE_ADD(updated_at, INTERVAL 5 MINUTE) ")
    Users validateVerificationCodeByEmail(@Param("email") String email, @Param("code") String code);


    @Query("select u from Users u where u.email = :email")
    Users findByQuery(@Param("email") String email);

    Page<Users> findAll(Specification<Users> postSpec , Pageable pageable );
    Page<Users> findAllByUserType(int userType , Pageable pageable );

}
