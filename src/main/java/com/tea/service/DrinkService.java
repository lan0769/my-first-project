package com.tea.service;

import com.tea.dao.DrinkDao;
import com.tea.entity.Drink;

import java.math.BigDecimal;
import java.util.List;

public class DrinkService {
    private final DrinkDao drinkDao=new DrinkDao();
    private static final int STOCK_WARN_THRESHOLD=10;
    public List<Drink> listAllActive(){
        return drinkDao.listAllActive();
    }
    public Drink findById(Long id){
        return drinkDao.findById(id);
    }
    public boolean addDrink(String name, String category, BigDecimal price,Integer stock){
        if (name == null || name.isBlank()) throw new IllegalArgumentException("奶茶名不能为空");
        if (price == null || price.signum() <= 0) throw new IllegalArgumentException("价格必须 > 0");
        if (stock == null || stock < 0) throw new IllegalArgumentException("库存必须 ≥ 0");
        Drink d=new Drink(name.trim(),category,price,stock);
        return drinkDao.insert(d)==1;
    }
    public boolean updateDrink(Long id, BigDecimal price, Integer stock) {
        return drinkDao.updatePriceAndStock(id, price, stock) == 1;
    }

    public boolean deactivate(Long id) {
        return drinkDao.deactivate(id) == 1;
    }

    public List<Drink> listLowStock() {
        return drinkDao.listLowStock(STOCK_WARN_THRESHOLD);
    }
}
