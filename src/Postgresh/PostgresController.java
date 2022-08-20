package Postgresh;

import CustomExceptions.EmptyFileException;
import CustomExceptions.FileTypeNotMatchedException;
import CustomExceptions.InvalidDataTypeException;
import CustomExceptions.UnknownFileTypeException;

import DataParser.DataParserFactory;
import DataParser.DataParser;

import Models.Employee;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class PostgresController {
    private PostgresDB postgresDB;

    public PostgresController(){
        this.postgresDB = PostgresDB.createInstance();
    }

    public PostgresController(PostgresDB postgresDB) {
        this.postgresDB = postgresDB;
    }

    public void createTable() throws SQLException {
        String query = "drop table if exists employee;" +
                "create table employee(id serial primary key,name varchar(30),designation varchar(30),salary float ,experience int,fileType varchar(30));";
        System.out.println("Table Creating " + postgresDB.getStatement().execute(query));
    }


    public void prepareSQLBunchInsert(ArrayList<Employee> employees){
        PreparedStatement preparedStatement =null;
        try {
            if(employees == null) throw new InvalidDataTypeException("Can't Insert corrected data !");

            String query = "insert into employee (name,designation,salary,experience,fileType) values(?,?,?,?,?)";
            preparedStatement = postgresDB.getConnection().prepareStatement(query);

            for(Employee employee:employees){
                preparedStatement.setString(1,employee.getName());
                preparedStatement.setString(2,employee.getDesignation());
                preparedStatement.setFloat(3,employee.getSalary());
                preparedStatement.setInt(4,employee.getExperience());
                preparedStatement.setString(5,employee.getFileType());
                preparedStatement.addBatch();
            }

            int[] updateCounts = preparedStatement.executeBatch();
            System.out.println(updateCounts.length);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public  Thread getParseThread(String type,String fileName)
            throws UnknownFileTypeException, FileTypeNotMatchedException,FileNotFoundException, EmptyFileException {

        String basePath = System.getProperty("user.dir")+"/";

        DataParserFactory dataParserFactory = new DataParserFactory();
        DataParser parser = dataParserFactory.getParserObject(type);

        if (parser == null){
            throw new UnknownFileTypeException("Unknown File Type !");
        }

        String fileExtention = fileName.substring(fileName.lastIndexOf('.')+1);
        if (!type.equals(fileExtention)){
            throw new FileTypeNotMatchedException("File Type Not Matched !");
        }


        File file = getFileByName(basePath + fileName);
        parser.setFile(file);

        return new Thread(parser){
            @Override
            public void run() {
                super.run();
                prepareSQLBunchInsert(parser.parseData());
            }
        };
    }

    private File getFileByName(String path) throws FileNotFoundException,EmptyFileException {

        File file = new File(path);
        Scanner fileContent = new Scanner(new FileReader(file));

        if(!fileContent.hasNextLine()){
            throw new EmptyFileException("File is Empty!");
        }

        return file;
    }

}
