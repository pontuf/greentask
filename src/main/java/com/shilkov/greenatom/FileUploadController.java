package com.shilkov.greenatom;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shilkov.greenatom.storage.StorageFileNotFoundException;
import com.shilkov.greenatom.storage.StorageService;
import com.shilkov.greenatom.FileRepository;

@Controller
public class FileUploadController {

	private final StorageService storageService;
	@Autowired
	private FileRepository fileRepository;

	@Autowired
	public FileUploadController(StorageService storageService) {
		this.storageService = storageService;
	}

	@GetMapping("/get")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@RequestParam("id") int id) {

		Resource file = storageService.loadAsResource(fileRepository.findById(id).get(0).getName());
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@GetMapping("/userlist")
	@ResponseBody
	public List<String> getUserList() {
		
		UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		
		List<FileEntity> userlist = (List<FileEntity>) fileRepository.findAll();
		List<String> ul = new ArrayList<String>();
		for (int i = 0; i < userlist.size(); i++) {
			if (!ul.contains(userlist.get(i).getFileuser()))
				ul.add(userlist.get(i).getFileuser());
		}
		
		return ul;
	}

	@GetMapping("/")
	public String listUploadedFiles(Model model) throws IOException {

		/*
		 * model.addAttribute("files", storageService.loadAll() .map(path ->
		 * MvcUriComponentsBuilder .fromMethodName(FileUploadController.class,
		 * "serveFile", path.getFileName().toString()) .build().toUri().toString())
		 * .collect(Collectors.toList()));
		 */

		return "uploadForm";
	}

	/*
	 * @GetMapping("/files/{filename:.+}")
	 * 
	 * @ResponseBody public ResponseEntity<Resource> serveFile(@PathVariable String
	 * filename) {
	 * 
	 * Resource file = storageService.loadAsResource(filename); return
	 * ResponseEntity.ok() .header(HttpHeaders.CONTENT_DISPOSITION,
	 * "attachment; filename=\"" + file.getFilename() + "\"") .body(file); }
	 */

	@PostMapping("/")
	@ResponseBody
	public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		storageService.store(file);

		FileEntity n = new FileEntity();

		n.setName(file.getOriginalFilename());
		n.setDate(Date.valueOf(java.time.LocalDate.now()));
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		n.setFileuser(((UserDetails) principal).getUsername());

		fileRepository.save(n);
		return String.format("Your file id is %d", n.getId());
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}
