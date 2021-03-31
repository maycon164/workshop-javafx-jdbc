package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	private Connection conn;

	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller seller) {
		PreparedStatement st = null;
		try {
			String sql = "Insert into seller (Name, Email,  BirthDate, BaseSalary, DepartmentId)"
					+ " Values (?, ?, ?, ?, ?)";
			st = conn.prepareStatement(sql + " ", Statement.RETURN_GENERATED_KEYS);
			st.setString(1, seller.getName());
			st.setString(2, seller.getEmail());
			st.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			st.setDouble(4, seller.getBaseSalary());
			st.setInt(5, seller.getDep().getId());

			int linha = st.executeUpdate();

			if (linha > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					seller.setId(id);
				}
				DB.closeResultSet(rs);
				System.out.println("Pronto! linhas afetadas " + linha + " linhas");
			} else {
				throw new DbException("Error I dont know: no rows affected");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void update(Seller seller) {
		PreparedStatement st = null;
		try {
			String sql = "Update seller Set Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ?"
					+ " Where Id = ?";
			st = conn.prepareStatement(sql);
			st.setString(1, seller.getName());
			st.setString(2, seller.getEmail());
			st.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			st.setDouble(4, seller.getBaseSalary());
			st.setInt(5, seller.getDep().getId());
			st.setInt(6, seller.getId());
			
			int linha = st.executeUpdate();
			if(linha > 0) 
				System.out.println("Linhas afetadas " + linha + " linhas");
			else
				throw new DbException("erro nenhuma linha afetada");
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void deleteById(int id) {
		PreparedStatement st = null;
		try {
			String sql = "Delete from seller where Id = ?";
			st = conn.prepareStatement(sql);
			st.setInt(1, id);
			int linha = st.executeUpdate();
			if (linha > 0)
				System.out.println("Deleted! " + linha + " linhas afetadas");
			else
				throw new DbException("delete error");
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public Seller findById(int id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {

			String sql = "Select seller.*, department.Name as depName" + " from seller Inner join department "
					+ " on seller.DepartmentId = department.Id" + " where seller.Id = ?";

			st = conn.prepareStatement(sql);
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Department dep = instantiateDepartment(rs);

				Seller seller = instantiateSeller(rs, dep);

				return seller;
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
		return null;
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller seller = new Seller();
		seller.setId(rs.getInt("Id"));
		seller.setName(rs.getString("Name"));
		seller.setBaseSalary(rs.getDouble("BaseSalary"));
		seller.setEmail(rs.getString("Email"));
		seller.setBirthDate(new java.util.Date(rs.getTimestamp("BirthDate").getTime()));
		seller.setDep(dep);
		return seller;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("depName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {

			String sql = "Select seller.*, department.Name as depName" + " from seller Inner join department"
					+ " on seller.DepartmentId = department.Id" + " Order by Name";

			st = conn.prepareStatement(sql);
			rs = st.executeQuery();

			List<Seller> lista = new ArrayList<Seller>();
			Map<Integer, Department> aux = new HashMap<Integer, Department>();
			while (rs.next()) {

				Department obj = aux.get(rs.getInt("DepartmentId"));
				if (obj == null) {
					obj = instantiateDepartment(rs);
					aux.put(rs.getInt("DepartmentId"), obj);
				}

				Seller seller = instantiateSeller(rs, obj);
				lista.add(seller);
			}
			return lista;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}

	}

	@Override
	public List<Seller> findByDepartment(Department dep) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {

			String sql = "Select seller.*, department.Name as depName" + " from seller Inner join department"
					+ " on seller.DepartmentId = department.Id" + " where DepartmentId = ?" + " Order by Name";

			st = conn.prepareStatement(sql);
			st.setInt(1, dep.getId());
			rs = st.executeQuery();

			List<Seller> lista = new ArrayList<Seller>();
			Map<Integer, Department> aux = new HashMap<Integer, Department>();
			while (rs.next()) {

				Department obj = aux.get(rs.getInt("DepartmentId"));
				if (obj == null) {
					obj = instantiateDepartment(rs);
					aux.put(rs.getInt("DepartmentId"), obj);
				}

				Seller seller = instantiateSeller(rs, obj);
				lista.add(seller);
			}
			return lista;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

}
