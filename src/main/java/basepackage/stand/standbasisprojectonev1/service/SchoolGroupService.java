package basepackage.stand.standbasisprojectonev1.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import basepackage.stand.standbasisprojectonev1.model.SchoolGroup;
import basepackage.stand.standbasisprojectonev1.payload.SchoolGroupRequest;
import basepackage.stand.standbasisprojectonev1.repository.SchoolgroupRepository;
import basepackage.stand.standbasisprojectonev1.util.CommonActivity;


@Service
public class SchoolGroupService {

private static final Logger logger = LoggerFactory.getLogger(SchoolGroupService.class);
	
	@Autowired
    private SchoolgroupRepository groupRepository;
	
	public List<SchoolGroup> findAll() {
		
		return groupRepository.findAll();
	}
	
	public SchoolGroup findSchoolGroup(long id) {
		
		Optional<SchoolGroup> sch = groupRepository.findById(id);
		if (sch.isPresent()) {
			SchoolGroup schval = sch.get();
			
			return schval;
		}
		return null;
	}
	
	public Map<String, Object> getPaginatedGroupSchools(int page, int size, String query) {
		CommonActivity.validatePageNumberAndSize(page, size);

        // Retrieve group of Schools
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<SchoolGroup> schs = null;
       
        if ( query.equals("") || query == null ) {
        	schs = groupRepository.findAll(pageable);
        }
        else {
        	schs = groupRepository.filter("%"+ query + "%",  pageable);
        }

        if(schs.getNumberOfElements() == 0) {
        	Map<String, Object> responseEmpty = new HashMap<>();
        	responseEmpty.put("schoolgroups", Collections.emptyList());
        	responseEmpty.put("currentPage", schs.getNumber());
        	responseEmpty.put("totalItems", schs.getTotalElements());
        	responseEmpty.put("totalPages", schs.getTotalPages());
        	
        	return responseEmpty;
        }
        
        List<SchoolGroup> scharray = new ArrayList<SchoolGroup>();
        
        scharray = schs.getContent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("schoolgroups", scharray);
        response.put("currentPage", schs.getNumber());
        response.put("totalItems", schs.getTotalElements());
        response.put("totalPages", schs.getTotalPages());
        response.put("isLast", schs.isLast());
        
       // long active = 1; long inactive = 0;
      /*  long sriSchools = schRepository.countBySri(active);
        long nonSriSchools = schRepository.countBySri(inactive);
        long inactiveSchools = schRepository.countByStatus(inactive);*/
        
      
        return response;
    }
	
	public SchoolGroup update(SchoolGroupRequest schgroupRequest,long id) {
		Optional<SchoolGroup> existing = groupRepository.findById(id);
		if (existing.isPresent()) {
			SchoolGroup schval1 = existing.get();
			CommonActivity.copyNonNullProperties(schgroupRequest, schval1);
			return groupRepository.save(schval1);
		}	   
		
		return null;
	}
	
	public SchoolGroup delete(Long id) {
		Optional<SchoolGroup> sch = groupRepository.findById(id);
		if (sch.isPresent()) {
			SchoolGroup schval = sch.get();
			schval.setStatus(-1);
			groupRepository.save(schval);
			return schval;
		}
		return null;
	}
	
	
}
