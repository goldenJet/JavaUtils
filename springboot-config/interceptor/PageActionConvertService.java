package com.wailian.page;

import com.wailian.action.Action;
import com.wailian.action.ActionConfig;
import com.wailian.action.ActionModel;
import com.wailian.action.QueryCondition;
import com.wailian.entity.Role;
import com.wailian.entity.RoleMenuAuth;
import com.wailian.entity.StaffProfile;
import com.wailian.repository.RoleMenuAuthRepository;
import com.wailian.service.ActionService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by jet.chen on 2017/2/27.
 */
@Service
@Log4j
public class PageActionConvertService {

    @Autowired
    ActionService actionService;

    @Autowired
    RoleMenuAuthRepository roleMenuAuthRepository;


    public AccountInfo getAccountInfo(HttpServletRequest request) {
        AccountInfo accountInfo = new AccountInfo();

        if (SecurityContextHolder.getContext() != null &&
                SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null
                ) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof StaffProfile) {
                StaffProfile staffProfile = (StaffProfile) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (staffProfile != null) {
                    accountInfo.setFullName(staffProfile.getName());
                    accountInfo.setId(staffProfile.getId());
                    accountInfo.setDepartmentId(staffProfile.getDepartment().getId());
                    accountInfo.setDepartmentName(staffProfile.getDepartment().getName());
                    accountInfo.setNewUser(staffProfile.getNewUser());
                    String roleName = "";
                    Map<Integer, RoleMenuAuth> roleMenuAuthMap = new HashMap<>();
                    //为用户角色构建菜单权限，如果用户任意角色（现为一个角色）拥有CRUD权限，则该用户就拥有CRUD权限
                    List<Role> roles = staffProfile.getRoles();
                    if (roles != null && roles.size() > 0) {
                        Role role = roles.get(0);
                        roleName = role.getName();
                        List<RoleMenuAuth> roleMenuAuths = roleMenuAuthRepository.findByRoleId(role.getId());
                        for (RoleMenuAuth r : roleMenuAuths) {
                            int parentMenuId = r.getMenu().getParentId();
                            int menuId = r.getMenu().getId();
                            if (roleMenuAuthMap.containsKey(menuId)) {
                                RoleMenuAuth rma = roleMenuAuthMap.get(menuId);
                                rma.setAllowDelete(getAuthIntegerBy(rma.getAllowDelete(), r.getAllowDelete()));
                                rma.setAllowInsert(getAuthIntegerBy(rma.getAllowInsert(), r.getAllowInsert()));
                                rma.setAllowView(getAuthIntegerBy(rma.getAllowView(), r.getAllowView()));
                                rma.setAllowUpdate(getAuthIntegerBy(rma.getAllowUpdate(), r.getAllowUpdate()));
                            } else {
                                RoleMenuAuth menuAuth = new RoleMenuAuth();
                                menuAuth.setAllowDelete(r.getAllowDelete());
                                menuAuth.setAllowInsert(r.getAllowInsert());
                                menuAuth.setAllowView(r.getAllowView());
                                menuAuth.setAllowUpdate(r.getAllowUpdate());
                                roleMenuAuthMap.put(menuId, menuAuth);
                            }
                            RoleMenuAuth roleMenuAuth = roleMenuAuthMap.get(menuId);
                            if(showParentMenu(roleMenuAuth) && !roleMenuAuthMap.containsKey(parentMenuId)){
                                RoleMenuAuth parentRoleMenuAuth = new RoleMenuAuth();
                                parentRoleMenuAuth.setAllowView(1);
                                parentRoleMenuAuth.setAllowDelete(1);
                                parentRoleMenuAuth.setAllowInsert(1);
                                parentRoleMenuAuth.setAllowUpdate(1);
                                roleMenuAuthMap.put(parentMenuId,parentRoleMenuAuth);
                            }else if(!showParentMenu(roleMenuAuth) && roleMenuAuthMap.containsKey(parentMenuId)){
                                roleMenuAuthMap.remove(parentMenuId);
                            }
                        }
                    }
                    accountInfo.setRoleMenuAuthMap(roleMenuAuthMap);
                    accountInfo.setTitle(roleName);
                }
            }
        }
        return accountInfo;
    }

    private Integer getAuthIntegerBy(Integer one, Integer two) {
        if (one != null && two != null && (one == 1 || two == 1)) {
            return 1;
        }
        return 0;
    }

    private boolean showParentMenu(RoleMenuAuth rma) {
        if ((rma.getAllowDelete() != null && rma.getAllowDelete() != 0) ||
                (rma.getAllowInsert() != null && rma.getAllowInsert() != 0) ||
                (rma.getAllowUpdate() != null && rma.getAllowUpdate() != 0) ||
                (rma.getAllowView() != null && rma.getAllowView() != 0)) {
            return true;
        }
        return false;
    }


    public List<MenuItem> getMainMenuSideBar(Locale locale) throws IllegalAccessException {
        List<MenuItem> menuItemList = new ArrayList<>();
        List<ActionModel> actionModelList = actionService.searchActionModelByCondition(new QueryCondition("name", QueryCondition.Condition.EQUAL, "main_sidebar_menus"));
        if (actionModelList.size() == 1) {
            getMenuItemByActionModel(actionModelList.get(0), locale, menuItemList);
        }
        return menuItemList;
    }


    public PageHeader genericPageHeader(HttpServletRequest servletRequest) throws IllegalAccessException {
        PageHeader pageHeader = new PageHeader();
        List<Breadcrumb> breadcrumbList = new ArrayList<>();
        String parentId = servletRequest.getParameter("parentId");
        List<ActionModel> actionModels = actionService.searchActionModelByCondition(new QueryCondition("name", QueryCondition.Condition.EQUAL, parentId));
        if (actionModels.size() == 1) {
            ActionModel actionModel = actionModels.get(0);
            try {
                ResourceInfo resourceInfo = getResourceInfoByAction(null, actionModel.getName(), actionModel.getResourceBundle(), servletRequest.getLocale());
                Breadcrumb breadcrumb = new Breadcrumb(resourceInfo, null);
                breadcrumbList.add(breadcrumb);
            } catch (Exception e) {
                log.error("resourceInfo can't found:" + actionModel);
            }
        }
        String uri = servletRequest.getRequestURI();
        uri = uri.substring(1, uri.length());
        String actionName = uri;
        String actionType = null;
        if (uri.contains("/")) {
            actionType = uri.split("/")[0];
            actionName = uri.split("/")[1];
        } else {
            // Default url to home page
            actionType = "home";
            actionName = "home";
        }
        Action action = actionService.searchActionByNameAndType(actionName, actionType);
        if (action != null) {
            ResourceInfo resourceInfo = getResourceInfoByAction(action.getType(), action.getName(), action.getResourceBundle(), servletRequest.getLocale());
            pageHeader.setTitle(resourceInfo.getName());
            pageHeader.setDescription(resourceInfo.getDescription());
            Breadcrumb breadcrumb = new Breadcrumb(resourceInfo, action.getUrl());
            breadcrumbList.add(breadcrumb);
        }
        pageHeader.setBreadcrumbList(breadcrumbList);
        return pageHeader;
    }


    private void getMenuItemByActionModel(ActionModel actionModel, Locale locale, List<MenuItem> menuItemList) {
        List<ActionConfig> actionConfigList = actionModel.getActionConfigList();
        for (ActionConfig actionConfig : actionConfigList) {
            MenuItem menuItem = new MenuItem();
            if (actionConfig instanceof Action) {
                Action action = (Action) actionConfig;
                action = actionService.searchActionByNameAndType(action.getName(), action.getType());
                menuItem.setId(action.getType() + "::" + action.getName());

                ResourceInfo resourceInfo = getResourceInfoByAction(action.getType(), action.getName(), action.getResourceBundle(), locale);
                menuItem.setResourceInfo(resourceInfo);
                if (resourceInfo != null) {
                    menuItemList.add(menuItem);
                }
            } else if (actionConfig instanceof ActionModel) {
                ActionModel subActionMode = actionService.searchActionModelByName(actionConfig.getName());
                subActionMode.setResourceBundle(((ActionModel) actionConfig).getResourceBundle());
                menuItem.setId(subActionMode.getName());
                ResourceInfo resourceInfo = getResourceInfoByAction(null, subActionMode.getName(), subActionMode.getResourceBundle(), locale);
                menuItem.setResourceInfo(resourceInfo);
                getMenuItemByActionModel(subActionMode, locale, menuItem.getChild());
                if (resourceInfo != null) {
                    menuItemList.add(menuItem);
                }
            }
        }
    }


    private ResourceInfo getResourceInfoByAction(String type, String name, String resourceBundle, Locale locale) {
        ResourceInfo resourceInfo = null;
        if (resourceBundle.contains("/")) {
            resourceBundle = resourceBundle.replace("/", ".");
        }
        if (resourceBundle.contains("\\")) {
            resourceBundle = resourceBundle.replace("\\", ".");
        }
        try {
            resourceInfo = new ResourceInfo(type, name, ResourceBundle.getBundle(resourceBundle, locale));
        } catch (MissingResourceException e) {
            log.error(type + "." + name + ": can't found resourceBundle:" + resourceBundle);
        }
        return resourceInfo;
    }


}
