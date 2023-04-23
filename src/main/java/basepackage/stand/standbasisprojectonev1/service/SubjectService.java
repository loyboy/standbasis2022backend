package basepackage.stand.standbasisprojectonev1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.Subject;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.repository.SubjectRepository;
import basepackage.stand.standbasisprojectonev1.repository.TeacherRepository;

@Service
public class SubjectService {
	
	private static final Logger logger = LoggerFactory.getLogger(SubjectService.class);
	
	@Autowired
    private SubjectRepository subRepository;
	
	@Autowired		
    private TeacherRepository teaRepository;
	
	/*public Subject createSubject(SubjectRequest subRequest) {
       // Subject sub = new Subject( subRequest.getId(), subRequest.getName() , subRequest.getCategory());
       // logger.info("Subject {} has been created. ", subRequest.getName() );
       // return subRepository.save(sub);
    }*/

	public List<Subject> findAll() {
		
		return subRepository.findAll();
	}
	
	public List<Subject> findAllByTeacher(Long id) {		
		Optional<Teacher> tea = teaRepository.findById(id);
		if (tea.isPresent()) {
			Teacher teaval = tea.get();			
			return subRepository.findByTeacher(teaval);
		}
		return null;		
	}
}
