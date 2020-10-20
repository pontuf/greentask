package com.shilkov.greenatom;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shilkov.greenatom.storage.StorageFileNotFoundException;
import com.shilkov.greenatom.storage.StorageService;

@RestController
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
		Resource file;
		try {
			file = storageService.loadAsResource(fileRepository.findById(id).get(0).getName());
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}

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

	@GetMapping("/topdate")
	@ResponseBody
	public Date getTopDate() {
		HashMap<Date, Integer> datemap = new HashMap<>();
		Date topdate = Date.valueOf("1900-1-1");
		List<FileEntity> userlist = (List<FileEntity>) fileRepository.findAll();

		for (int i = 0; i < userlist.size(); i++) {
			if (!datemap.containsKey(userlist.get(i).getDate()))
				datemap.put(userlist.get(i).getDate(), 1);
			else
				datemap.replace(userlist.get(i).getDate(), datemap.get(userlist.get(i).getDate()) + 1);
		}
		int maxcount = Collections.max(datemap.values());
		for (Map.Entry<Date, Integer> entry : datemap.entrySet()) {
			if (entry.getValue().equals(maxcount))
				topdate = entry.getKey();
		}

		return topdate;
	}

	// Method for sorting the TreeMap based on values
	public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
		Comparator<K> valueComparator = new Comparator<K>() {
			@Override
			public int compare(K k1, K k2) {
				int compare = map.get(k1).compareTo(map.get(k2));
				if (compare == 0)
					return 1;
				else
					return compare;
			}
		};

		Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
		sortedByValues.putAll(map);
		return sortedByValues;
	}

	@GetMapping("/sorteduserlist")
	@ResponseBody
	public Map<String, Integer> getSortedUserList() {
		TreeMap<String, Integer> sul = new TreeMap<>();

		List<FileEntity> userlist = (List<FileEntity>) fileRepository.findAll();

		for (int i = 0; i < userlist.size(); i++) {
			if (!sul.containsKey(userlist.get(i).getFileuser()))
				sul.put(userlist.get(i).getFileuser(), 1);
			else
				sul.replace(userlist.get(i).getFileuser(), sul.get(userlist.get(i).getFileuser()) + 1);
		}

		return sortByValues(sul);
	}

	@GetMapping("/")
	public String listUploadedFiles(Model model) throws IOException {
		return "Authentication was successful.";
	}

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
