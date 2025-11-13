/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.sistemaCondominio.dal;
import java.sql.*;
/**
 * sistema-condominio
 * @author laris
 */
public class ModuloConexao {
    //metodo para estabelecer conexao com o banco
    public static Connection conector(){
        java.sql.Connection conexao = null;
        // chama o driver
        String driver = "org.postgresql.Driver";
        // armazenando informações referentes ao banco
        String url = "jdbc:postgresql://localhost:5432/sistema-condominio"; // conferir numero da porta
        String user = "postgres";
        String password = "12345";
        // estabelecendo conexao com o banco
        try {
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);
            return conexao;
        } catch (Exception e) {
            System.out.println(e); // mostra o erro
            return null;
        }
    }
}
