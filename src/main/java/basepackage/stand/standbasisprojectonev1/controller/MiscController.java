package basepackage.stand.standbasisprojectonev1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.model.Student;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.service.ClassService;
import basepackage.stand.standbasisprojectonev1.service.SchoolGroupService;
import basepackage.stand.standbasisprojectonev1.service.StudentService;
import basepackage.stand.standbasisprojectonev1.service.SubjectService;
import basepackage.stand.standbasisprojectonev1.service.TeacherService;

@RestController
@RequestMapping("/api/misc")
public class MiscController {
	
	// for all general data manipulation in our software
	 @Autowired
	 SchoolGroupService schoolgroupservice;
	 
	 @Autowired
	 StudentService studentservice;
	 
	 @Autowired
	 TeacherService teacherservice;
	 
	 @Autowired
	 ClassService classservice;
	 
	 @Autowired
	 SubjectService subjectservice;
	 
	 @GetMapping("/allSchoolGroups")
	 public ResponseEntity<?> findSchoolGroups() {
		 List<SchoolGroup> grouplist = schoolgroupservice.findAll();
		 return ResponseEntity.ok().body(new ApiContentResponse<SchoolGroup>(true, "List of school groups gotten successfully.", grouplist));
	 }
	 
	 @GetMapping("/allClasses/{id}")
	 public ResponseEntity<?> findClasses(@PathVariable(value = "id") Long id) {
		 List<ClassStream> classlist = classservice.findAllBySchool(id);
		 return ResponseEntity.ok().body(new ApiContentResponse<ClassStream>(true, "List of classes gotten successfully.", classlist));
	 }
	 
	 @GetMapping("/allPupils/{id}")
	 public ResponseEntity<?> findPupils(@PathVariable(value = "id") Long id) {
		 List<Student> studentlist = studentservice.findAllBySchool(id);
		 return ResponseEntity.ok().body(new ApiContentResponse<Student>(true, "List of students gotten successfully.", studentlist));	
	 }
	 
	 @GetMapping("/allTeachers/{id}")
	 public ResponseEntity<?> findTeachers(@PathVariable(value = "id") Long id) {
		 List<Teacher> teacherlist = teacherservice.findAllBySchool(id);
		 return ResponseEntity.ok().body(new ApiContentResponse<Teacher>(true, "List of teachers gotten successfully.", teacherlist));	
	 }
	 
	 @GetMapping("/allSubjects")
	 public ResponseEntity<?> findSubjects() {
		 List<Subject> subjectlist = subjectservice.findAll();
		 return ResponseEntity.ok().body(new ApiContentResponse<Subject>(true, "List of subjects gotten successfully.", subjectlist));	
	 }
	 
	 
	 
}

