package com.ucop.service;

import com.ucop.dao.ProfileDAO;
import com.ucop.entity.AccountProfile;

public class ProfileService {

    private final ProfileDAO dao = new ProfileDAO();

    public AccountProfile findByUserId(int userId) {
        return dao.findByUserId(userId);
    }

    public void saveOrUpdate(AccountProfile profile) {
        dao.saveOrUpdate(profile);
    }
}
