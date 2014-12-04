package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.naming.*;
import javax.sql.*;

import bean.Article;
import bean.Category;
import bean.PageResult;

public class ArticleDAO {

	public static DataSource getDataSource() throws NamingException {
		Context initCtx = null;
		Context envCtx = null;

		initCtx = new InitialContext();
		envCtx = (Context) initCtx.lookup("java:comp/env");

		return (DataSource) envCtx.lookup("jdbc/WebDB");
	}
	
	public static PageResult<Article> getPage(int page, int numItemsInPage) 
			throws SQLException, NamingException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;		

		if (page <= 0) {
			page = 1;
		}
		
		DataSource ds = getDataSource();
		PageResult<Article> result = new PageResult<Article>(numItemsInPage, page);
		
		int startPos = (page - 1) * numItemsInPage;
		
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			
			// users 테이블: user 수 페이지수 개산
	 		rs = stmt.executeQuery("SELECT COUNT(*) FROM article");
			rs.next();

			result.setNumItems(rs.getInt(1));
			
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			
	 		// users 테이블 SELECT
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM article LIMIT " + startPos + ", " + numItemsInPage);
			
			while(rs.next()) {
				result.getList().add(new Article(rs.getInt("postid"),
												rs.getString("userid"),
												rs.getInt("albumid"),
												rs.getString("photo"),
												rs.getString("content"),
												rs.getTimestamp("postdate"),
												rs.getString("category"),
												rs.getInt("hits"),
												rs.getInt("likehits"),
												rs.getInt("postip")));
			}
		} finally {
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return result;		
	}	
	
	public static Category getlist() throws SQLException, NamingException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Category catelist = new Category(new ArrayList<String>());
		
		DataSource ds = getDataSource();
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM category");
			
			while(rs.next()) {
				catelist.getlist().add(rs.getString("list"));
			}
		} finally {
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return catelist;
	}
	
	
	/* 글올리기 */
	public static boolean create(Article post) throws SQLException, NamingException {
		int result;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		DataSource ds = getDataSource();
		try {
			conn = ds.getConnection();

			stmt = conn.prepareStatement(
					"INSERT INTO article(userid, albumid, photo, content, category, postdate, postip) " +
					"VALUES(?, ?, ?, ?, ?, ?, ?)"
					);

			stmt.setString(1, post.getUserid());
			stmt.setInt(2, 1); //앨범명
			stmt.setString(3, post.getPhoto());
			stmt.setString(4, post.getContent());
			stmt.setString(5, post.getCategory());
			stmt.setTimestamp(6, post.getPostdate());
			stmt.setInt(7, 123456789); // ip주소

			result = stmt.executeUpdate();
		} finally {
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return (result == 1);
	}
	
	/* 글수정  */
	public static boolean update(Article post) throws SQLException, NamingException {
		int result;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		DataSource ds = getDataSource();
		
		try {
			conn = ds.getConnection();
			stmt = conn.prepareStatement(
					"UPDATE article " +
					"SET  albumid=?, content=?, category=?" +
					"WHERE postid=?"
					);
			
			stmt.setInt(1, 2);
			stmt.setString(2, post.getContent());
			stmt.setString(3, post.getCategory());
			stmt.setInt(4, post.getPostid());
			
			result = stmt.executeUpdate();
		} finally {
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return (result == 1);		
	}
	/* 글삭제  */
	/* 글찾기  */
	public static Article findbyId(int postid) throws SQLException, NamingException {
		Article post = new Article();
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;		

		DataSource ds = getDataSource();

		try {
			conn = ds.getConnection();
			
			stmt = conn.prepareStatement("SELECT * FROM article WHERE article.postid = ?");
			stmt.setInt(1, postid);
			
			rs = stmt.executeQuery();
			while(rs.next()) {
				post = new Article(rs.getInt("postid"),
									rs.getString("userid"),
									rs.getInt("albumid"),
									rs.getString("photo"),
									rs.getString("content"),
									rs.getTimestamp("postdate"),
									rs.getString("category"),
									rs.getInt("hits"),
									rs.getInt("likehits"),
									rs.getInt("postip"));
			}
		} finally {
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return post;		
	}
}
