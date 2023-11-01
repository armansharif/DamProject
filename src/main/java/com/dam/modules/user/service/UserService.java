package com.dam.modules.user.service;


import com.dam.commons.exception.UserServiceException;
import com.dam.modules.convert.ConvertEnFa;
import com.dam.modules.jwt.JwtUtils;

import com.dam.modules.mail.SendMail;
import com.dam.modules.sms.SmsVerification;
import com.dam.modules.user.model.Roles;
import com.dam.modules.user.model.Users;
import com.dam.modules.user.repository.RolesRepository;
import com.dam.modules.user.repository.UsersRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service

public class UserService implements UserDetailsService {

    Logger logger = LoggerFactory.getLogger(UserService.class);
    private final SmsVerification smsVerification;

    private final RolesRepository rolesRepository;
    private UsersRepository usersRepository;

    private MessageSource messageSource;
    private Environment env;

    @Autowired
    public UserService(UsersRepository usersRepository, ConvertEnFa convertEnFa, SmsVerification smsVerification, RolesRepository rolesRepository, MessageSource messageSource, Environment env) {
        this.usersRepository = usersRepository;
        this.smsVerification = smsVerification;
        this.rolesRepository = rolesRepository;
        this.messageSource = messageSource;
        this.env = env;
    }

    @Value("${general.user.registrationOnFirstLogin.enable}")
    private boolean registrationOnFirstLogin;


    public String generateCode(Users users) {
        return "code";
        //   return "code" + users.getMobile().substring((users.getMobile().length() - 4), users.getMobile().length());
    }

