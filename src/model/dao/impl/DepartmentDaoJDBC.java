package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoJDBC implements DepartmentDao {

    private Connection connection;

    public DepartmentDaoJDBC(Connection connection){
        this.connection = connection;
    }

    @Override
    public void insert(Department department) {
        PreparedStatement st = null;
        try{
            st = connection.prepareStatement("INSERT INTO department (Name) VALUE (?)",
                    Statement.RETURN_GENERATED_KEYS);

            st.setString(1, department.getName());

            int rowsAffected = st.executeUpdate();

            if(rowsAffected > 0){
                ResultSet rs = st.getGeneratedKeys();
                if(rs.next()){
                    int id = rs.getInt(1);
                    department.setId(id);
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
    public void update(Department department) {

    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
            st = connection.prepareStatement("SELECT * FROM department WHERE Id = ?");
            st.setInt(1, id);
            rs = st.executeQuery();
            if(rs.next()){
                return instatiateDepartment(rs);
            }
            return null;

        }catch (SQLException e){
            throw new DbException("Error message: " + e.getMessage());

        }finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    private Department instatiateDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getInt("Id"));
        department.setName(rs.getString("Name"));

        return department;
    }

    @Override
    public List<Department> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;

        try{
            st = connection.prepareStatement("SELECT * FROM department ORDER BY Id");
            rs = st.executeQuery();
            List<Department> list = new ArrayList<>();

            while(rs.next()){
                Department department = instatiateDepartment(rs);
                list.add(department);
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
