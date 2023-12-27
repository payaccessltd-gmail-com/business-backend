package com.jamub.payaccess.api.services;

import com.jamub.payaccess.api.dao.RoleDao;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.Permission;
import com.jamub.payaccess.api.enums.UserRole;
import com.jamub.payaccess.api.models.Bank;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.UserRolePermission;
import com.jamub.payaccess.api.models.request.CreateRolePrivilegeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class RoleService {


    @Autowired
    private RoleDao roleDao;

    private Logger log = LoggerFactory.getLogger(this.getClass());




    @Autowired
    public RoleService(RoleDao roleDao){
        this.roleDao = roleDao;
    }


    public List getRoles() {
        return Arrays.stream(UserRole.values()).collect(Collectors.toList());
    }

    public Map getPrivileges() {

        Map<String, String> mp = Arrays.stream(Permission.values()).collect(Collectors.toMap(Permission::name, Permission::getValue));
//        return Arrays.stream(Permission.values()).collect(Collectors.toList());
        return mp;
    }

    public List<UserRolePermission> createUserRolePermission(CreateRolePrivilegeRequest createRolePrivilegeRequest, User authenticatedUser, String ipAddress) {
        String userRole = createRolePrivilegeRequest.getUserRole();
        List<String> permissionList = createRolePrivilegeRequest.getPermission();

        try
        {
            UserRole.valueOf(userRole);
            List<UserRolePermission> list = permissionList.stream().map(p -> {
                String description = "Map Permission to Role - " + userRole + " to " + p;
                log.info("" + userRole + " ... " + p);
//                UserRolePermission urp = roleDao.getUserRolePermissionByRoleAndPermission(userRole, p);
//                if(urp==null)
//                {

                try {
                    return roleDao.createUserRolePermission(userRole, p,
                            authenticatedUser.getId(), ipAddress, description,
                            ApplicationAction.MAP_PERMISSION_TO_ROLE, authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                            UserRolePermission.class.getCanonicalName());
                }
                catch(DataIntegrityViolationException e)
                {
                    return null;
                }
//                }
//                return null;

            }).filter(t -> {
                return t!=null;
            }).collect(Collectors.toList());

            return list;
        }
        catch(IllegalArgumentException iae)
        {
            iae.printStackTrace();
        }

        return null;
    }

    public List<UserRolePermission> getUserRolePermissionList(Integer pageNumber, Integer rowCount) {
        return roleDao.getUserRolePermissionList(pageNumber, rowCount);
    }

    public List<UserRolePermission> getUserRolePermissionListByRoleName(Integer pageNumber, Integer rowCount, String roleName) {
        return roleDao.getPermissionsByRole(pageNumber, rowCount, roleName);
    }
}
