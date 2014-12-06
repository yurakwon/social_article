package servlet;

import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import dao.*;

import bean.*;

/**
 * Servlet implementation class CommentServlet
 */
@WebServlet("/Comment")
public class CommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CommentServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean ret = false;
		JSONObject rsobj = new JSONObject();

		// 어떤 명령인지 확인
		String op = request.getParameter("op");
		int id = Integer.parseInt(request.getParameter("id"));

		try {
			if(op.equals("remove_comment")) { // 댓글 삭제
				ret = CommentDAO.remove(id);
			} 

			if(ret) {
				rsobj.put("result", "ok");//id 번째 삭제성공
			} else {
				rsobj.put("result", "no");//id 번쨰 삭제실패
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(rsobj.toJSONString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		boolean ret 		= false;;
		Comment comment = new Comment();

		request.setCharacterEncoding("utf-8");
		int postid = Integer.parseInt(request.getParameter("postid"));
		Member user = (Member) session.getAttribute("user"); 		
		String userid = user.getUserid();
		String commentcontent = request.getParameter("comment");
		System.out.println("코멘트내용 : " + commentcontent);

		//빈코멘트 에러는 photolist와 js에서 막는다.
		
		comment.setCommentcontent(commentcontent);
		comment.setCommentdate(new Timestamp(System.currentTimeMillis()));
		comment.setCommentip(123456789);
		comment.setPostid(postid);
		comment.setUserid(userid);		

		try {
			ret = CommentDAO.create(comment);
			if(ret) {
				//코멘트 성공
			} else {
				//코멘트 실패
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		response.sendRedirect("");
	}

}