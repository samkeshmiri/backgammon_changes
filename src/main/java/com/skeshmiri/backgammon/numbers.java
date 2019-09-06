package com.skeshmiri.backgammon;

import java.util.Random;

public class numbers{
    public static void main(String[] args) {
        int countSix = 0;
        Random rnd = new Random();
        for (int i = 0; i < 100; i++){
            int dice1 = 1 + rnd.nextInt(5);
            int dice2 = 1 + rnd.nextInt(5);
            if(dice1 == 6 || dice2 == 6){
                countSix++;
            }
        }
        System.out.println(countSix);
    }
}