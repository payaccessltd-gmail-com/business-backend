package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.enums.Permission;
import com.jamub.payaccess.api.models.ErrorMessage;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.UserRolePermission;
import com.jamub.payaccess.api.models.request.CreateRolePrivilegeRequest;
import com.jamub.payaccess.api.models.request.TerminalOrderRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.BankService;
import com.jamub.payaccess.api.services.RoleService;
import com.jamub.payaccess.api.services.TokenService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/roles")
@Api(produces = "application/json", value = "Operations pertaining to Role Management")
public class RoleController {


    @Autowired
    TokenService tokenService;

    @Autowired
    RoleService roleService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());



    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_VIEW_ROLE')")
    @RequestMapping(value = "/get-role-list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "List Roles", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getRoleList(HttpServletRequest request,
                                        HttpServletResponse response) throws JsonProcessingException {



        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setMessage("Roles listed");
        payAccessResponse.setResponseObject(roleService.getRoles());
        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);


    }


    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_VIEW_PERMISSION')")
    @RequestMapping(value = "/get-permission-list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "List Permissions", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getPermissionList(HttpServletRequest request,
                                      HttpServletResponse response) throws JsonProcessingException {


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setMessage("Permissions listed");
        payAccessResponse.setResponseObject(roleService.getPrivileges());
        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);


    }




    @CrossOrigin
    //CREATE_ROLE_PERMISSION
    @PreAuthorize("hasRole('ROLE_CREATE_NEW_ACQUIRER')")
    @RequestMapping(value = "/create-role-permissions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Create Role Permission", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity createRolePermission(@RequestBody @Valid CreateRolePrivilegeRequest createRolePrivilegeRequest,
                                                BindingResult bindingResult,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws JsonProcessingException {


        if (bindingResult.hasErrors()) {
            List errorMessageList =  bindingResult.getFieldErrors().stream().map(fe -> {
                return new ErrorMessage(fe.getField(), fe.getDefaultMessage());
            }).collect(Collectors.toList());

            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setResponseObject(errorMessageList);
            payAccessResponse.setStatusCode(PayAccessStatusCode.VALIDATION_FAILED.label);
            payAccessResponse.setMessage("Request validation failed");
            return ResponseEntity.badRequest().body(payAccessResponse);
        }
        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        List<UserRolePermission> userRolePermissionList = roleService.createUserRolePermission(createRolePrivilegeRequest, authenticatedUser, ipAddress);

        if(userRolePermissionList!=null && !userRolePermissionList.isEmpty())
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(userRolePermissionList);
            payAccessResponse.setMessage("User Role has been mapped to the permissions successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setResponseObject(userRolePermissionList);
        payAccessResponse.setMessage("User Role was not mapped to the permissions successfully. Ensure the permissions have not been previously mapped to the role");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
    }


    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_VIEW_ROLE_PERMISSION')")
    @RequestMapping(value = "/get-user-role-permission-list/{rowCount}/{pageNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get List of Role Permissions", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getUserRolePermissionList(
            @PathVariable(required = true) Integer rowCount,
            @PathVariable(required = true) Integer pageNumber,
            @RequestParam(required = false) Optional<String> roleName,
            HttpServletRequest request,
            HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }

        List<UserRolePermission> userRolePermissionList = new ArrayList<UserRolePermission>();

        if(roleName.isPresent() && roleName.get()!=null)
            userRolePermissionList = roleService.getUserRolePermissionListByRoleName(pageNumber, rowCount, roleName.get());
        else
            userRolePermissionList = roleService.getUserRolePermissionList(pageNumber, rowCount);


        if(userRolePermissionList!=null && !userRolePermissionList.isEmpty())
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("User role permissions listed");
            payAccessResponse.setResponseObject(userRolePermissionList);
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("There are no User role permissions currently listed");
        payAccessResponse.setResponseObject(userRolePermissionList);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);



    }

}
