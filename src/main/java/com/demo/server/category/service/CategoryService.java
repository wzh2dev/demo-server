package com.demo.server.category.service;

import com.demo.server.category.domain.Category;
import com.demo.server.category.domain.CategoryStatus;
import com.demo.server.category.dto.CategoryCreateRequest;
import com.demo.server.category.exception.ConflictException;
import com.demo.server.category.exception.NotFoundException;
import com.demo.server.category.exception.ValidationException;
import com.demo.server.category.repository.CategoryRepository;

import java.util.List;
import java.util.Objects;

/**
 * 分类领域服务。
 */
public class CategoryService {

    private static final int NAME_MAX_LEN = 32;

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = Objects.requireNonNull(categoryRepository);
    }

    public Category create(CategoryCreateRequest req) {
        validateCreate(req);
        if (req.getParentId() != null) {
            Category parent = categoryRepository.findById(req.getParentId());
            if (parent == null) {
                throw new NotFoundException("category", req.getParentId());
            }
        }
        Category category = new Category();
        category.setParentId(req.getParentId());
        category.setName(req.getName());
        category.setIcon(req.getIcon());
        category.setSortOrder(req.getSortOrder());
        category.setStatus(req.getStatus() == null ? CategoryStatus.ACTIVE : req.getStatus());
        return categoryRepository.save(category);
    }

    public Category get(Long id) {
        return requireCategory(id);
    }

    /**
     * 获取分类树。
     */
    public List<Category> tree() {
        List<Category> roots = categoryRepository.findByParent(null);
        for (Category root : roots) {
            fillChildren(root);
        }
        return roots;
    }

    /**
     * 获取某分类的子分类。
     */
    public List<Category> children(Long parentId) {
        return categoryRepository.findByParent(parentId);
    }

    public List<Category> list(CategoryStatus status) {
        return categoryRepository.findAll(status);
    }

    /**
     * 更新分类（移动到新 parent、改名等）。
     */
    public Category update(Long id, CategoryCreateRequest req) {
        Category category = requireCategory(id);
        validateCreate(req);
        if (req.getParentId() != null) {
            if (req.getParentId().equals(id)) {
                throw new ValidationException("category cannot be its own parent");
            }
            Category parent = categoryRepository.findById(req.getParentId());
            if (parent == null) {
                throw new NotFoundException("category", req.getParentId());
            }
            // 检测环：如果 id 是 newParent 的祖先，则禁止
            if (isAncestor(id, req.getParentId())) {
                throw new ConflictException("cannot move category into its own descendant");
            }
        }
        category.setParentId(req.getParentId());
        category.setName(req.getName());
        category.setIcon(req.getIcon());
        category.setSortOrder(req.getSortOrder());
        if (req.getStatus() != null) {
            category.setStatus(req.getStatus());
        }
        return categoryRepository.save(category);
    }

    /**
     * 删除分类，若存在子分类则拒绝。
     */
    public void delete(Long id) {
        Category category = requireCategory(id);
        if (categoryRepository.countChildren(id) > 0) {
            throw new ConflictException("category has children: " + id);
        }
        if (!categoryRepository.deleteById(id)) {
            throw new NotFoundException("category", id);
        }
    }

    private Category requireCategory(Long id) {
        Category category = categoryRepository.findById(id);
        if (category == null) {
            throw new NotFoundException("category", id);
        }
        return category;
    }

    private void fillChildren(Category parent) {
        List<Category> children = categoryRepository.findByParent(parent.getId());
        parent.setChildren(children);
        for (Category child : children) {
            fillChildren(child);
        }
    }

    /**
     * 判断 ancestorId 是否是 candidateId 的祖先。
     */
    private boolean isAncestor(Long ancestorId, Long candidateId) {
        Long pid = candidateId;
        int guard = 0;
        while (pid != null && guard < 1000) {
            Category current = categoryRepository.findById(pid);
            if (current == null) {
                return false;
            }
            if (ancestorId.equals(current.getParentId())) {
                return true;
            }
            pid = current.getParentId();
            guard++;
        }
        return false;
    }

    private void validateCreate(CategoryCreateRequest req) {
        if (req == null) {
            throw new ValidationException("request is null");
        }
        if (req.getName() == null || req.getName().isEmpty()) {
            throw new ValidationException("name is empty");
        }
        if (req.getName().length() > NAME_MAX_LEN) {
            throw new ValidationException("name too long");
        }
    }
}
