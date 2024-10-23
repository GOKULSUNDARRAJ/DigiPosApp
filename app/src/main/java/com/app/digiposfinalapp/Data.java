package com.app.digiposfinalapp;

import java.util.ArrayList;
import java.util.List;

public class Data {

    public static List<Fruit> getFruitList() {
        List<Fruit> fruitList = new ArrayList<>();

        Fruit Avocado = new Fruit();
        Avocado.setName("Avocado");
        fruitList.add(Avocado);

        Fruit Banana = new Fruit();
        Banana.setName("Banana");
        fruitList.add(Banana);

        Fruit Coconut = new Fruit();
        Coconut.setName("Coconut");
        fruitList.add(Coconut);

        Fruit Guava = new Fruit();
        Guava.setName("Guava");
        fruitList.add(Guava);



        return fruitList;
    }

}