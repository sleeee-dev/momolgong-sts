package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import model.CommunityDAO;
import model.NoticeDAO;
import model.StudyGroupDAO;
import model.StudyListDAO;
import model.StudyMembersDAO;

@Controller
@RequestMapping("camstudy")
public class StudyController {
	
	@Autowired
	public StudyMembersDAO memdao;
	@Autowired
	public StudyListDAO listdao;
	@Autowired
	public StudyGroupDAO groupdao;
	@Autowired
	public NoticeDAO notdao;
	@Autowired
	public CommunityDAO comdao;
	
	@PostMapping(value = "/auth/register", produces = "application/json; charset=UTF-8")
	protected String deptInsert() {
		
		memdao.insertMember();
		
		return "회원가입성공";
	}
}