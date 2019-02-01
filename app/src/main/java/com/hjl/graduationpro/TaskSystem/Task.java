package com.hjl.graduationpro.TaskSystem;


import com.hjl.graduationpro.Factory.GameObjectFactory;

public class Task {

    private volatile static Task task;
    private long grade = 0; //总成绩
    private int rank = 0;  //
    private int bigSpeed;
    private int midSpeed;
    private int smlSpeed;

    private Task(){
        bigSpeed = 8;
        midSpeed = 12;
        smlSpeed = 16;

    }

    public static Task getInstance(){

        if (task == null){
            synchronized (GameObjectFactory.class){
                if (task == null){
                    task = new Task();
                }
            }
        }

        return task;
    }

    public void addScore(int score){
        grade += score;
    }

    private void computeRank() {
        if (grade <= 100){
            rank = 1;
        }else if (grade <= 300){
            rank = 2;
        }else if (grade <= 500){
            rank = 3;
        }else if (grade <= 700){
            rank = 4;
        }else if (grade <= 1000){
            rank = 5;
        }else if (grade <= 1500){
            rank = 6;
        }else if (grade <= 1800){
            rank = 7;
        }else if (grade <= 2300){
            rank = 8;
        }else if (grade <= 3000){
            rank = 9;
        }else if (grade <= 4000){
            rank = 10;
        }else {
            //
        }
    }


    public long getGrade() {
        return grade;
    }

    public int getRank() {
        computeRank();
        return rank;
    }

    public void reStartAll(){
        grade = 0;
        rank = 1;
    }

    public int getBigSpeed() {
        computeRank();
        return bigSpeed + rank*2;
    }

    public int getMidSpeed() {
        computeRank();
        return midSpeed + rank*3;
    }

    public int getSmlSpeed() {
        computeRank();
        return smlSpeed + rank*4;
    }

}
