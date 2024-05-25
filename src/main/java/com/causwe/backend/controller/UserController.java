package com.causwe.backend.controller;

import com.causwe.backend.exceptions.UnauthorizedException;
import com.causwe.backend.exceptions.UserNotFoundException;
import com.causwe.backend.dto.UserDTO;
import com.causwe.backend.model.User;
import com.causwe.backend.security.JwtTokenProvider;
import com.causwe.backend.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userData, @CookieValue(name = "jwt", required = false) String token) {
        if (Objects.equals(userData.getUsername(), "") || Objects.equals(userData.getPassword(), "")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Long memberId = jwtTokenProvider.getUserIdFromToken(token);
            User currentUser = userService.getUserById(memberId);
            if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
                throw new UnauthorizedException("Only admins can create users.");
            }

            User newUser = userService.createUser(modelMapper.map(userData, User.class));
            UserDTO newUserDTO = modelMapper.map(newUser, UserDTO.class);
            return new ResponseEntity<>(newUserDTO, HttpStatus.CREATED);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody UserDTO userData, HttpServletResponse response) {
        User user = userService.login(modelMapper.map(userData, User.class));
        if (user != null) {
            String token = jwtTokenProvider.generateToken((Long) user.getId());
            Cookie cookie = new Cookie("jwt", token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            String cookieHeader = String.format("%s=%s; Path=%s; SameSite=None; Secure", cookie.getName(), cookie.getValue(), cookie.getPath());
            response.addHeader("Set-Cookie", cookieHeader);

            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("")
    public ResponseEntity<UserDTO> getUserById(@CookieValue(name = "jwt", required = false) String token) {
        try {
            Long memberId = jwtTokenProvider.getUserIdFromToken(token);
            User user = userService.getUserById(memberId);
            UserDTO UserDTO =  modelMapper.map(user, UserDTO.class);
            return new ResponseEntity<>(UserDTO, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/devs")
    public ResponseEntity<List<UserDTO>> getAllDevs() {
        List<User> devUsers = userService.getAllDevs();

        List<UserDTO> devUserDTOs = devUsers
        .stream()
        .map(user -> modelMapper.map(user, UserDTO.class))
        .collect(Collectors.toList());


        return new ResponseEntity<>(devUserDTOs, HttpStatus.OK);
    }

    /*
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers(@CookieValue(name = "jwt", required = false) String token) {
        try {
            Long memberId = jwtTokenProvider.getUserIdFromToken(token);
            User currentUser = userService.getUserById(memberId);
            if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
                throw new UnauthorizedException("Only admins can view all users.");
            }

            List<User> users = userService.getAllUsers();
            List<UserDTO> userDTOs = users.stream()
                    .map(user -> modelMapper.map(user, UserDTO.class))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userDTOs, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@CookieValue(name = "jwt", required = false) String token,
                                           @PathVariable Long userId) {
        try {
            Long memberId = jwtTokenProvider.getUserIdFromToken(token);
            User currentUser = userService.getUserById(memberId);
            if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
                throw new UnauthorizedException("Only admins can delete users.");
            }

            userService.deleteUser(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
    */

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        // Invalidate the cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("jwt"))
                    .forEach(cookie -> {
                        cookie.setMaxAge(0);
                        cookie.setPath("/"); // Ensure cookie is deleted from the correct path
                        response.addCookie(cookie);
                    });
        }
        return ResponseEntity.ok().body("Logged out successfully.");
    }
}
