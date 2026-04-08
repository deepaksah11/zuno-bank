package com.zunoBank.Transactions.service;

import com.zunoBank.Transactions.entity.type.SpendingCategory;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    public SpendingCategory autoTag(String description) {
        if (description == null){
            return SpendingCategory.OTHERS;
        }
        String d = description.toLowerCase();
        if (d.contains("zomato") || d.contains("swiggy"))
            return SpendingCategory.FOOD;
        if (d.contains("uber") || d.contains("ola"))
            return SpendingCategory.TRAVEL;
        if (d.contains("electricity") || d.contains("gas"))
            return SpendingCategory.UTILITIES;
        if (d.contains("amazon") || d.contains("flipkart"))
            return SpendingCategory.SHOPPING;
        if (d.contains("hospital") || d.contains("pharmacy"))
            return SpendingCategory.HEALTH;
        if (d.contains("school") || d.contains("college"))
            return SpendingCategory.EDUCATION;
        return SpendingCategory.OTHERS;
    }
}
