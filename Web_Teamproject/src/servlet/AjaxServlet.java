package servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import sun.org.mozilla.javascript.internal.json.JsonParser;

import bean.*;

import dao.*;

/**
 * Servlet implementation class AjaxServlet
 */
@WebServlet("/AjaxServlet")
public class AjaxServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AjaxServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		JSONObject rsobj = new JSONObject();
		Post post = new Post();
		ArrayList<Post> list = new ArrayList<Post>();
		String searchtext = null;
		
		String op = request.getParameter("op");
		
		int op_num = -1;
		int op_num1 = -1;
		try {
			if(op.equals("popup")) {
				//get Post id
				op_num = Integer.parseInt(request.getParameter("postid"));
			} else if(op.equals("page")) {
				//get Page num
				op_num = Integer.parseInt(request.getParameter("page"));
			} else if(op.equals("category")) {
				//get category
				op_num = Integer.parseInt(request.getParameter("page"));
				op_num1 = Integer.parseInt(request.getParameter("code"));
			} else if(op.equals("search")) {
				//get category
				op_num = Integer.parseInt(request.getParameter("page"));
				searchtext = request.getParameter("code");
			} 
		} catch (NumberFormatException e) {
			e.printStackTrace();
			op_num = -1;
		}
		
		// 로그인 상태이면 사용자의 정보를 받아온다
		String loginphoto = "";
		if(session != null) {
			Member user = (Member) session.getAttribute("user");
			if(user != null) {
				loginphoto = user.getProfilephoto();
				rsobj.put("loginphoto", loginphoto);
			}
		}
		
		try {
			if(op.equals("popup")) {
				post = PostDAO.findByPostID(op_num);
				JSONArray comjsar = new JSONArray();
				
				rsobj.put("user", post.getMember().UsertoJson());
				rsobj.put("article", post.getArticle().ArtitoJson());
			
				for(int i=0; i<post.getComment().size(); i++) {
					comjsar.add(post.getComment().get(i).CommenttoJson());
				}
				rsobj.put("comment", comjsar);
			} else if(op.equals("page")) {
				list = PostDAO.getPage(op_num);
				
				JSONArray postjsar = new JSONArray();
				for(int i=0; i<list.size(); i++) {
					JSONObject subjsobj = new JSONObject();
					JSONArray comjsar = new JSONArray();
					subjsobj.put("user", list.get(i).getMember().UsertoJson());
					subjsobj.put("article", list.get(i).getArticle().ArtitoJson());
					
					for(int j=0; j<list.get(i).getComment().size(); j++) {
						comjsar.add(list.get(i).getComment().get(j).CommenttoJson());
					}
					subjsobj.put("comment", comjsar);
					postjsar.add(subjsobj);
				}
				rsobj.put("post", postjsar);
			} else if(op.equals("category") || op.equals("search")) {
				if (op.equals("category")) {
					list = PostDAO.getCategoryPage(op_num, op_num1);
				} else if ( op.equals("search")) {
					list = PostDAO.getSearchResult(op_num, searchtext);
				}
				
				JSONArray postjsar = new JSONArray();
				for(int i=0; i<list.size(); i++) {
					JSONObject subjsobj = new JSONObject();
					JSONArray comjsar = new JSONArray();
					subjsobj.put("user", list.get(i).getMember().UsertoJson());
					subjsobj.put("article", list.get(i).getArticle().ArtitoJson());
					
					for(int j=0; j<list.get(i).getComment().size(); j++) {
						comjsar.add(list.get(i).getComment().get(j).CommenttoJson());
					}
					subjsobj.put("comment", comjsar);
					postjsar.add(subjsobj);
				}
				rsobj.put("post", postjsar);
			}			
		} catch (Exception e) {
			System.out.println(e);
			rsobj.put("ERROR", e.getMessage());
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(rsobj.toJSONString());
	}
}
