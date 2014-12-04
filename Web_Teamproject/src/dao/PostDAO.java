package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.naming.*;
import javax.sql.*;

import org.apache.catalina.connector.Request;

import bean.*;

public class PostDAO {
	
	public static DataSource getDataSource() throws NamingException {
		Context initCtx = null;
		Context envCtx = null;

		// Obtain our environment naming context
		initCtx = new InitialContext();
		envCtx = (Context) initCtx.lookup("java:comp/env");

		// Look up our data source
		return (DataSource) envCtx.lookup("jdbc/WebDB");
	}
	
	public static ArrayList<Post> getPage(int page) throws SQLException, NamingException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;		
		ArrayList<Comment> comment = new ArrayList<Comment>();
		
		if (page <= 0) {
			page = 1;
		}
		
		DataSource ds = getDataSource();
		ArrayList<Post> result = new ArrayList<Post>();
		
		int startPos = (page - 1) * 20;
		
		try {
			conn = ds.getConnection();
	 		// 전체 글  테이블 SELECT.. startPos부터 numItems까지
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select DISTINCT * from member,article where  member.userid = article.userid order by article.postdate desc limit " + startPos + ", 20");
			
			// 먼저 글의 목록을 받아온다
			while(rs.next()) { 
				result.add(new Post(new Member( rs.getString("userid"),
														rs.getString("userpassword"),
														rs.getTimestamp("registerdate"),
														rs.getString("lastname"),
														rs.getString("firstname"),
														rs.getString("nickname"),
														rs.getString("profilephoto"),
														rs.getString("gender"),
														rs.getString("email"),
														rs.getString("introduce"),
														rs.getString("website"),
														rs.getInt("level")),
											  new Article(rs.getInt("postid"),
														rs.getString("userid"),
														rs.getInt("albumid"),
														rs.getString("photo"),
														rs.getString("content"),
														rs.getTimestamp("postdate"),
														rs.getString("category"),
														rs.getInt("hits"),
														rs.getInt("likehits"),
														rs.getInt("postip")),
												new ArrayList<Comment>()));
			}

			// 글의 목록에 코멘트 리스트를 채워준다.
			for(int i=0; i < result.size(); i++) {
				comment = CommentDAO.getCommentList(result.get(i).getArticle().getPostid());			
				result.get(i).setComment(comment);
			}
		} finally {
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return result;		
	}

	/* 각 카테고리에 대해 20개씩 글을 가져오는 메소드 */
	public static ArrayList<Post> getCategoryPage(int page, int cate_num) throws SQLException, NamingException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;		
		ArrayList<Comment> comment = new ArrayList<Comment>();
		
		DataSource ds = getDataSource();
		ArrayList<Post> result = new ArrayList<Post>();
		
		int startPos = (page - 1) * 20;
		
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			
	 		// 전체 글  테이블 SELECT.. startPos부터 numItems까지
			stmt = conn.createStatement();
			rs = stmt.executeQuery( "select * " +  
									" from member, article" +
									" where member.userid = article.userid" +
									"	and article.category = (select category.list from category where category.num = " + cate_num +
									") order by article.postdate desc limit " + startPos + ", 20");
			
