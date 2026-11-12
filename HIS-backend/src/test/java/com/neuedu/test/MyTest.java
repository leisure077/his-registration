package com.neuedu.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyTest {
    public static void main(String[] args) {
        try {
            // 加载驱动类
            Class.forName("org.postgresql.Driver");
            // 建立连接
            Connection con = DriverManager.getConnection("jdbc:postgresql://192.168.2.151:5432/db1", "postgres", "123456789");
            // 打开一个命令行 写语句
            PreparedStatement pstmt = con.prepareStatement("insert into sys_dept(dept_name, dept_code, dept_type) values(?, ?, ?)");
            // 将真实值传入
            pstmt.setString(1, "脑外科");
            pstmt.setString(2, "NAOWAIKE");
            pstmt.setString(3, "1");
            int i = pstmt.executeUpdate();
            System.out.println(i);
            pstmt.close();
            con.close();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
