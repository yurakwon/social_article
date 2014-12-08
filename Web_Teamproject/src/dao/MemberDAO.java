package dao;

import java.sql.*;
import javax.naming.*;
import javax.sql.*;

import bean.Member;
import bean.PageResult;


public class MemberDAO {
	
	public static DataSource getDataSource() throws NamingException {
		Context initCtx = null;
		Context envCtx = null;

		initCtx = new InitialContext();
		envCtx = (Context) initCtx.lookup("java:comp/env");

		return (DataSource) envCtx.lookup("jdbc/WebDB");
	}
	
	public static PageResult<Member> getPage(int page, int numItemsInPage) 
			throws SQLException, NamingException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;		

		if (page <= 0) {
			page = 1;
		}
		
		DataSource ds = getDataSource();
		PageResult<Member> result = new PageResult<Member>(numItemsInPage, page);
		
		
		int startPos = (page - 1) * numItemsInPage;
		
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			
			// users 테이블: user 수 페이지수 개산
	 		rs = stmt.executeQuery("SELECT COUNT(*) FROM member");
			rs.next();
			
			result.setNumItems(rs.getInt(1));
			
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM member LIMIT " + startPos + ", " + numItemsInPage);
			
			while(rs.next()) {
				result.getList().add(new Member(
											rs.getString("userid"),
											rs.getString("userpassword"),
											rs.getTimestamp("registerdate"),
											rs.getString("lastname"),
											rs.getString("firstname"),
											rs.getString("nickname"),
											rs.getString("profilephoto"),
											rs.getString("gender"),
											rs.getString("email"),
											rs.getString("introduce"),
											rs.getString("website")));
			}
		} finally {
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return result;		
	}
	
	public static Member findById(String userid) throws NamingException, SQLException{
		Member user = null;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		DataSource ds = getDataSource();
		
		try {
			conn = ds.getConnection();

			stmt = conn.prepareStatement("SELECT * FROM member WHERE userid = ?");
			stmt.setString(1, userid);
			
			rs = stmt.executeQuery();

			if (rs.next()) {
				user = new Member(
						rs.getString("userid"),
						rs.getString("userpassword"),
						rs.getTimestamp("registerdate"),
						rs.getString("lastname"),
						rs.getString("firstname"),
						rs.getString("nickname"),
						rs.getString("profilephoto"),
						rs.getString("gender"),
						rs.getString("email"),
						rs.getString("introduce"),
						rs.getString("website"));
			}	
		} finally {
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return user;
	}
	
	public static boolean create(Member user) throws SQLException, NamingException {
		int result;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		DataSource ds = getDataSource();
		try {
			conn = ds.getConnection();

			stmt = conn.prepareStatement(
					"INSERT INTO member(userid, userpassword, registerdate, lastname, firstname, nickname, profilephoto, gender, email, introduce, website) " +
					"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
					);
			stmt.setString(1,  user.getUserid());
			stmt.setString(2,  user.getPwd());
			stmt.setTimestamp(3,  user.getRegisterdate());
			stmt.setString(4,  user.getLastname());
			stmt.setString(5,  user.getFirstname());
			stmt.setString(6,  user.getNickname());
			if(user.getProfilephoto() == null) {
				stmt.setString(7,  "default.gif");
			} else {
				stmt.setString(7,  user.getProfilephoto());
			}
			stmt.setString(8,  user.getGender());
			stmt.setString(9,  user.getEmail());
			stmt.setString(10,  user.getIntroduce());
			stmt.setString(11,  user.getWebsite());
			
			result = stmt.executeUpdate();
		} finally {
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return (result == 1);
	}
	
	public static boolean update(Member user) throws SQLException, NamingException {
		int result;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		DataSource ds = getDataSource();
		
		try {
			conn = ds.getConnection();
			
			// 사진 변경이 없으면
			if(user.getProfilephoto() == null) {
				stmt = conn.prepareStatement(
						"UPDATE member " +
						"SET  userpassword=?, lastname=?, firstname=?, nickname=?, gender=?, email=?, introduce=?, website=?" +
						"WHERE userid=?"
						);
				stmt.setString(1,  user.getPwd());
				stmt.setString(2,  user.getLastname());
				stmt.setString(3,  user.getFirstname());
				stmt.setString(4,  user.getNickname());
				stmt.setString(5,  user.getGender());
				stmt.setString(6,  user.getEmail());
				stmt.setString(7,  user.getIntroduce());
				stmt.setString(8,  user.getWebsite());
				stmt.setString(9,  user.getUserid());
			} else {
				stmt = conn.prepareStatement(
						"UPDATE member " +
						"SET  userpassword=?, lastname=?, firstname=?, nickname=?, profilephoto=?, gender=?, email=?, introduce=?, website=?" +
						"WHERE userid=?"
						);
				stmt.setString(1,  user.getPwd());
				stmt.setString(2,  user.getLastname());
				stmt.setString(3,  user.getFirstname());
				stmt.setString(4,  user.getNickname());
				stmt.setString(5,  user.getProfilephoto());
				stmt.setString(6,  user.getGender());
				stmt.setString(7,  user.getEmail());
				stmt.setString(8,  user.getIntroduce());
				stmt.setString(9,  user.getWebsite());
				stmt.setString(10,  user.getUserid());
			}
			
			result = stmt.executeUpdate();
		} finally {
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return (result == 1);		
	}
	
	public static boolean remove(String userid) throws NamingException, SQLException {
		int result;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		DataSource ds = getDataSource();
		
		try {
			conn = ds.getConnection();

			stmt = conn.prepareStatement("DELETE FROM member WHERE userid=?");
			stmt.setString(1,  userid);
			
			result = stmt.executeUpdate();
		} finally {
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return (result == 1);		
	}
}
