package servlet;

import bean.*;
import dao.*;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import java.util.List;

import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;


/**
 * Servlet implementation class User
 */
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminServlet() {
        super();
    }


	private int getIntFromParameter(String str, int defaultValue) {
		int id;
		
		try {
			id = Integer.parseInt(str);
		} catch (Exception e) {
			id = defaultValue;
		}
		return id;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String 	op 			= request.getParameter("op");
		String userid = request.getParameter("id");
		String 	actionUrl 	= "";
		boolean ret;

		try {
			if (op == null || op.equals("member")) {
				int page = getIntFromParameter(request.getParameter("page"), 1);
				
				PageResult<Member> users = MemberDAO.getPage(page, 10);
				request.setAttribute("users", users);
				request.setAttribute("page", page);
				actionUrl = "admin/index.jsp";
			} else if (op.equals("show")) {
				Member user = MemberDAO.findById(userid);
				request.setAttribute("user", user);
				actionUrl = "admin/show.jsp";
			} else if (op.equals("update")) {
				Member user = MemberDAO.findById(userid);
				request.setAttribute("user", user);
				request.setAttribute("method", "PUT");
				actionUrl = "admin/signup.jsp";
			} else if (op.equals("delete")) {
				ret = MemberDAO.remove(userid);
				request.setAttribute("result", ret);
				
				if (ret) {
					request.setAttribute("msg", "사용자 정보가 삭제되었습니다.");
					actionUrl = "admin/success.jsp";
				} else {
					request.setAttribute("error", "사용자 정보 삭제에 실패했습니다.");
					actionUrl = "admin/error.jsp";
				}
			} else if (op.equals("signup")) {
				request.setAttribute("method", "POST");
				request.setAttribute("user", new Member());
				actionUrl = "admin/signup.jsp";
			}  else if (op.equals("article")) {
				int page = getIntFromParameter(request.getParameter("page"), 1);
				PageResult<Article> posts = ArticleDAO.getPage(page, 10);
				request.setAttribute("page", page);
				request.setAttribute("posts", posts);
				actionUrl = "admin/article.jsp";
			} else {
				request.setAttribute("error", "알 수 없는 명령입니다");
				actionUrl = "admin/error.jsp";
			}
		} catch (SQLException | NamingException e) {
			request.setAttribute("error", e.getMessage());
			e.printStackTrace();
			actionUrl = "admin/error.jsp";
		}
		
		RequestDispatcher dispatcher = request.getRequestDispatcher(actionUrl);
		dispatcher.forward(request, response);
	}

	private boolean isRegisterMode(MultipartRequest request) {
		String method = request.getParameter("_method");
		return method == null || method.equals("POST");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean ret 		= false;
		boolean res = true;
		String 	actionUrl = "admin";
		String 	msg;
		Member 	user 		= new Member();
		MultipartRequest multi = null;
		
		request.setCharacterEncoding("utf-8");
		List<String> errorMsgs = new ArrayList<String>();
				
		String imagePath = getServletContext().getRealPath("images/profile"); //실제로 업로드 될 폴더의 경로 설정
		int size = 2 * 1024 * 1024; 
		
		multi = new MultipartRequest(request, imagePath, size, "utf-8", new DefaultFileRenamePolicy());		
		
		// POST로 설정된 form에서 값을 받아와서 임시로 저장함 
		String userid 		= multi.getParameter("userid");
		String pwd 			= multi.getParameter("pwd");
		String pwd_confirm 	= multi.getParameter("pwd_confirm");
		String lastname 	= multi.getParameter("lastname");
		String firstname 	= multi.getParameter("firstname");
		String nickname 	= multi.getParameter("nickname");
		String email 		= multi.getParameter("email");
		String gender 		= multi.getParameter("gender");
		String website		= multi.getParameter("website");
		String introduce 	= multi.getParameter("introduce");

		if (pwd.length() < 6 || pwd == null ) {
			errorMsgs.add("비밀번호는 6자 이상 입력해주세요.");
			res = false;
		} 
		if (!pwd.equals(pwd_confirm)) {
			errorMsgs.add("비밀번호가 일치하지 않습니다.");
			res = false;
		}

		if (userid == null || userid.trim().length() == 0) {
			errorMsgs.add("ID를 반드시 입력해주세요.");
			res = false;
		}
		
		if (lastname == null || lastname.trim().length() == 0) {
			errorMsgs.add("성을 반드시 입력해주세요.");
			res = false;
		}
		
		if (firstname == null || firstname.trim().length() == 0) {
			errorMsgs.add("이름을 반드시 입력해주세요.");
			res = false;
		}
		
		if (nickname == null || nickname.trim().length() == 0) {
			errorMsgs.add("별명을 반드시 입력해주세요.");
			res = false;
		}
		
		if (gender == null || !(gender.equals("M") || gender.equals("F") )) {
			errorMsgs.add("성별에 적합하지 않은 값이 입력되었습니다.");
			res = false;
		}

		// sign.jsp에서 입력받은 데이터를  javabean인 Member 클래스의 인스턴스 user에 등록 
		user.setUserid(userid);
		user.setPwd(pwd); 
		user.setRegisterdate(new Timestamp(System.currentTimeMillis()));
		user.setLastname(lastname);
		user.setFirstname(firstname);
		user.setNickname(nickname);
		user.setEmail(email);
		user.setGender(gender);
		user.setIntroduce(introduce);
		user.setWebsite(website);
		
		try {
			//bean인 user를 가지고 DAO의 함수를 호출하여 DB처리를 함 
			if(res) {
				if (isRegisterMode(multi)) {
					ret = MemberDAO.create(user);
					msg = "<b>" + lastname + firstname + "</b>님의 사용자 정보가 등록되었습니다.";
				} else {
					ret = MemberDAO.update(user);
					actionUrl = "admin/success.jsp";
					msg = "<b>" + lastname + firstname + "</b>님의 사용자 정보가 수정되었습니다.";
				}
				if (ret != true) {
					errorMsgs.add("변경에 실패했습니다.");
					actionUrl = "admin/error.jsp";
				} else {
					request.setAttribute("msg", msg);
					actionUrl = "admin/success.jsp";
				}
			}
		} catch (Exception e) {
			errorMsgs.add(e.getMessage());
			System.out.println(e.getMessage());
			actionUrl = "admin/error.jsp";
		}
		
		request.setAttribute("errorMsgs", errorMsgs);
		RequestDispatcher dispatcher = request.getRequestDispatcher(actionUrl);
		dispatcher.forward(request,  response);
	}
}
