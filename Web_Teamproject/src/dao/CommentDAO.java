package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.naming.*;
import javax.sql.*;

import bean.*;

public class CommentDAO {

	public static DataSource getDataSource() throws NamingException {
		Context initCtx = null;
		Context envCtx = null;

		// Obtain our environment naming context
		initCtx = new InitialContext();
		envCtx = (Context) initCtx.lookup("java:comp/env");

		// Look up our data source
		return (DataSource) envCtx.lookup("jdbc/WebDB");
	}
	
	public static ArrayList<Comment> getCommentList(int postid) throws SQLException, NamingException {
		ArrayList<Comment> list = new ArrayList<Comment>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		DataSource ds = getDataSource();
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select DISTINCT	comment.*, member.nickname, member.profilephoto " 
								 + "from  comment join article, member "
								 + "where comment.postid = " + postid 
								 + " and comment.userid = member.userid order by comment.commentdate ");

			while(rs.next()) {
				list.add(new Comment(rs.getInt("commentid"), 
									rs.getInt("postid"), 
									rs.getString("userid"), 
									rs.getString("commentcontent"), 
									rs.getTimestamp("commentdate"), 
									rs.getInt("commentip"), 
									rs.getString("nickname"), 
									rs.getString("profilephoto")));
			}
		} finally {
			// 무슨 일이 있어도 리소스를 제대로 종료
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return list;
	}
	
	public static boolean create(Comment comment) throws SQLException, NamingException {
		int result;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		DataSource ds = getDataSource();
		try {
			conn = ds.getConnection();

			// 질의 준비
			stmt = conn.prepareStatement(
					"INSERT INTO comment(postid, userid, commentcontent, commentdate, commentip) " +
					"VALUES( ?, ?, ?, ?, ?)"
					);

			stmt.setInt(1, comment.getPostid());
			stmt.setString(2, comment.getUserid());
			stmt.setString(3, comment.getCommentcontent());
			stmt.setTimestamp(4, comment.getCommentdate());
			stmt.setInt(5,  comment.getCommentip());

			// 수행
			result = stmt.executeUpdate();
		} finally {
			// 무슨 일이 있어도 리소스를 제대로 종료
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}
		
		return (result == 1);
	}
	
	public static boolean remove(int id) throws NamingException, SQLException {
		int result;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		DataSource ds = getDataSource();

		try {
			conn = ds.getConnection();

			// 질의 준비
			stmt = conn.prepareStatement("DELETE FROM comment WHERE commentid=?");
			stmt.setInt(1, id);

			// 수행
			result = stmt.executeUpdate();
		} finally {
			// 무슨 일이 있어도 리소스를 제대로 종료
			if (rs != null) try{rs.close();} catch(SQLException e) {}
			if (stmt != null) try{stmt.close();} catch(SQLException e) {}
			if (conn != null) try{conn.close();} catch(SQLException e) {}
		}

		return (result == 1);		
	}
}