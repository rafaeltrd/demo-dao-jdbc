package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements SellerDao {

    private Connection connection;

    public SellerDaoJDBC(Connection connection){
        this.connection = connection;
    }

    @Override
    public void insert(Seller seller) {
        PreparedStatement st = null;
        try{
            st = connection.prepareStatement("""
                    INSERT INTO seller
                    (Name, Email, BirthDate, BaseSalary, DepartmentId)
                    VALUES
                    (?, ?, ?, ?, ?)""",
                    Statement.RETURN_GENERATED_KEYS);

            st.setString(1, seller.getName());
            st.setString(2, seller.getEmail()); //Correção getEmail
            st.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
            st.setDouble(4, seller.getBaseSalary());
            st.setInt(5, seller.getDepartment().getId());

            int rowsAffected = st.executeUpdate();

            if(rowsAffected > 0){
                ResultSet rs = st.getGeneratedKeys();
                if(rs.next()){
                    int id = rs.getInt(1);
                    seller.setId(id);
                }
                DB.closeResultSet(rs);
            }else {
                throw new DbException("Unexpected error! No rows affected!");
            }

        }catch (SQLException e){
            throw new DbException("Error message: " + e.getMessage());

        }finally {
            DB.closeStatement(st);
        }

    }

    @Override
    public void update(Seller seller) {
        PreparedStatement st = null;
        try{
            st = connection.prepareStatement("""
                    UPDATE seller
                    SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ?
                    WHERE Id = ?""",
                    Statement.RETURN_GENERATED_KEYS);

            st.setString(1, seller.getName());
            st.setString(2, seller.getEmail()); //Correção getEmail
            st.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
            st.setDouble(4, seller.getBaseSalary());
            st.setInt(5, seller.getDepartment().getId());
            st.setInt(6, seller.getId());

            st.executeUpdate();

        }catch (SQLException e){
            throw new DbException("Error message: " + e.getMessage());

        }finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try{
            st = connection.prepareStatement("DELETE FROM seller WHERE Id = ?");
            st.setInt(1, id);

            int affectedRows = st.executeUpdate();

            if(affectedRows == 0){
                throw new DbException("Error: ID not found");
            }

        }catch (SQLException e){
            throw new DbException("Error message: " + e.getMessage());
        }finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = connection.prepareStatement("""
                    SELECT seller.*,department.Name as DepName
                    FROM seller INNER JOIN department
                    ON seller.DepartmentId = department.Id
                    WHERE seller.Id = ?""");

            st.setInt(1, id);
            rs = st.executeQuery();
            if(rs.next()){
                Department department = instatiateDepartment(rs);

                return instatiateSeller(rs, department);
            }
            return null;

        }catch (SQLException e){
            throw new DbException("Error message: " + e.getMessage());

        }finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    private Seller instatiateSeller(ResultSet rs, Department department) throws SQLException {
        Seller seller = new Seller();
        seller.setId(rs.getInt("Id"));
        seller.setName(rs.getString("Name"));
        seller.setEmail(rs.getString("Email"));
        seller.setBaseSalary(rs.getDouble("BaseSalary"));
        seller.setBirthDate(rs.getDate("BirthDate"));
        seller.setDepartment(department);

        return seller;
    }

    private Department instatiateDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getInt("DepartmentId"));
        department.setName(rs.getString("DepName"));

        return department;
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = connection.prepareStatement("""
                    SELECT seller.*,department.Name as DepName
                    FROM seller INNER JOIN department
                    ON seller.DepartmentId = department.Id
                    ORDER BY Name""");

            rs = st.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()){

                Department department = map.get(rs.getInt("DepartmentId"));

                if (department == null){
                    department = instatiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), department);
                }

                Seller seller = instatiateSeller(rs, department);
                list.add(seller);
            }
            return list;

        }catch (SQLException e){
            throw new DbException("Error message: " + e.getMessage());

        }finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Seller> findByDepartment(Department dep) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = connection.prepareStatement("""
                    SELECT seller.*,department.Name as DepName
                    FROM seller INNER JOIN department
                    ON seller.DepartmentId = department.Id
                    WHERE DepartmentId = ?
                    ORDER BY Name""");

            st.setInt(1, dep.getId());
            rs = st.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()){

                Department department = map.get(rs.getInt("DepartmentId"));

                if (department == null){
                    department = instatiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), department);
                }

                Seller seller = instatiateSeller(rs, department);
                list.add(seller);
            }
            return list;

        }catch (SQLException e){
            throw new DbException("Error message: " + e.getMessage());

        }finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }
}