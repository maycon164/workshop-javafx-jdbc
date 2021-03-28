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
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	private Connection conn;

	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department dep) {
		PreparedStatement st = null;
		try {
			String sql = "Insert into department (Name) Values (?)";
			st = conn.prepareStatement(sql + "", Statement.RETURN_GENERATED_KEYS);
			st.setString(1, dep.getName());
			int linha = st.executeUpdate();
			if (linha > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					System.out.println("Department Inserido no banco");
					dep.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("erro não foi possivel inserir");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void update(Department dep) {
		PreparedStatement st = null;
		try {
			String sql = "Update department Set Name = ? where Id = ?";
			st = conn.prepareStatement(sql);
			st.setString(1, dep.getName());
			st.setInt(2, dep.getId());
			int linha = st.executeUpdate();
		
			if (linha > 0) {
				System.out.println(linha + " linhas afetadas");
				System.out.println("Update completo");
			} else {
				throw new DbException("Erro update cancelado");
			}
		
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
	}

	@Override
	public void deleteById(int id) {
		PreparedStatement st = null;
		try {
			String sql = "delete from department where Id = ?";
			st = conn.prepareStatement(sql);
			st.setInt(1, id);
			int linha = st.executeUpdate();
			if (linha > 0) {
				System.out.println("Deletado");
			} else {
				throw new DbException("Error delete");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}

	}

	@Override
	public Department findById(int id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "select department.* from department " + "where Id = ?";
			st = conn.prepareStatement(sql);
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Department dep = new Department(rs.getInt("Id"), rs.getString("Name"));
				return dep;
			} else {
				throw new DbException("Erro department não encontrado");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}

	}

	@Override
	public List<Department> findAll() {
		Statement st = null;
		ResultSet rs = null;
		try {
			String sql = "Select * from department";
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			Map<String, Department> auxiliar = new HashMap<String, Department>();

			while (rs.next()) {
				Department dep = auxiliar.get(rs.getString("Name"));
				if (dep == null) {
					dep = new Department(rs.getInt("Id"), rs.getString("Name"));
					auxiliar.put(rs.getString("Name"), dep);
				}

			}

			List<Department> lista = new ArrayList<Department>();
			auxiliar.forEach((key, value) -> lista.add(value));

			return lista;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
	}

}
