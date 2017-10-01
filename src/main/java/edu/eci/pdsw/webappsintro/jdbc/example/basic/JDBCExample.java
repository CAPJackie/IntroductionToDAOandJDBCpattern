/*
 * Copyright (C) 2015 hcadavid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.pdsw.webappsintro.jdbc.example.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class JDBCExample {
    
    public static void main(String args[]){
        try {
            String url="jdbc:mysql://desarrollo.is.escuelaing.edu.co:3306/bdprueba";
            String driver="com.mysql.jdbc.Driver";
            String user="bdprueba";
            String pwd="bdprueba";

            Class.forName(driver);

            Connection con=DriverManager.getConnection(url,user,pwd);
            con.setAutoCommit(false);
                 
            
            System.out.println("Valor total pedido 1:"+valorTotalPedido(con, 1));
            
            List<String> prodsPedido=nombresProductosPedido(con, 1);
            
            
            System.out.println("Productos del pedido 1:");
            System.out.println("-----------------------");
            for (String nomprod:prodsPedido){
                System.out.println(nomprod);
            }
            System.out.println("-----------------------");
            
            
            int suCodigoECI = 2110805;
            registrarNuevoProducto(con, suCodigoECI, "Juan David Ramirez Mendoza", 99999999);            
            con.commit();
                        
            
            con.close();
                                   
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    /**
     * Agregar un nuevo producto con los parámetros dados
     * @param con la conexión JDBC
     * @param codigo
     * @param nombre
     * @param precio
     * @throws SQLException 
     */
    public static void registrarNuevoProducto(Connection con, int codigo, String nombre,int precio) throws SQLException{
        //Crear preparedStatement
        //Asignar parámetros
        //usar 'execute'
        
        PreparedStatement preparedStatement = null;
        
        String insertNewProduct = "INSERT INTO ORD_PRODUCTOS" + 
                "(codigo, nombre, precio) VALUES" +
                "(?,?,?)";
        
        try{
            con.setAutoCommit(false);
            preparedStatement = con.prepareStatement(insertNewProduct);
            preparedStatement.setInt(1, codigo);
            preparedStatement.setString(2, nombre);
            preparedStatement.setInt(3, precio);
            preparedStatement.executeUpdate();
            con.commit();
        }catch(SQLException e){
            String selectProduct = "SELECT codigo, nombre, precio "
                                              + "FROM ORD_PRODUCTOS "
                                              + "WHERE codigo = ?";
                
                preparedStatement = con.prepareStatement(selectProduct);
                preparedStatement.setInt(1, codigo);
                ResultSet rs = preparedStatement.executeQuery();
                
                rs.next();
                
                System.out.println("Codigo "+rs.getInt("codigo")+"\nNombre: "+rs.getString("nombre")+"\nPrecio: "+rs.getInt("precio"));
        } finally{
            if(preparedStatement != null){
                preparedStatement.close();
            }
            
            if(con != null){
                preparedStatement.close();
            }
            
        
        }
        
    }
    
    /**
     * Consultar los nombres de los productos asociados a un pedido
     * @param con la conexión JDBC
     * @param codigoPedido el código del pedido
     * @return 
     */
    public static List<String> nombresProductosPedido(Connection con, int codigoPedido) throws SQLException{
        List<String> np=new LinkedList<>();
        
        
        //Crear prepared statement
        //asignar parámetros
        //usar executeQuery
        //Sacar resultados del ResultSet
        //Llenar la lista y retornarla
        
        PreparedStatement preparedStatement = null;
        
        String selectNameFromProducts = "SELECT nombre "
                                              + "FROM ORD_PRODUCTOS, ORD_DETALLES_PEDIDO, ORD_PEDIDOS "
                                              + "WHERE ORD_PEDIDOS.codigo = pedido_fk AND "
                                              + "producto_fk = ORD_PRODUCTOS.codigo "
                                              + "AND ORD_PEDIDOS.codigo = ?";
        
        try{
            con.setAutoCommit(false);
            preparedStatement = con.prepareStatement(selectNameFromProducts);
            preparedStatement.setInt(1, codigoPedido);
            ResultSet rs = preparedStatement.executeQuery();
            
            while(rs.next()){
                np.add(rs.getString("nombre"));
            }
            
            con.commit();
        }catch(SQLException e){
            e.printStackTrace();
        } finally{
            if(preparedStatement != null){
                preparedStatement.close();
            }
            
            if(con != null){
                preparedStatement.close();
            }
            
            con.setAutoCommit(true);
        }
        
        
        return np;
    }

    
    /**
     * Calcular el costo total de un pedido
     * @param con
     * @param codigoPedido código del pedido cuyo total se calculará
     * @return el costo total del pedido (suma de: cantidades*precios)
     */
    public static int valorTotalPedido(Connection con, int codigoPedido) throws SQLException{
        
        //Crear prepared statement
        //asignar parámetros
        //usar executeQuery
        //Sacar resultado del ResultSet
        
        int resultado = 0;
        PreparedStatement preparedStatement = null;
        
        String selectValorTotalPedido = "SELECT SUM(cantidad*precio) as valortotal "
                                              + "FROM ORD_PRODUCTOS, ORD_DETALLES_PEDIDO, ORD_PEDIDOS "
                                              + "WHERE ORD_PEDIDOS.codigo = pedido_fk AND "
                                              + "producto_fk = ORD_PRODUCTOS.codigo "
                                              + "AND ORD_PEDIDOS.codigo = ?";
        
        try{
            con.setAutoCommit(false);
            preparedStatement = con.prepareStatement(selectValorTotalPedido);
            preparedStatement.setInt(1, codigoPedido);
            
            ResultSet rs = preparedStatement.executeQuery();
            
            rs.next();            
                  
            resultado = rs.getInt("valortotal");

            
            con.commit();
        }catch(SQLException e){
            e.printStackTrace();
        } finally{
            if(preparedStatement != null){
                preparedStatement.close();
            }
            
            if(con != null){
                preparedStatement.close();
            }
            
            con.setAutoCommit(true);
        }
        
        
        return resultado;
    }
    

    
    
    
}
