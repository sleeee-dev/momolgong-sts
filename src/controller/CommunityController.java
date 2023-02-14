package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.JsonObject;

import model.CommunityDAO;
import model.domain.CommunityDTO;

@Controller
@RequestMapping("Community")
public class CommunityController {

	@Autowired
	public CommunityDAO comdao;

	//글쓰기
	@RequestMapping(value = "/write", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	public String write(Model model, @ModelAttribute CommunityDTO dto) throws Exception{
		System.out.println("insert()----------");
		System.out.println(dto);
		if(dto.getComContent() == null || dto.getComContent().trim().length() == 0 || dto.getComPw() == null || dto.getComPw().trim().length() == 0 || 
				dto.getComTitle() == null || dto.getComTitle().trim().length() == 0 || dto.getSubject() == null) {
			throw new RuntimeException("입력값이 충분하지 않습니다.");
		}
		
		model.addAttribute("dto", comdao.write(dto));

		return "forward:/comm/read.jsp"; //상세 글 페이지로 이동
	}



	//읽기
	@RequestMapping(value = "/view/{comNo}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	public String view(Model model, @PathVariable long comNo) throws SQLException {
		System.out.println("view()------------"+comNo);

		CommunityDTO dto = comdao.view(comNo, true);

		if(dto == null) {
			throw new RuntimeException("게시물이 존재하지 않습니다.");
		}else {
			model.addAttribute("dto", dto);
		}
		return "forward:/comm/read.jsp";
	}



	//목록 보기
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	public String list(Model model) throws Exception{
		System.out.println("list()------------");

		model.addAttribute("list", comdao.list());
		return "forward:/comm/allView.jsp";
	}



	//수정화면(read.jsp에서 수정버튼 클릭시 실행되는 로직)
	@RequestMapping(value = "/updateform", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	public String updateForm(Model model, @RequestParam("comNo") long comNo) throws SQLException{
		System.out.println("updateForm()---------"+comNo);

		if(comNo == 0) {
			throw new RuntimeException("게시물이 존재하지 않습니다.");
		}else {
			CommunityDTO dto = comdao.view(comNo, false);
			model.addAttribute("dto", dto);
		}
		return "update";
	}


	//수정
	@RequestMapping(value = "/update", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	public String update(Model model, @ModelAttribute CommunityDTO dto) throws SQLException{
		System.out.println("update()---------");
		if(dto.getComContent() == null || dto.getComContent().trim().length() == 0 || dto.getComPw() == null || dto.getComPw().trim().length() == 0 || 
				dto.getComTitle() == null || dto.getComTitle().trim().length() == 0 || dto.getSubject() == null) {
			throw new RuntimeException("입력값이 충분하지 않습니다.");
		}
		boolean result = comdao.update(dto);
		if(result) {
			model.addAttribute("comNo", dto.getComNo());
		}else {
			throw new RuntimeException("게시물이 존재하지 않거나 비밀번호가 틀렸습니다.");
		}

		return "redirect:/read.jsp"; //상세 글 페이지로 이동
	}
	
	
	
	//삭제
	@RequestMapping(value = "/delete", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	public String delete(@RequestParam("comNo") long comNo, @RequestParam("comPw") String comPw) {
		System.out.println("delete()---------");
		if(comNo == 0 || comPw == null || comPw.trim().length() == 0) {
			throw new RuntimeException("입력값이 충분하지 않습니다.");
		}
		boolean result = comdao.delete(comNo, comPw);
		if(!result) {
			throw new RuntimeException("게시물이 존재하지 않거나 비밀번호가 틀렸습니다.");
		}
		return "redirect:/list.jsp";
	}




	//예외 처리에 대한 중복 코드를 분리해서 예외처리 전담 메소드
	@ExceptionHandler(Exception.class)
	public String totalEx(Exception e, HttpServletRequest req) { 
		System.out.println("예외 처리 전담");
		e.printStackTrace(); //개발자 관점에서 필요한 정보, 서버 콘솔창에만 출력

		req.setAttribute("errorMsg", e.getMessage());
		return "forward:/error.jsp"; 
	}
	
	//이미지파일 업로드
	@RequestMapping(value = "fileUpload.do", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String fileUpload(HttpServletRequest req, HttpServletResponse res, MultipartHttpServletRequest multiFile) throws Exception{
		//Json 객체 생성
		JsonObject json = new JsonObject();
		//Json 객체 출력하기 위해 PrintWriter 생성
		PrintWriter printWriter = null;
		OutputStream out = null;
		//파일을 가져오기 위해 MultipartHttpServletRequest의 getFile 메서드 사용
		MultipartFile file = multiFile.getFile("upload");
		//파일이 비어있지 않고(비어있으면 null반환)
		if(file != null) {
			//파일 사이즈가 0보다 크고, 파일이름이 공백이 아닐때
			if(file.getSize()>0 && StringUtils.isNotBlank(file.getName())) {
				if(file.getContentType().toLowerCase().startsWith("image/")) {
					try {
						//파일 이름 
						String fileName = file.getName();
						//파일 내용
						byte[] bytes = file.getBytes();
						//파일이 실제 저장되는 경로
						String uploadPath = req.getServletContext().getRealPath("/img");
						//저장되는 파일에 경로 설정
						File uploadFile = new File(uploadPath);
						if(!uploadFile.exists()) {
							uploadFile.mkdirs();
						}
						//UUID : 사용고유성이 보장되는 id. 파일명 중복방지 랜덤생성
						fileName = UUID.randomUUID().toString();
						//업로드 경로+파일이름을 줘서 데이터를 서버에 전송
						uploadPath = uploadPath+"/"+fileName;
						out = new FileOutputStream(new File(uploadPath));
						out.write(bytes);
						
						//클라이언트에 추가
						printWriter = res.getWriter();
						res.setContentType("text/html");
						
						//파일 연결되는 url 주소 설정
						String fileUrl = req.getContextPath()+"/img/"+fileName;
						
						//생성된 json 객체를 이용해 파일 업로드+이름+주소를 ckEditor에 전송
						json.addProperty("uploaded", 1);
						json.addProperty("fileName", fileName);
						json.addProperty("url", fileUrl);
						
						printWriter.println(json);
					}catch(IOException e) {
						e.printStackTrace();
					}finally {
						if(out != null) {
							out.close();
						}
						if(printWriter != null) {
							printWriter.close();
						}
					}
				}
			}
		}
		return null;
	}
	
}