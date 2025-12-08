package cn.harrison.sso.service.impl;

import cn.harrison.sso.entity.Permission;
import cn.harrison.sso.mapper.PermissionMapper;
import cn.harrison.sso.service.PermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public List<Permission> getPermissionTree(Permission permission) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        if (permission != null) {
            if (StringUtils.hasText(permission.getSimplifiedName())) {
                wrapper.like(Permission::getSimplifiedName, permission.getSimplifiedName());
            }
        }
        wrapper.orderByAsc(Permission::getSort);
        List<Permission> list = this.list(wrapper);

        // If search condition is present, return flat list (or handle tree filtering if needed)
        // For simplicity, if searching, we return the list. If not, we build the tree.
        if (permission != null && StringUtils.hasText(permission.getSimplifiedName())) {
            return list;
        }

        return buildTree(list);
    }

    private List<Permission> buildTree(List<Permission> permissions) {
        List<Permission> trees = new ArrayList<>();
        for (Permission permission : permissions) {
            if (permission.getParentId() == 0) {
                permission.setChildren(getChildren(permission.getId(), permissions));
                trees.add(permission);
            }
        }
        return trees;
    }

    private List<Permission> getChildren(Long parentId, List<Permission> permissions) {
        List<Permission> children = new ArrayList<>();
        for (Permission permission : permissions) {
            if (permission.getParentId() != null && permission.getParentId().equals(parentId)) {
                permission.setChildren(getChildren(permission.getId(), permissions));
                children.add(permission);
            }
        }
        return children;
    }
}
