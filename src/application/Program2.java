package application;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.util.List;

public class Program2 { //Teste department
    public static void main(String[] args) {

        DepartmentDao departmentDao = DaoFactory.createDepartmentDao();

        System.out.println("==== TEST 1: department findById: ====");

        Department department = departmentDao.findById(2);

        System.out.println(department);

        System.out.println("\n==== TEST 2: department findAll: ====");

        List<Department> list = departmentDao.findAll();

        for(Department d : list){
            System.out.println(d);
        }

        System.out.println("\n==== TEST 3: department insert: ====");

        Department newDepartment = new Department(null, "Architecture");
        departmentDao.insert(newDepartment);
        System.out.println("Inserted id = " + newDepartment.getId());
    }
}
