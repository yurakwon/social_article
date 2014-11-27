package bean;

import java.sql.Timestamp;

import org.json.simple.JSONObject;

public class Member implements java.io.Serializable {
	private static final long serialVersionUID = 2193897931951340673L;
	
	private static final String[][] genders = {{"M", "남자"}, {"F", "여자"}};
	private static final String[][] infoopen = {{"Y", "공개"}, {"N", "비공개"}};

	private String 		userid;
	private String 		pwd;
	private Timestamp 	registerdate;
	private String 		lastname;
	private String 		firstname;
	private String 		nickname;
	private String 		profilephoto;
	private String 		gender;
	private String 		email;
	private String 		introduce;
	private String 		website;
	private String 		info;
	private int 		level;
		
	// No-arg constructor 가 있어야 한다.
	public Member() {
	}

	public Member(String userid, String pwd, Timestamp registerdate, String lastname, String firstname, 
				  String nickname, String profilephoto, String gender, String email, String introduce, 
				  String website,
				  String info, int level) 
	{
		super();
		this.userid 		= userid;
		this.pwd 			= pwd;
		this.registerdate 	= registerdate;
		this.lastname 		= lastname;
		this.firstname 		= firstname;
		this.nickname 		= nickname;
		this.profilephoto 	= profilephoto;
		this.gender 		= gender;
		this.email 			= email;
		this.introduce 		= introduce;
		this.website 		= website;
		this.info 			= info;
		this.level 			= level;
	}

	// getter & setter 가 있어야 한다. (Eclipse 에서 자동 생성 가능)
	/* 아이디 */
	public String getUserid() { return userid; }
	public void setUserid(String userid) { this.userid = userid; }
	
	/* 비밀번호 */
	public String getPwd() { return pwd; }
	public void setPwd(String pwd) { this.pwd = pwd; }
	
	/* 가입일 */
	public Timestamp getRegisterdate() { return registerdate; }
	public void setRegisterdate(Timestamp registerdate) { this.registerdate = registerdate; }

	/* 성 */
	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }
	
	/*이름 */
	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

	/* 별명 */
	public String getNickname() { return nickname; }
	public void setNickname(String nickname) { this.nickname = nickname; }

	/* 프로필 사진 */
	public String getProfilephoto() { return profilephoto; }
	public void setProfilephoto(String profilephoto) {  this.profilephoto = profilephoto; }

	/* 성별*/
	public String getGender() { return gender; }
	public void setGender(String gender) { this.gender = gender; }
	public String getGenderStr() { return (gender.equals("M")) ? "남자" : "여자"; }
	public String checkGender(String genderName) { return (genderName.equals(gender)) ? "checked" : ""; }
	public String[][] getGenders() { return genders; }
	
	/* 이메일 */
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	
	/* 소개 */
	public String getIntroduce() { return introduce; }
	public void setIntroduce(String introduce) { this.introduce = introduce; }

	/* 웹사이트 */
	public String getWebsite() { return website; }
	public void setWebsite(String website) { this.website = website; }

	/* 정보공개여부 */
	public String getInfo() { return info; }
	public void setInfo(String info) { this.info = info; }
	public String getInfoStr() { return (info.equals("Y")) ? "공개" : "비공개"; }
	public String checkInfo(String infoselect) { return (infoselect.equals(info)) ? "checked" : ""; }
	public String[][] getInfoopen() { return infoopen; }
	
	/* 회원레벨 */
	public int getLevel() { return level; }
	public void setLevel(int level) { this.level = level; }
	
	public JSONObject UsertoJson() {
		JSONObject js = new JSONObject();
		
		js.put("userid", this.userid);
		//js.put("pwd", this.pwd);
		js.put("registerdate", this.registerdate.toString());
		js.put("lastname", this.lastname);
		js.put("firstname", this.firstname);
		js.put("nickname", this.nickname);
		js.put("profilephoto", this.profilephoto);
		js.put("gender", this.gender);
		js.put("email", this.email);
		js.put("introduce", this.introduce);
		js.put("website", this.website);
		js.put("info", this.info);
		//js.put("level", this.level);
		
		return js;
	}
}