    public String verificationUser(String mobile) {
        env.getProperty("dam.urlUserImage");

        String response = "";
        try {
            String smsCode = smsVerification.generateCode() + "";
            Users user = usersRepository.findByMobile(mobile);
            if (user != null) {
                user.setPassword(smsCode);
                this.usersRepository.save(user);
            } else if (registrationOnFirstLogin) {
                Set<Roles> roles = new HashSet<>();
                roles.add(rolesRepository.findRolesByName("user"));
                user = new Users(mobile, smsCode, roles);
                user.setCode(generateCode(user));
                //register new user
                this.usersRepository.save(user);
            } else {
                JSONObject resJson = new JSONObject();
                resJson.put("code", 200);
                resJson.put("status", "fail");
                resJson.put("message", "User not found");
                return resJson.toString();
            }
            if (user != null) {
                //  response = smsVerification.sendSmsVerificationGhasedak(mobile, smsCode);
                response = smsVerification.sendSmsVerificationSMSIR(mobile, smsCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String verificationUserCM(String mobile) {
        String response = "";
        JSONObject resJson = new JSONObject();
        try {
            String idOfCM = smsVerification.sendSmsGenerateCMCOM(mobile);
            Users user = usersRepository.findByMobile(mobile);
            if (user != null) {
                user.setPassword(idOfCM);
                this.usersRepository.save(user);

            } else {
                Set<Roles> roles = new HashSet<>();
                roles.add(rolesRepository.findRolesByName("user"));
                user = new Users(mobile, idOfCM, roles);
                user.setCode(generateCode(user));
                user.setEmail(UUID.randomUUID().toString());
                //register new user
                this.usersRepository.save(user);
            }

            resJson.put("code", 200);
            resJson.put("status", "success");
            resJson.put("message", "Verification code sent successfully.");
        } catch (IOException e) {
            resJson.put("code", 401);
            resJson.put("status", "fail");
            resJson.put("message", "Unfortunately, there is a problem");
            e.printStackTrace();
        }
        return resJson.toString();
    }

    public int verificationUserByEmail(String email) {
        SendMail sendMail = new SendMail();
        int response = -1;
        try {
            String smsCode = smsVerification.generateCode() + "";
            Users user = usersRepository.findByEmail(email);
            if (user != null) {
                user.setPassword(smsCode);
                this.usersRepository.save(user);

            } else {
                Set<Roles> roles = new HashSet<>();
                roles.add(rolesRepository.findRolesByName("user"));
                user = new Users("", email, smsCode, roles);
                user.setCode(generateCode(user));
                user.setMobile(UUID.randomUUID().toString());
                //register new user
                this.usersRepository.save(user);
            }
            response = sendMail.sendMail(email, " Verification Code : ", " <p> <h3>    " + smsCode + "</h3></p>");
            //  response =   sendMail.asycSendMailNewUser(email,  " asycSendMailNewUser Verification Code ", " <p> <h3>    " + smsCode + "</h3></p>");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Users registerUser(Users users) throws IOException {
        String urlPostImageString = env.getProperty("dam.urlUserImage");

        String rootUserImageString = env.getProperty("dam.rootUserImage");
        Path rootUsersImage = Paths.get(rootUserImageString);
        String path = rootUsersImage.toFile().getAbsolutePath();


        if (users.getFile() != null && users.getFile().getSize() > 0) {
            byte[] bytes = users.getFile().getBytes();
            String fileName = UUID.randomUUID() + "." + Objects.requireNonNull(users.getFile().getContentType()).split("/")[1];
            Files.write(Paths.get(path + File.separator + fileName), bytes);
            users.setImg(urlPostImageString + fileName);
        }
        users.setCode(generateCode(users));
        return this.usersRepository.save(users);
    }

    public Users updateUser(Long id,
                            String name,
                            String family,
                            String email,
                            String address,
                            MultipartFile file) throws IOException {
        Optional<Users> user = usersRepository.findById(id);
        String urlPostImageString = env.getProperty("dam.urlUserImage");
        String rootUserImageString = env.getProperty("dam.rootUserImage");
        Path rootUsersImage = Paths.get(rootUserImageString);
        if (user.isPresent()) {
            user.get().setName(name);
            user.get().setFamily(family);
            user.get().setEmail(email);
            user.get().setAddress(address);
            String path = rootUsersImage.toFile().getAbsolutePath();
            if (file != null) {
                byte[] bytes = file.getBytes();
                String fileName = UUID.randomUUID() + "." + Objects.requireNonNull(file.getContentType()).split("/")[1];
                Files.write(Paths.get(path + File.separator + fileName), bytes);
                user.get().setImg(urlPostImageString + fileName);
            }
        }
        return this.usersRepository.save(user.orElse(null));
    }

    public Users updateUser(Long id,
                            String name,
                            String family,
                            String email,
                            String mobile,
                            String username,
                            String password,
                            String address,
                            MultipartFile file) throws IOException {
        Optional<Users> user = usersRepository.findById(id);
        String urlPostImageString = env.getProperty("dam.urlUserImage");
        String rootUserImageString = env.getProperty("dam.rootUserImage");
        Path rootUsersImage = Paths.get(rootUserImageString);
        if (user.isPresent()) {
            if (name != null)
                user.get().setName(name);
            if (family != null)
                user.get().setFamily(family);
            if (email != null)
                user.get().setEmail(email);
            if (mobile != null)
                user.get().setMobile(mobile);
            if (username != null)
                user.get().setUsername(username);
            if (password != null)
                user.get().setAdminPassword(password);
            if (address != null)
                user.get().setAddress(address);
            String path = rootUsersImage.toFile().getAbsolutePath();
            if (file != null) {
                byte[] bytes = file.getBytes();
                String fileName = UUID.randomUUID() + "." + Objects.requireNonNull(file.getContentType()).split("/")[1];
                Files.write(Paths.get(path + File.separator + fileName), bytes);
                user.get().setImg(urlPostImageString + fileName);
            }
        }
        Users user_saved = null;
        try {
            user_saved = this.usersRepository.save(user.orElse(null));
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return this.usersRepository.save(user.orElse(null));
    }

    public List<Users> findAllUsers() {
        return this.usersRepository.findAll();
    }

    public List<Users> findAllUsers(int page, int perPage, String sort, Specification<Users> userSpec) {
        Pageable postSortedAndPagination =
                PageRequest.of(page, perPage, Sort.by(sort).ascending());
        Page<Users> pagedResult = this.usersRepository.findAll(userSpec, postSortedAndPagination);
        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<Users>();
        }
    }

    public List<Users> findDamDar(int page, int perPage, String sort) {
        Pageable postSortedAndPagination =
                PageRequest.of(page, perPage, Sort.by(sort).ascending());
        Page<Users> pagedResult = this.usersRepository.findAllByUserType(1, postSortedAndPagination);
        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<Users>();
        }
    }

    public Optional<Users> findUser(Long id) {
        return this.usersRepository.findById(id);
    }

    public Users findUserByMobile(String mobile) {
//        if (userEntity == null) {
//            throw new UserServiceException("User " + username + " not found");
//        }

        return this.usersRepository.findByMobile(mobile);
    }

    public Users findUserByUserName(String username) {
        return this.usersRepository.findByUsername(username);
    }

    public Users findUserByEmail(String email) {
        return this.usersRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByUsername(username);

    }

    public Optional<Users> checkVerificationUser(String mobile, String code) {
        return usersRepository.validateVerificationCode(mobile, code);
    }

    public boolean checkVerificationUserCMCOM(Users user, String code) throws IOException {
        return smsVerification.verificationCMCOM(user.getPassword(), code);
    }

    public Users checkVerificationUser(String mobile, String email, String code) {
        return usersRepository.validateVerificationCode(mobile, email, code);
    }

    public Users checkVerificationUserByEmail(String email, String code) {
        return usersRepository.validateVerificationCodeByEmail(email, code);
    }

    public Users checkVerificationAdmin(String mobile, String pass) {
        return usersRepository.findByMobileAndAdminPasswordAndAdminPasswordIsNotNull(mobile, pass);
    }

    public Users login(String username, String pass) {
        return usersRepository.findByUsernameAndAdminPasswordAndAdminPasswordIsNotNull(username, pass);
    }


    public void deleteUser(Long id) throws Exception {
        this.usersRepository.deleteById(id);
    }

    public boolean checkUserAndToken(Long userId, HttpServletRequest request) {
        JwtUtils jwtUtils = new JwtUtils();
        Long userIdByToken = jwtUtils.getUserId(request);
        //check token is valid
        Optional<Users> user = findUser(userId);
        if (user.isPresent() && userId == userIdByToken) {
            return true;
        } else {
            return false;
        }
    }

    public Long getUserIdByToken(HttpServletRequest request) {
        JwtUtils jwtUtils = new JwtUtils();
        return jwtUtils.getUserId(request);
    }

    public Users getUserByToken(HttpServletRequest request) {
        JwtUtils jwtUtils = new JwtUtils();
        Users user = findUser(getUserIdByToken(request)).orElse(null);
        if (user == null)
            throw new UserServiceException(messageSource.getMessage("user.notFound", null, null));
        return user;
    }
}
