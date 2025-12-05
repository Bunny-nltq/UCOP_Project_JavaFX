package com.ucop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    // GETTER & SETTER
    public Integer getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
