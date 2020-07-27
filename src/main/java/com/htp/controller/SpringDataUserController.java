package com.htp.controller;

import com.htp.controller.request.UserCreateRequest;
import com.htp.controller.request.UserUpdateRequest;
import com.htp.dao.springdata.UserRepository;
import com.htp.domain.Roles;
import com.htp.domain.hibernate.HibernateRole;
import com.htp.domain.hibernate.HibernateUser;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/springdata/users")
@Transactional
public class SpringDataUserController {

    private UserRepository userRepository;

    public SpringDataUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ApiOperation(value = "Finding all users")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successful loading users"),
            @ApiResponse(code = 500, message = "Server error, something wrong")
    })
    @GetMapping
    public ResponseEntity<List<HibernateUser>> findAll() {
        return new ResponseEntity<>(userRepository.findUsersWithAdminRoles(), HttpStatus.OK);
    }

    @ApiOperation(value = "Search with pagination")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successful loading users"),
            @ApiResponse(code = 500, message = "Server error, something wrong")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "Page number", example = "0", defaultValue = "0", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "Items per page", example = "3", defaultValue = "3", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "Field to sort", example = "0", defaultValue = "id", dataType = "string", paramType = "query")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<HibernateUser>> searchWithPagination(@ApiIgnore Pageable pageable) {
        Page<HibernateUser> usersPage = userRepository.findAll(pageable);
        return new ResponseEntity<>(usersPage, HttpStatus.OK);
    }

    @ApiOperation(value = "Test caches")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successful loading users"),
            @ApiResponse(code = 500, message = "Server error, something wrong")
    })
    @GetMapping("/testCache")
    public ResponseEntity<String> testCaches() {
        return new ResponseEntity<>("so far so good!", HttpStatus.OK);
    }

    @ApiOperation(value = "Finding user by id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successful loading user"),
            @ApiResponse(code = 500, message = "Server error, something wrong")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "User database id", example = "7", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping("/{id}")
    public ResponseEntity<HibernateUser> getUserById(@PathVariable Long id) {
        return new ResponseEntity<>(userRepository.findById(id).get(), HttpStatus.OK);
    }

    @ApiOperation(value = "Create user")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successful creation user"),
            @ApiResponse(code = 422, message = "Failed user creation properties validation"),
            @ApiResponse(code = 500, message = "Server error, something wrong")
    })
    @PostMapping
    public HibernateUser create(@Valid @RequestBody UserCreateRequest createRequest) {

        HibernateUser user = new HibernateUser();
        user.setFirstName(createRequest.getFirstName());
        user.setLastName(createRequest.getLastName());
        user.setPhoneNumber(createRequest.getPhoneNumber());
        user.setPassportData(createRequest.getPassportData());
        user.setLogin(createRequest.getLogin());
        user.setPassword(createRequest.getPassword());
        user.setCreated(new Timestamp(new Date().getTime()));
        user.setChanged(new Timestamp(new Date().getTime()));

        HibernateRole hibernateRole = new HibernateRole();
        hibernateRole.setRoleName(Roles.ROLE_USER.name());
        hibernateRole.setUser(user);
        user.setRoles(Collections.singleton(hibernateRole));

        return userRepository.save(user);
    }

    @ApiOperation(value = "Update user")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successful creation user"),
            @ApiResponse(code = 422, message = "Failed user creation properties validation"),
            @ApiResponse(code = 500, message = "Server error, something wrong")
    })
    @PutMapping
    public HibernateUser update(@Valid @RequestBody UserUpdateRequest updateRequest) {

        HibernateUser user = new HibernateUser();
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setPhoneNumber(updateRequest.getPhoneNumber());
        user.setLogin(updateRequest.getLogin());
        user.setCreated(new Timestamp(new Date().getTime()));
        user.setChanged(new Timestamp(new Date().getTime()));

        userRepository.updateUser(user.getFirstName(), user.getLastName(), user.getPhoneNumber(), user.getLogin());
        return user;
    }

//    @ApiOperation(value = "Delete user")
//    @ApiResponses({
//            @ApiResponse(code = 201, message = "Successful delete user"),
//            @ApiResponse(code = 500, message = "Server error, something wrong"),
//            @ApiResponse(code = 502, message = "Wrong user id")
//    })
//    @PutMapping("/{id}")
//    public HibernateUser deleteUser(@PathVariable Long id) {
//        HibernateUser user = new HibernateUser();
//        userRepository.deleteUser(user.getId());
//        return user;
//    }

}