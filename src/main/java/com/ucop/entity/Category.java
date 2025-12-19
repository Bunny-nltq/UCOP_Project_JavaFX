package com.ucop.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category extends BaseAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer status = 1;

    // ---------- Category cha ----------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    // ---------- Category con ----------
    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Category> children = new ArrayList<>();

    // ================= GETTERS / SETTERS =================
    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getStatus() { return status; }
    public Category getParent() { return parent; }
    public List<Category> getChildren() { return children; }

    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(Integer status) { this.status = status; }
    public void setParent(Category parent) { this.parent = parent; }
    public void setChildren(List<Category> children) { this.children = children; }

    // ================= SAFE METHODS =================

    /** Add child category safely */
    public void addChild(Category child) {
        children.add(child);
        child.setParent(this);
    }

    /** Remove child category safely */
    public void removeChild(Category child) {
        children.remove(child);
        child.setParent(null);
    }

    // ================= SAFE toString =================
    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "'}";
    }
}
