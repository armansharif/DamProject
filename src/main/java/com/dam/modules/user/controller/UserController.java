package com.dam.modules.user.controller;

import com.dam.commons.Routes;
import com.dam.enums.Authority;
import com.dam.modules.jwt.JwtUtils;
import com.dam.modules.user.model.Users;
import com.dam.modules.user.service.AddressesService;
import com.dam.modules.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.dam.config.JsonResponseBodyTemplate.createResponseJson;

@RestController
@RequestMapping(produces = "application/json")
@Api(value = "User Controller", description = "User controller for all user related action")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    private final AddressesService addressesService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    private MessageSource messageSource;


    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils, AddressesService addressesService, MessageSource messageSource) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.addressesService = addressesService;
        this.messageSource = messageSource;
    }

    @GetMapping(value = {Routes.GET_users_admin})
    @PreAuthorize("hasAuthority('OP_ACCESS_USER')")
    public List<Users> getAllUsers(
            @And({
                    @Spec(path = "mobile", spec = Like.class),
                    @Spec(path = "family", spec = Like.class),
                    @Spec(path = "email", spec = Like.class),
                    @Spec(path = "name", spec = Like.class)
            }) Specification<Users> userSpec,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int perPage,
            HttpServletResponse response) {
        logger.info("try to get user list.");
        return userService.findAllUsers(page, perPage, sort, userSpec);
    }


    @ApiIgnore
    @GetMapping(value = {Routes.GET_users_by_id})
    // @PreAuthorize("hasAuthority('OP_ACCESS_USER')")
    @ApiOperation(value = "Get a user by ID", response = ResponseEntity.class)
    public ResponseEntity<Object> getUser(@PathVariable(value = "id") Long id) {
        Users user = userService.findUser(id).orElse(null);
        if (user != null) {
            return ResponseEntity.ok()
                    .body(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = {Routes.GET_profile})
    // @PreAuthorize("hasAuthority('OP_ACCESS_USER')")
    public ResponseEntity<Object> getUserByToken(HttpServletRequest request) {
        Long user_id = jwtUtils.getUserId(request);
        Users user = userService.findUser(user_id).orElse(null);
        if (user != null) {
            return ResponseEntity.ok()
                    .body(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = {Routes.POST_forget_pass_mobile}, produces = "application/json")
    public String forgetPassUser(@RequestParam String mobile, HttpServletResponse response) {
        logger.info("try to reset pass.");
        mobile = jwtUtils.arabicToDecimal(mobile);
        userService.findUserByMobile(mobile);
        return userService.verificationUserCM(mobile);

    }

    @PostMapping(value = {Routes.POST_forget_pass_email}, produces = "application/json")
    public ResponseEntity<?> forgetPassUserByEmail(@RequestParam String email, HttpServletResponse response) {
        //validation email
        logger.info("try to verification by Email.");
        JSONObject resJson = new JSONObject();
        Users user = userService.findUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(
                    createResponseJson("fail", HttpStatus.NOT_FOUND.value(), "User not found").toString(),
                    HttpStatus.NOT_FOUND);
        } else {
            int res = userService.verificationUserByEmail(email);
            if (res > 0) {
                return ResponseEntity.ok()
                        .body(createResponseJson("success", HttpStatus.OK.value(), "Verification code sent successfully.").toString());
            } else {
                return new ResponseEntity<>(
                        createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unfortunately, there was a problem.").toString(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }


    }

    @PostMapping(value = {Routes.POST_user_verify_mobile}, produces = "application/json")
    public String preVerificationUser(@RequestParam String mobile, HttpServletResponse response) {
        mobile = jwtUtils.arabicToDecimal(mobile);
        // mobile= jwtUtils.urlDecode(mobile);
        logger.info("try to verification.");
        return userService.verificationUser(mobile);
    }

    @PostMapping(value = {Routes.POST_user_verify_email}, produces = "application/json")
    public ResponseEntity<?> preVerificationUserByEmail(@RequestParam String email, HttpServletResponse response) {
        //validation email
        logger.info("try to verification by Email.");
        int res = userService.verificationUserByEmail(email);
        JSONObject resJson = new JSONObject();

        if (res > 0) {
            return ResponseEntity.ok()
                    .body(createResponseJson("success", HttpStatus.OK.value(), "Verification code sent successfully.").toString());

        } else {
            return new ResponseEntity<>(
                    createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unfortunately, there was a problem.").toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping(value = {Routes.POST_user_auth_mobile}, produces = "application/json")
    public ResponseEntity<?> verificationUser(
            @RequestParam String mobile,
            @RequestParam String code,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String family,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) MultipartFile file,
            HttpServletResponse response) {
        mobile = jwtUtils.arabicToDecimal(mobile);


        Users user = userService.findUserByMobile(mobile);
        JSONObject okJson = new JSONObject();
        if (mobile.equals("09123456789") && code.equals("1234")) {
            //ورود بدون کد
        } else {
            userService.checkVerificationUser(mobile, code);
        }
        String token = jwtUtils.generateToken(mobile, user.getId());
        response.addHeader("Authorization", token);

        boolean isAdmin = user.getRoles().stream()
                .filter(c -> c.getName().equals("admin"))
                .findFirst()
                .isPresent();
        okJson.put("code", response.getStatus());
        okJson.put("token", token);
        okJson.put("status", "success");
        okJson.put("isAdmin", isAdmin);
        okJson.put("message", messageSource.getMessage("verification.successful", null, Locale.getDefault()));//"The token was created successfully. "
        logger.info(" verification successful");

        try {
            userService.updateUser(user.getId(), name, family, email, mobile, username, password, address, file);
        } catch (IOException e) {
            return new ResponseEntity<>(
                    createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok()
                .body(okJson.toString());
    }


    @PostMapping(value = {Routes.POST_user_auth_email}, produces = "application/json")
    public ResponseEntity<?> verificationUserByEmail(
            @RequestParam String email,
            @RequestParam String code,
            @RequestParam(required = false) String username,
            @RequestParam String password,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String family,
            @RequestParam String mobile,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) MultipartFile file,
            HttpServletResponse response) {

        username = mobile;
        Users user = userService.checkVerificationUser(mobile, email, code);
        if (user == null) {
            logger.info(" verification failed");
            JSONObject unSuccessfulLogin = new JSONObject();
            unSuccessfulLogin.put("code", response.getStatus());
            unSuccessfulLogin.put("token", "");
            unSuccessfulLogin.put("status", "fail");
            //"The verification code has expired or was entered incorrectly."
            unSuccessfulLogin.put("message", messageSource.getMessage("verification.wrong.code", null, Locale.getDefault()));
            return ResponseEntity.badRequest().body(unSuccessfulLogin.toString());
        }

        if (mobile != null) {
            Users userCheck = userService.findUserByMobile(mobile);
            if (userCheck != null) {
                logger.info(" mobile exist");
                JSONObject unSuccessfulLogin = new JSONObject();
                unSuccessfulLogin.put("code", response.getStatus());
                unSuccessfulLogin.put("token", "");
                unSuccessfulLogin.put("status", "fail");
                //"Mobile number already exist! please use another number."
                unSuccessfulLogin.put("message", messageSource.getMessage("mobile.exist", null, Locale.getDefault()));
                return ResponseEntity.badRequest().body(unSuccessfulLogin.toString());
            }
        }

        if (username != null) {
            Users userCheck = userService.findUserByMobile(username);
            if (userCheck != null) {
                logger.info(" username exist");
                JSONObject unSuccessfulLogin = new JSONObject();
                unSuccessfulLogin.put("code", response.getStatus());
                unSuccessfulLogin.put("token", "");
                unSuccessfulLogin.put("status", "fail");
                //"UserName already exist! please use UserName."
                unSuccessfulLogin.put("message", messageSource.getMessage("username.exist", null, Locale.getDefault()));

                return ResponseEntity.badRequest().body(unSuccessfulLogin.toString());
            }
        }

        String token = jwtUtils.generateToken(username, user.getId());
        response.addHeader("Authorization", token);
        JSONObject successfulLogin = new JSONObject();
        successfulLogin.put("code", response.getStatus());
        successfulLogin.put("token", token);
        successfulLogin.put("status", "success");
        //, "The token was created successfully. "
        successfulLogin.put("message", messageSource.getMessage("verification.successful", null, Locale.getDefault()));
        logger.info(" verification successful");
        //update user
        try {
            Users user_saved = userService.updateUser(user.getId(), name, family, email, mobile, username, password, address, file);
            if (user_saved == null) {
                return new ResponseEntity<>(
                        createResponseJson("fail", HttpStatus.NOT_FOUND.value(), "User not found").toString(),
                        HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(
                    createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok()
                .body(successfulLogin.toString());
    }


    @PostMapping(value = {Routes.POST_reset_pass_mobile}, produces = "application/json")
    public ResponseEntity<?> resetPasswordByMobile(
            @RequestParam String mobile,
            @RequestParam String code,
            @RequestParam String password,
            HttpServletResponse response) {
        mobile = jwtUtils.arabicToDecimal(mobile);
        Users user = userService.findUserByMobile(mobile);

        boolean isverify = false;
        try {
            isverify = userService.checkVerificationUserCMCOM(user, code);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject successfulLogin = new JSONObject();
        JSONObject unSuccessfulLogin = new JSONObject();
        if (!isverify) {
            logger.info(" verification failed");
            unSuccessfulLogin.put("code", response.getStatus());
            unSuccessfulLogin.put("token", "");
            unSuccessfulLogin.put("status", "fail");
            unSuccessfulLogin.put("message", "The verification code has expired or was entered incorrectly.");
            return ResponseEntity.badRequest().body(unSuccessfulLogin.toString());
        } else {
            String token = jwtUtils.generateToken(user.getUsername(), user.getId());
            response.addHeader("Authorization", token);

            successfulLogin.put("code", response.getStatus());
            successfulLogin.put("token", token);
            successfulLogin.put("status", "success");
            successfulLogin.put("message", "The token was created successfully. ");
            logger.info(" verification successful");
        }
        //update user
        try {
            Users user_saved = userService.updateUser(user.getId(), null, null, null, mobile, null, password, null, null);
            if (user_saved == null) {
                return new ResponseEntity<>(
                        createResponseJson("fail", HttpStatus.NOT_FOUND.value(), "Unfortunately, there was a problem.").toString(),
                        HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(
                    createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok()
                .body(successfulLogin.toString());
    }


    @PostMapping(value = {Routes.POST_reset_pass_email}, produces = "application/json")
    public ResponseEntity<?> resetPasswordByEmail(
            @RequestParam String email,
            @RequestParam String code,
            @RequestParam String password,
            HttpServletResponse response) {

        Users user = userService.checkVerificationUserByEmail(email, code);
        if (user == null) {
            logger.info(" verification failed");
            JSONObject unSuccessfulLogin = new JSONObject();
            unSuccessfulLogin.put("code", response.getStatus());
            unSuccessfulLogin.put("token", "");
            unSuccessfulLogin.put("status", "fail");
            unSuccessfulLogin.put("message", "The verification code has expired or was entered incorrectly.");
            return ResponseEntity.badRequest().body(unSuccessfulLogin.toString());
        }
        String token = jwtUtils.generateToken(user.getUsername(), user.getId());
        response.addHeader("Authorization", token);
        JSONObject successfulLogin = new JSONObject();
        successfulLogin.put("code", response.getStatus());
        successfulLogin.put("token", token);
        successfulLogin.put("status", "success");
        successfulLogin.put("message", "The token was created successfully. ");
        logger.info(" verification successful");
        //update user
        try {
            Users user_saved = userService.updateUser(user.getId(), null, null, email, null, null, password, null, null);
            if (user_saved == null) {
                return new ResponseEntity<>(
                        createResponseJson("fail", HttpStatus.NOT_FOUND.value(), "User not found").toString(),
                        HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(
                    createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok()
                .body(successfulLogin.toString());
    }

    @PostMapping(value = {Routes.POST_login}, produces = "application/json")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String pass, HttpServletResponse response) {

        pass = jwtUtils.urlDecode(pass);
        Users user = userService.login(username, pass);
        if (user == null) {
            logger.info(" login failed");
            JSONObject unSuccessfulLogin = new JSONObject();
            unSuccessfulLogin.put("code", response.getStatus());
            unSuccessfulLogin.put("token", "");
            unSuccessfulLogin.put("status", "fail");
            unSuccessfulLogin.put("message", "The username or password is incorrect. ");
            return ResponseEntity.badRequest().body(unSuccessfulLogin.toString());
        }
        String token = jwtUtils.generateToken(username, user.getId());
        response.addHeader("Authorization", token);
        JSONObject successfulLogin = new JSONObject();
        successfulLogin.put("code", response.getStatus());
        successfulLogin.put("token", token);
        successfulLogin.put("status", "success");
        successfulLogin.put("message", "The token was created successfully. ");
        logger.info(" verification successful");
        return ResponseEntity.ok()
                .body(successfulLogin.toString());

    }


    @PostMapping(value = {Routes.POST_admin_login}, produces = "application/json")
    public ResponseEntity<?> adminLogin(@RequestParam String username, @RequestParam String pass, HttpServletResponse response) {

        pass = jwtUtils.urlDecode(pass);
        Users user = userService.login(username, pass);
        if (user == null) {
            logger.info(" login failed");
            JSONObject unSuccessfulLogin = new JSONObject();
            unSuccessfulLogin.put("code", response.getStatus());
            unSuccessfulLogin.put("token", "");
            unSuccessfulLogin.put("status", "fail");
            unSuccessfulLogin.put("message", "The username or password is incorrect. ");
            return ResponseEntity.badRequest().body(unSuccessfulLogin.toString());
        } else if (!user.getRoles().isEmpty()) {
            String token = jwtUtils.generateToken(username, user.getId());
            response.addHeader("Authorization", token);
            JSONObject successfulLogin = new JSONObject();
            successfulLogin.put("code", response.getStatus());
            successfulLogin.put("token", token);
            successfulLogin.put("status", "success");
            successfulLogin.put("message", "The token was created successfully. ");
            logger.info(" verification successful");
            return ResponseEntity.ok()
                    .body(successfulLogin.toString());
        } else {
            logger.info(" login failed");
            JSONObject unSuccessfulLogin = new JSONObject();
            unSuccessfulLogin.put("code", response.getStatus());
            unSuccessfulLogin.put("token", "");
            unSuccessfulLogin.put("status", "fail");
            unSuccessfulLogin.put("message", "The user is not an administrator");
            return ResponseEntity.badRequest().body(unSuccessfulLogin.toString());
        }


    }


    @PostMapping(value = {Routes.POST_admin_users})
    @PreAuthorize("hasAuthority('OP_ADD_USER')")
    public ResponseEntity<Object> addUser(@ModelAttribute Users users) {
        try {
            if (userService.findUserByMobile(users.getMobile()) == null) {
                Users user_saved = userService.registerUser(users);
                return ResponseEntity.ok()
                        .body(user_saved);
            } else {
                return new ResponseEntity<>(
                        createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), "The user is already registered with this phone number").toString(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                    createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @PutMapping(value = {Routes.PUT_profile})
    // @PreAuthorize("hasAuthority('OP_ACCESS_PUBLIC')")
    public ResponseEntity<Object> updateUser(
            @RequestParam String name,
            @RequestParam String family,
            @RequestParam String address,
            @RequestParam(required = false) MultipartFile file,
            HttpServletRequest request
    ) {
        try {

            Long user_id = jwtUtils.getUserId(request);
            Users user = userService.findUser(user_id).orElse(null);
            if (user != null) {
                name = jwtUtils.urlDecode(name);
                family = jwtUtils.urlDecode(family);
                address = jwtUtils.urlDecode(address);
                Users user_saved = userService.updateUser(user_id, name, family, null, null, null, null, address, file);
                if (user_saved != null) {
                    return ResponseEntity.ok()
                            .body(user_saved);
                } else {
                    return new ResponseEntity<>(
                            createResponseJson("fail", HttpStatus.NOT_FOUND.value(), "user not found").toString(),
                            HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(
                        createResponseJson("fail", HttpStatus.NOT_FOUND.value(), "user not found").toString(),
                        HttpStatus.NOT_FOUND);
            }


        } catch (Exception e) {
            return new ResponseEntity<>(
                    createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @DeleteMapping(value = {"/users/addresses/{id}", "/users/addresses/{id}/"})
    //  @PreAuthorize("hasAuthority('OP_ACCESS_PUBLIC')")
    public ResponseEntity<Object> deleteAddresses(@PathVariable(value = "id") Long id) {
        try {
            this.addressesService.deleteAddresses(id);
            return ResponseEntity.ok()
                    .body(createResponseJson("success", HttpStatus.OK.value(), "Address deleted successfully").toString());
        } catch (Exception e) {
            return new ResponseEntity<>(
                    createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping(value = {"/users/{id}", "/users/{id}/"})
    @PreAuthorize("hasAuthority('OP_DELETE_USER')")
    public ResponseEntity<Object> deleteUsers(@PathVariable(value = "id") Long id, HttpServletResponse response) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok()
                    .body(createResponseJson("success", response.getStatus(), "User deleted successfully").toString());
        } catch (Exception e) {
            return new ResponseEntity<>(
                    createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }


    @GetMapping(value = {"/users/{id}/addresses", "/users/{id}/addresses/"})
    // @PreAuthorize("hasAuthority('OP_ACCESS_PUBLIC')")
    public ResponseEntity<Object> getAddressOfUser(@PathVariable(value = "id") Long id) {
        try {

            //check token is valid
            Optional<Users> user = this.userService.findUser(id);
            if (user.isPresent()) {
                return ResponseEntity.ok()
                        .body(this.addressesService.findAddressOfUser(user.get()));
            } else {
                return new ResponseEntity<>(
                        createResponseJson("fail", HttpStatus.FORBIDDEN.value(), "The user is not valid").toString(),
                        HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                    createResponseJson("fail", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}
