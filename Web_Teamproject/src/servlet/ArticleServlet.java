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
			Category list = ArticleDAO.getlist(); // 카테고리 리스트를 받음
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
		
		List<String> errorMsgs = new ArrayList<String>();
		
		String imagePath = getServletContext().getRealPath("images/photo"); //실제로 업로드 될 폴더의 경로 설정
		int size = 5 * 1024 * 1024;
		
		multi = new MultipartRequest(request, imagePath, size, "utf-8", new DefaultFileRenamePolicy());		
		
		// POST로 설정된 form에서 값을 받아와서 저장
		if(!isUploadMode(multi)) { // 수정이면 게시글 id를 받아옴
			int postid = Integer.parseInt(multi.getParameter("postid"));
			post.setPostid(postid);
		}
		
		Member user = (Member) session.getAttribute("user"); 		
		String userid = user.getUserid();
		String photo = UploadPhoto(multi, response, imagePath);
		String content = multi.getParameter("content");
		String Category = multi.getParameter("category");

		if (isUploadMode(multi) && photo == null) {
			errorMsgs.add("사진을 등록하세요");
			res = false;
		}
		
		if (content == null || content.trim().length() == 0) {
			errorMsgs.add("내용을 입력해주세요");
			res = false;
		}

		post.setCategory(Category);
		post.setContent(content);
		post.setPostip(1);
		post.setPhoto(photo);
		post.setPostdate(new Timestamp(System.currentTimeMillis()));
		post.setUserid(userid);
		
		try {
			if(res) {
				if (isUploadMode(multi)) {
					ret = ArticleDAO.create(post);
				} else {
					ret = ArticleDAO.update(post);
				}
				if (ret != true) {
					errorMsgs.add("글 작성이나 수정에 실패했습니다.");
					actionUrl = "error.jsp";
				} else {
					actionUrl = "main.jsp";
				}
			}
		} catch (Exception e) {
			errorMsgs.add(e.getMessage());
			System.out.println(e.getMessage());
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

	private String UploadPhoto(MultipartRequest multi, HttpServletResponse response, String imagePath) {
		String photo = "";
		String changephoto = "";
		String curTimeStr 	= Long.toString(System.currentTimeMillis()); //시스템시간 사용
	
		try {
			// 업로드 된 이미지 이름 얻는다
			Enumeration files 	= multi.getFileNames();
			String 		file 	= (String) files.nextElement();
			
			//썸네일을 만든다
			if((multi.getOriginalFileName(file)) != null) {
				// 파일을 업로드 했으면 파일명을 얻음
				photo = multi.getOriginalFileName(file);
				// 파일명 변경준비
				changephoto 	= photo;
				String fileExt 	= "";
				int i = -1;
				if ((i = changephoto.lastIndexOf(".")) != -1) {
					fileExt = changephoto.substring(i); // 확장자만 추출
					changephoto = changephoto.substring(0, i); // 파일명만 추출
				}

				changephoto = curTimeStr + fileExt;
				// 파일명 변경
				File oldFile = new File(imagePath + System.getProperty("file.separator") + photo);
				File newFile = new File(imagePath + System.getProperty("file.separator") + changephoto);	
			    oldFile.renameTo(newFile);
			    
			    //리스트에 표시할 폭 200px의 썸네일을 만든다
				// 이 클래스에 변환할 이미지를 담는다.(이미지는 ParameterBlock을 통해서만 담을수 있다.)
				ParameterBlock pb = new ParameterBlock();
				pb.add(imagePath + System.getProperty("file.separator") + changephoto); // 
				
				RenderedOp rOp = JAI.create("fileload", pb);
		
				// 불러온 이미지를 BuffedImage에 담는다.
				BufferedImage bi = rOp.getAsBufferedImage();

				int tb_width = bi.getWidth(); // 폭
				int tb_height =  bi.getHeight(); // 너비
				
				// 페이지의 썸네일폭은 200으로 고정이므로 그림을 200에 맞춰서 보정한다.
				if(tb_width > 200) {
					tb_width = 200;
					tb_height = bi.getHeight() * 200 / bi.getWidth();
				}
				
				// thumb라는 이미지 버퍼를 생성, 버퍼의 사이즈는 200*(상대값)으로 설정.
				BufferedImage thumb = new BufferedImage(tb_width, tb_height, BufferedImage.TYPE_INT_RGB);
		
				// 버퍼사이즈 200*(상대값)으로  맞춰  썸네일을 그림
				Graphics2D g = thumb.createGraphics();
				g.drawImage(bi, 0, 0, tb_width, tb_height, null);
		
				//출력할 위치와 파일이름을 설정하고 섬네일 이미지를 생성한다. 저장하는 타입을 jpg로 설정.
				File file1 = new File(imagePath + "/sm" + changephoto);
				ImageIO.write(thumb, "jpg", file1);  
			}
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}		
		return changephoto;
	}
}
