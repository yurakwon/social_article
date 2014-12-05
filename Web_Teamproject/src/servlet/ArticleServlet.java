package servlet;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Article;
import bean.Category;
import bean.Member;
import bean.PageResult;
import bean.Post;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import dao.ArticleDAO;
import dao.MemberDAO;
import dao.PostDAO;


/**
 * Servlet implementation class ArticleServlet
 */
@WebServlet("/article")
public class ArticleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ArticleServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		
		request.setCharacterEncoding("utf-8");
		String op = request.getParameter("op");
		String search = "";
		String actionUrl = "";
		int cg_num = -1, op_num = -1;
		boolean ret = false;
		
		try {
			if(op == null) {
				op = "list";
			} else if(op.equals("category")) {
				cg_num = Integer.parseInt(request.getParameter("cate"));
			} else if(op.equals("delete") || op.equals("update")) {
				op_num = Integer.parseInt(request.getParameter("id"));
			} else if(op.equals("search")) {
				search = request.getParameter("search");
				byte b[] = search.getBytes("ISO-8859-1");
				search = new String(b, "UTF-8");

			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			op_num = -1;
		}
		
		if(request.getParameter("search") != null) {
			search = request.getParameter("search");
			op = "search";
		}

		try {
			Category list = ArticleDAO.getlist(); // 카테고리 리스트를 받아옴
			if(op.equals("write")) { 
				request.setAttribute("method", "POST");
				request.setAttribute("article", new Article());
				request.setAttribute("category", list);
				actionUrl = "articlewrite.jsp";
			} else if(op.equals("update")) {
				Article post = ArticleDAO.findbyId(op_num);
				request.setAttribute("article", post);
				request.setAttribute("method", "PUT");
				request.setAttribute("category", list);
				
				actionUrl = "articlewrite.jsp";
			} else if(op.equals("list")) {
				request.setAttribute("category", list);
				
				actionUrl = "photolist_all.jsp";
			} else if(op.equals("search")) {
				request.setAttribute("op", "search");
				request.setAttribute("search", search);
				request.setAttribute("category", list);
			
				actionUrl = "photolist.jsp";
			} else if(op.equals("category")) {
				request.setAttribute("op", "category");
				request.setAttribute("category", list);
				request.setAttribute("cate_num", cg_num);

				actionUrl = "photolist.jsp";
			} else if(op.equals("delete")) {
				ret = PostDAO.remove(op_num);
				if(ret) {
					System.out.println(op_num + "번째 글 삭제성공");
				} else {
					System.out.println(op_num + "번째 글 삭제실패");
				}
				actionUrl = "";
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
		if(op.equals("delete")) {
				response.sendRedirect(actionUrl);
		} else {
			RequestDispatcher dispatcher = request.getRequestDispatcher(actionUrl);
			dispatcher.forward(request, response);
		}
	}

	private boolean isUploadMode(MultipartRequest request) {
		String method = request.getParameter("_method");
		return method == null || method.equals("POST");
	}	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		boolean ret 		= false;
		boolean res = true;
		String 	actionUrl = "";
		Article post = new Article();
		MultipartRequest multi = null;
		
		request.setCharacterEncoding("utf-8");
	}
}
