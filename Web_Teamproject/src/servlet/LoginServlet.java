package servlet;

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
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import bean.*;
import dao.*;

/**
 * Servlet implementation class User
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	private enum state {login, signup, update};
	
	public LoginServlet() {
		super();
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);

		String op = request.getParameter("op");
		String userid = "";
		String actionUrl = "";
		
		Member user = (Member) session.getAttribute("user"); 	
		if(user != null) {
			userid = user.getUserid();
		}
		
		// op가 없는 경우 무조건 login처리
		if(op == null) {
			op = "login";
		}
		
		try{
			if(op.equals("login")) { //로그인클릭경우
				actionUrl="login.jsp";
			} else if(op.equals("logout")) { 
				System.out.println(session.getAttribute("userid") + "님 로그아웃함");
				session.invalidate();
			} else if(op.equals("signup")) {
				request.setAttribute("method", "POST");
				request.setAttribute("user", new Member());
				actionUrl="signup.jsp";
			} else if (op.equals("update")) {
				user = MemberDAO.findById(userid);
				request.setAttribute("user", user);
				request.setAttribute("method", "PUT");
				actionUrl = "signup.jsp";
			}else {
			}
		} catch (Exception e) {
			System.out.println("세션종료");
			session.invalidate();
		}
		
		if(!op.equals("logout")) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(actionUrl);
			dispatcher.forward(request, response);
		} else if(op.equals("logout")) {
			response.sendRedirect(actionUrl);
		}
	}

	private state isRegisterMode(MultipartRequest request) {
		String method = request.getParameter("_method");
		if(method.equals("POST")) {
			return state.signup;
		} else if (method.equals("PUT")) {
			return state.update;
		} else {
			return state.login;
		}
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean ret = false; // 회원가입결과
		boolean res = true;
		HttpSession session = request.getSession(false);
		String actionUrl = "";
		List<String> errorMsgs = new ArrayList<String>();		
		Member user = new Member();
		
		/* 업로드 처리 관련 */
		String imagePath = getServletContext().getRealPath("images/profile"); 
		int size = 2 * 1024 * 1024; 
		MultipartRequest multi = new MultipartRequest(request, imagePath, size, "utf-8", new DefaultFileRenamePolicy());	

		//POST로 설정된 form에서 값을 받아와서 임시로 저장함 
		request.setCharacterEncoding("utf-8");//Multipart사용시 request가 소멸되서 다시세팅.
		String userid = multi.getParameter("userid");
		String userpwd = multi.getParameter("pwd");
			System.out.println("userid:" + userid + " / userpwd : "+ userpwd);
		String pwd_confirm 	= multi.getParameter("pwd_confirm");
		String lastname 	= multi.getParameter("lastname");
		String firstname 	= multi.getParameter("firstname");
		String nickname 	= multi.getParameter("nickname");
		String email 		= multi.getParameter("email");
		String gender 		= multi.getParameter("gender");
		String website		= multi.getParameter("website");
		String introduce 	= multi.getParameter("introduce");
		
		if(isRegisterMode(multi) == state.signup || isRegisterMode(multi) == state.update) {
			//회원가입
			if (userpwd.length() < 6 || userpwd == null ) {
				errorMsgs.add("비밀번호는 6자 이상 입력해주세요.");
				res = false;
			} 
			if (!userpwd.equals(pwd_confirm)) {
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

		} else if(isRegisterMode(multi) == state.login) {
			if (userid == null || userid.trim().length() == 0) {
				errorMsgs.add("ID를 입력해주세요.");
				res = false;
			}
			if (userpwd == null || userpwd.trim().length() == 0) {
				errorMsgs.add("비밀번호를 입력해주세요.");
				res = false;
			}
		}
		
		try {
			if(res && (isRegisterMode(multi) == state.signup || isRegisterMode(multi) == state.update)) {
				// sign.jsp에서 입력받은 데이터를  Member user에 등록
				user.setUserid(userid);
				user.setPwd(userpwd);
				user.setRegisterdate(new Timestamp(System.currentTimeMillis()));
				user.setLastname(lastname);
				user.setFirstname(firstname);
				user.setNickname(nickname);
				user.setEmail(email);
				user.setGender(gender);
				user.setIntroduce(introduce);
				user.setWebsite(website);
				
				System.out.println(isRegisterMode(multi).toString());
				ret = SignUpManager.SignUp(multi, response, user, imagePath, isRegisterMode(multi).toString()); 
				if (user.getProfilephoto() == null) {
					errorMsgs.add("사진을 업로드하지 않거나 업로드에 실패하셨습니다.");
				}
				if(isRegisterMode(multi) == state.update) {
					// 정보업데이트를 하면 즉시 반영을 위해 세션을 다시 교체해줌
					session.setAttribute("user", user);
				}
				if(ret) {
					System.out.println("회원가입 || 수정 완료");
					actionUrl = "main.jsp";
				} else {
					System.out.println("회원가입 || 수정 실패");
					actionUrl = "error.jsp";
				}
			} else if(res && (isRegisterMode(multi) == state.login)) {
				user = MemberDAO.findById(userid);
				// 유저정보를 제대로 받아오면
				if(user != null) {
					if( user.getUserid().equals(userid) && user.getPwd().equals(userpwd) ) {
						session.setAttribute("user", user);
					    System.out.println("로그인성공! " + user.getUserid() + "님이 로그인하셨습니다.");
						actionUrl = "main.jsp";
						ret = true;
					} else if(user.getPwd() != userpwd) {
						errorMsgs.add("비밀번호가 일치하지 않습니다. 다시 확인해주세요");
						actionUrl = "login.jsp";
					}
				} else {
					errorMsgs.add("없는 아이디입니다. 다시 확인해주세요");
					actionUrl = "login.jsp";
				}
			} else {
				actionUrl = "error.jsp";
			}
		} catch (SQLException | NamingException e) {
			System.out.println(e.getMessage());
			errorMsgs.add("알 수 없는 문제가 발생하였습니다. 다시 시도해주세요");
			session.invalidate();
			// 다시 로그인 페이지로 이동
			actionUrl = "error.jsp";
		}
		
		if(actionUrl.equals("main.jsp")) {
			response.sendRedirect("");
		} else {		
			request.setAttribute("errorMsgs", errorMsgs);
			RequestDispatcher dispatcher = request.getRequestDispatcher(actionUrl);
			dispatcher.forward(request, response);
		}
	}

}
