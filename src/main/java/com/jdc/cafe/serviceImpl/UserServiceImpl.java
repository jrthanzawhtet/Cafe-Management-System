package com.jdc.cafe.serviceImpl;

import com.jdc.cafe.JWT.CustomerUserDetailsService;
import com.jdc.cafe.JWT.JwtFilter;
import com.jdc.cafe.JWT.JwtUtil;
import com.jdc.cafe.POJO.User;
import com.jdc.cafe.utils.EmailUtils;
import com.jdc.cafe.wrapper.UserWrapper;
import com.jdc.cafe.constents.CafeConstants;
import com.jdc.cafe.dao.UserDao;
import com.jdc.cafe.service.UserService;
import com.jdc.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup{}", requestMap);
       try {
           if (validateSignUpMap(requestMap)){
               User user = userDao.findByEmail(requestMap.get("email"));
               if (Objects.isNull(user)){
                   userDao.save(getUserFromMap(requestMap));
                   return CafeUtils.getResponseEntity("Successfully registered", HttpStatus.OK);
               }
               else
               {
                   return CafeUtils.getResponseEntity("Email already exists.",HttpStatus.BAD_REQUEST);
               }
           }
           else {
               return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
           }
       }catch (Exception ex){
           ex.printStackTrace();
       }
       return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateSignUpMap(Map<String,String> requestMap){
       if ( requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
               && requestMap.containsKey("email") && requestMap.containsKey("password"))
       {
           return true;
       }

       return false;
    }

    private User getUserFromMap(Map<String,String> reqestMap){
        User user = new User();
        user.setName(reqestMap.get("name"));
        user.setContactNumber(reqestMap.get("contactNumber"));
        user.setEmail(reqestMap.get("email"));
        user.setPassword(reqestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password"))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            if (auth.isAuthenticated()) {
                if (customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\""+
                            jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                                    customerUserDetailsService.getUserDetail().getRole()) + "\"}",
                    HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<String>("{\"message\":\"" + "Wait for admin approval."+"\"}"
                            ,HttpStatus.BAD_REQUEST);
                }
            }
        }catch (Exception ex){
            log.error("{}",ex);
        }
        return new ResponseEntity<String>("{\"message\":\"" + "Bad Credentials."+"\"}"
                ,HttpStatus.BAD_REQUEST);

    }


    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            if (jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
               Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
               if (!optional.isEmpty()){
                   userDao.updateStatus(requestMap.get("status"), Integer.valueOf(requestMap.get("id")));
                   sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(),userDao.getAllAdmin());
                   return CafeUtils.getResponseEntity("User Status Updated Successfully ", HttpStatus.OK);
               } else {
                  return CafeUtils.getResponseEntity("User ID does not exist.", HttpStatus.NOT_FOUND);
               }
            }else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if (status != null && status.equalsIgnoreCase("true")){
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved", "User:-" + user + "\n is approved by \nADMIN:-" + jwtFilter.getCurrentUser(),allAdmin);
        }else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disable", "User:-" + user + "\n is disable by \nADMIN:-" + jwtFilter.getCurrentUser(),allAdmin);
        }
    }

}
