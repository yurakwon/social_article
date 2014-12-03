package bean;

import java.sql.Timestamp;

import org.json.simple.JSONObject;

public class Member implements java.io.Serializable {
	private static final long serialVersionUID = 2193897931951340673L;
	
	private static final String[][] genders = {{"M", "남성"}, {"F", "여성"}};

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
	private int 		level;
		
	public Member() {
	}

	public Member(String userid, String pwd, Timestamp registerdate, String lastname, String firstname, 
				  String nickname, String profilephoto, String gender, String email, String introduce, 
				  String website, int level) 
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
		this.level 			= level;
	}

	public String getUserid() { return userid; }
	public void setUserid(String userid) { this.userid = userid; }
	
	public String getPwd() { return pwd; }
	public void setPwd(String pwd) { this.pwd = pwd; }
	
	public Timestamp getRegisterdate() { return registerdate; }
	public void setRegisterdate(Timestamp registerdate) { this.registerdate = registerdate; }

	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }

	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }

	public String getNickname() { return nickname; }
	public void setNickname(String nickname) { this.nickname = nickname; }

	public String getProfilephoto() { return profilephoto; }
	public void setProfilephoto(String profilephoto) {  this.profilephoto = profilephoto; }

	public String getGender() { return gender; }
	public void setGender(String gender) { this.gender = gender; }
	public String getGenderStr() { return (gender.equals("M")) ? "남성" : "여성"; }
	public String checkGender(String genderName) { return (genderName.equals(gender)) ? "checked" : ""; }
	public String[][] getGenders() { return genders; }
	
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	
	public String getIntroduce() { return introduce; }
	public void setIntroduce(String introduce) { this.introduce = introduce; }

	public String getWebsite() { return website; }
	public void setWebsite(String website) { this.website = website; }

	public int getLevel() { return level; }
	public void setLevel(int level) { this.level = level; }
	
	public JSONObject UsertoJson() {
		JSONObject js = new JSONObject();
		
		js.put("userid", this.userid);
		js.put("registerdate", this.registerdate.toString());
		js.put("lastname", this.lastname);
		js.put("firstname", this.firstname);
		js.put("nickname", this.nickname);
		js.put("profilephoto", this.profilephoto);
		js.put("gender", this.gender);
		js.put("email", this.email);
		js.put("introduce", this.introduce);
		js.put("website", this.website);
		
		return js;
	}
}
