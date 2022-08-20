package com.company;

import Models.CustomFile;
import Postgresh.PostgresController;
import Postgresh.PostgresDB;

public class Main {

    public static void main(String[] args) {

        String DB_NAME = "quinbay";
//        PostgresDB postgresDB = PostgresDB.createInstance(DB_NAME);

        PostgresDB postgresDB = PostgresDB.createInstance();
        CustomFile[] files = {
                new CustomFile("txt","Employee_txt.txt"),
                new CustomFile("csv","Employee_csv.csv"),
                new CustomFile("json","Employee_json.json"),
        };

        Thread[] threadPool = new Thread[files.length];

        try{
            postgresDB.connectDB();
//            PostgresController postgresController = new PostgresController(postgresDB);
            PostgresController postgresController = new PostgresController();

            postgresController.createTable();



            for (int i=0 ;i<files.length;i++){
                Thread parser = threadPool[i] = postgresController.getParseThread(files[i].getType(),files[i].getFilename());
                parser.start();
            }
            for (int i = 0; i < files.length; i++) {
                threadPool[i].join();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        postgresDB.closeAllConnections();
    }
}
