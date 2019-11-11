package com.example.demo;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@RestController
public class DemoController {

    @GetMapping(value = "/hello")
    public String demo() {
        return "Hello, World!";
    }

    @GetMapping(value = "/hi")
    public String demo2() {
        return "Hi, World!";
    }

    @GetMapping(value = "/testing{ti}")
    public String demo3(@RequestParam("ti") String ti){
        String output = "";
        Connection conn = null;
        String url = "jdbc:postgresql://ec2-174-129-253-169.compute-1.amazonaws.com/d9vsaknll1319";
        String user = "lfoagdwpzckmuq";
        String password = "7cf9b7a5b57780ee7f45c96cac75808dd2cc2ba77b123cf0948cfb290ad1d93c";
        try{
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("title = "+ti);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM book WHERE TITLE = "+ti;
            //String sql = "SELECT * FROM book";
            ResultSet rs = stmt.executeQuery(sql);
            int colCount = rs.getMetaData().getColumnCount();
            System.out.println("取得したカラム数:" + colCount);

            while(rs.next()){
                /* 行からデータを取得 */
                output += rs.getString("TITLE")+": "+rs.getInt("PRICE")+"yen";
                System.out.println(rs.getString("TITLE"));
                System.out.print(rs.getInt("PRICE"));
            }

        }catch(SQLException e){
            output = "Error:"+e;
        }
        return output;
    }
}