package com.ucop.service;

import com.ucop.dao.ItemDAO;
import com.ucop.entity.Item;

import java.util.List;

public class ItemService {

    private final ItemDAO dao = new ItemDAO();

    public void save(Item item){
        dao.save(item);
    }

    public void update(Item item){
        dao.update(item);
    }

    public void delete(Item item){
        dao.delete(item);
    }

    public List<Item> findAll(){
        return dao.findAll();
    }

    public Item findById(int id){
        return dao.findById(id);
    }
}
