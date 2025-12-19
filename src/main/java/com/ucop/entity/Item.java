package com.ucop.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "items")
public class Item extends BaseAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // ✅ FIX: bigint(20) -> Long

    @Column(nullable = false, unique = true, length = 255)
    private String sku;   // DB sku varchar(255)

    @Column(nullable = false, length = 200)
    private String name;  // DB name varchar(200)

    @Column(columnDefinition = "TEXT")
    private String description;

    // DB price decimal(38,2) -> để precision lớn cho khớp
    @Column(precision = 38, scale = 2)
    private BigDecimal price;

    @Column(length = 50)
    private String unit;

    // DB weight decimal(10,2) -> nên dùng BigDecimal cho chuẩn (nhưng Double vẫn chạy)
    // Nếu bạn muốn giữ Double thì giữ, còn chuẩn DB nên là BigDecimal:
    @Column(precision = 10, scale = 2)
    private BigDecimal weight;

    // DB stock int(11) NULL default 0 -> không cần nullable=false
    @Column(name = "stock")
    private Integer stock = 0;

    // DB status tinyint(4) NULL default 1
    @Column(name = "status")
    private Integer status = 1;

    // DB cột là image_url
    @Column(name = "image_url", length = 255)
    private String imagePath;

    // ---------- CATEGORY ----------
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    // ================= GETTERS / SETTERS =================

    public Long getId() {
        return id;
    }
    public void setId(Long id) {      // ✅ thêm setter để tiện
        this.id = id;
    }

    public String getSku() {
        return sku;
    }
    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getWeight() {
        return weight;
    }
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getStock() {
        return stock;
    }
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
}
