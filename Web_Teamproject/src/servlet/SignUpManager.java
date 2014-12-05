package servlet;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.sql.Timestamp;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Member;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import dao.MemberDAO;


public class SignUpManager extends HttpServlet {

	
	private static final long serialVersionUID = 1L;

	public SignUpManager() {
		
	}
	
	//가입 수정판단,사진이 저장될 장소
	public static boolean SignUp(MultipartRequest multi, HttpServletResponse response, Member user, String imagePath, String state) {
		boolean ret = false;

		user.setProfilephoto(UploadPhoto(multi, response, imagePath));
		System.out.println("사진여부 : " + user.getProfilephoto());
		try {
			if(state.equals("signup")) {
				System.out.println("가입모드");
				ret = MemberDAO.create(user);
			}
			else if(state.equals("update")) {
				System.out.println("수정모드");
				ret = MemberDAO.update(user);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return ret;
	}
	
	//업로드사진을 저장함수
	private static String UploadPhoto(MultipartRequest multi, HttpServletResponse response, String imagePath) {
		String userid 		= null;	
		String profilephoto = null;	
		String 	curTimeStr 	= Long.toString(System.currentTimeMillis()); //use Unix Time
	
		try {
			// 이미지 업로드		
			userid 		= multi.getParameter("userid");
			profilephoto = multi.getParameter("profilephoto");
			
			// 업로드 된 이미지 이름 
			Enumeration files 	= multi.getFileNames();
			String 		file 	= (String) files.nextElement();
			
			// 파일을 업로드 했다면 썸네일을 만든다 
			if((multi.getOriginalFileName(file)) != null) {
				// 파일을 업로드 했으면 파일명을 얻음
				profilephoto = multi.getOriginalFileName(file);
				// 파일명 변경준비
				String changephoto 	= profilephoto;
				String fileExt 		= "";
				int i = -1;
				if ((i = changephoto.lastIndexOf(".")) != -1) {
					fileExt = changephoto.substring(i); // 확장자만 추출
					changephoto = changephoto.substring(0, i); // 파일명만 추출
				}
				// 사진명을 time_userid로 설정
				changephoto = curTimeStr + "_" + userid;
				// 파일명 변경
				File oldFile = new File(imagePath + System.getProperty("file.separator") + profilephoto);
				File newFile = new File(imagePath + System.getProperty("file.separator") + changephoto + fileExt);	
			    oldFile.renameTo(newFile);			
				// 썸네일 파일명 준비용
				profilephoto = curTimeStr + "_" + userid + fileExt;
				// 이 클래스에 변환할 이미지를 담는다.(이미지는 ParameterBlock을 통해서만 담을수 있다.)
				ParameterBlock pb = new ParameterBlock();
				pb.add(imagePath + System.getProperty("file.separator") + profilephoto);
				
				RenderedOp rOp = JAI.create("fileload", pb);
		
				// 불러온 이미지를 BuffedImage에 담는다.
				BufferedImage bi = rOp.getAsBufferedImage();
				// thumb라는 이미지 버퍼를 생성, 버퍼의 사이즈는 100*100으로 설정.
				BufferedImage thumb = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		
				// 버퍼사이즈 100*100으로  맞춰  썸네일을 그림
				Graphics2D g = thumb.createGraphics();
				g.drawImage(bi, 0, 0, 100, 100, null);
		
				// 출력할 위치와 파일이름을 설정하고 섬네일 이미지를 생성한다. 저장하는 타입을 jpg로 설정.
				File file1 = new File(imagePath + "/sm" + profilephoto);
				ImageIO.write(thumb, "jpg", file1);
			}
		} catch (Exception e) {
			return null;
		}		
		return profilephoto;
	}
}
