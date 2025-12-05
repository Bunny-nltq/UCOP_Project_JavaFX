package com.ucop.dao;

import com.ucop.entity.Item;

public class ItemDAO extends GenericDAO<Item> {
    public ItemDAO() {
        super(Item.class);
    }
}