			while(rs.next()) { 
				result.add(new Post(new Member( rs.getString("userid"),
												rs.getString("userpassword"),
												rs.getTimestamp("registerdate"),
												rs.getString("lastname"),
												rs.getString("firstname"),
												rs.getString("nickname"),
												rs.getString("profilephoto"),
												rs.getString("gender"),
												rs.getString("email"),
												rs.getString("introduce"),
												rs.getString("website"),
//												rs.getString("info"),
												rs.getInt("level")),
									  new Article(rs.getInt("postid"),
												rs.getString("userid"),
												rs.getInt("albumid"),
												rs.getString("photo"),
												rs.getString("content"),
												rs.getTimestamp("postdate"),
												rs.getString("category"),
												rs.getInt("hits"),
												rs.getInt("likehits"),
												rs.getInt("postip")),
										new ArrayList<Comment>()));
			}
			// 글의 목록에 코멘트 리스트를 채워준다.
			for(int i=0; i < result.size(); i++) {
				comment = CommentDAO.getCommentList(result.get(i).getArticle().getPostid());			
				result.get(i).setComment(comment);
			}
			
		} finally {
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return result;		
	}
	
	/* 각 카테고리에 대해 20개씩 글을 가져오는 메소드 */
	public static ArrayList<Post> getSearchResult(int page, String text) throws SQLException, NamingException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;		
		ArrayList<Comment> comment = new ArrayList<Comment>();
		
		DataSource ds = getDataSource();
		ArrayList<Post> result = new ArrayList<Post>();
		
		int startPos = (page - 1) * 20;
		
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			
	 		// 전체 글  테이블 SELECT.. startPos부터 numItems까지
			stmt = conn.createStatement();
			rs = stmt.executeQuery( "select * " +  
									" from member, article" +
									" where member.userid = article.userid and article.content like '%" + text + "%' order by article.postdate desc limit " + startPos + ", 20");
			
			while(rs.next()) { 
				result.add(new Post(new Member( rs.getString("userid"),
												rs.getString("userpassword"),
												rs.getTimestamp("registerdate"),
												rs.getString("lastname"),
												rs.getString("firstname"),
												rs.getString("nickname"),
												rs.getString("profilephoto"),
												rs.getString("gender"),
												rs.getString("email"),
												rs.getString("introduce"),
												rs.getString("website"),
												rs.getInt("level")),
									  new Article(rs.getInt("postid"),
												rs.getString("userid"),
												rs.getInt("albumid"),
												rs.getString("photo"),
												rs.getString("content"),
												rs.getTimestamp("postdate"),
												rs.getString("category"),
												rs.getInt("hits"),
												rs.getInt("likehits"),
												rs.getInt("postip")),
										new ArrayList<Comment>()));
			}
			// 글의 목록에 코멘트 리스트를 채워준다.
			for(int i=0; i < result.size(); i++) {
				comment = CommentDAO.getCommentList(result.get(i).getArticle().getPostid());			
				result.get(i).setComment(comment);
			}
			
		} finally {
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return result;		
	}
	
	/* 아이디에 해당하는 글을 가져오는 메소드 */
	public static Post findByPostID(int id) throws SQLException, NamingException {
		Connection conn = null;
		PreparedStatement stmt = null;
		Statement st = null;
		ResultSet rs = null;
		ArrayList<Comment> comment = new ArrayList<Comment>();
		
		Post post = new Post();
		DataSource ds = getDataSource();
		
		try {
			conn = ds.getConnection();
			
			st = conn.createStatement();
			st.executeUpdate("update article set hits = hits + 1 where article.postid = " + id); // 조회수 1증가

			st.close();
			st = null;
			
			stmt = conn.prepareStatement("select DISTINCT * from article, member where article.postid = ? and article.userid = member.userid");
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			while(rs.next()) { 
				post = new Post(new Member( rs.getString("userid"),
											rs.getString("userpassword"),
											rs.getTimestamp("registerdate"),
											rs.getString("lastname"),
											rs.getString("firstname"),
											rs.getString("nickname"),
											rs.getString("profilephoto"),
											rs.getString("gender"),
											rs.getString("email"),
											rs.getString("introduce"),
											rs.getString("website"),
											rs.getInt("level")),
								  new Article(rs.getInt("postid"),
											rs.getString("userid"),
											rs.getInt("albumid"),
											rs.getString("photo"),
											rs.getString("content"),
											rs.getTimestamp("postdate"),
											rs.getString("category"),
											rs.getInt("hits"),
											rs.getInt("likehits"),
											rs.getInt("postip")),
									new ArrayList<Comment>());
			}
			
			comment = CommentDAO.getCommentList(id);			
			post.setComment(comment);		
		} catch(Exception e) { 
			e.printStackTrace();
		} finally {
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return post;		
	}
	
	public static boolean remove(int postid) throws NamingException, SQLException {
		int result;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		DataSource ds = getDataSource();
		
		try {
			conn = ds.getConnection();

			stmt = conn.prepareStatement("DELETE FROM article WHERE postid=?");
			stmt.setInt(1, postid);
			
			result = stmt.executeUpdate();
		} finally {
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return (result == 1);		
	}
}
