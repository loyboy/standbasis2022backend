package basepackage.stand.standbasisprojectonev1.service;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import basepackage.stand.standbasisprojectonev1.model.EvaluationValues;
import basepackage.stand.standbasisprojectonev1.model.EvaluatorCore;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.User;
import basepackage.stand.standbasisprojectonev1.repository.EvalCoreRepository;
import basepackage.stand.standbasisprojectonev1.repository.EvaluationRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.UserRepository;

@Service
public class EvaluationService<T> {

	@Autowired		
    private UserRepository userRepository;
	
	@Autowired		
    private SchoolRepository schRepository;
	
	@Autowired
	private EvaluationRepository evaRepository;
	
	@Autowired
	private EvalCoreRepository evacoreRepository;
	
	public List<EvaluationValues> findRounds(Long school, Long user, Boolean complete) {		
		Optional<School> sch = schRepository.findById(school); 
		Optional<User> u = userRepository.findById(user);
		
		if ( sch.isPresent() && u.isPresent() ) {
			
			School schval = sch.get();
			User uval = u.get();
			
			List<EvaluationValues> evlist = evaRepository.findRoundsByUser(schval, uval);
			
			List<EvaluationValues> otherList = evlist.stream().filter( distinctByKey( p -> p.getRoundId() ) ).collect( Collectors.toList() );
			
			if (complete.equals(true)) {
				List<EvaluationValues> otherListComplete = otherList.stream().filter( p -> p.getComplete() != null ).collect( Collectors.toList() );
				return otherListComplete;
			}
			
			return otherList;
		}
		return null;		
	}
	
	public List<EvaluationValues> findSchools(Long user) {
		Optional<User> u = userRepository.findById(user);
		if ( u.isPresent() ) {
			
			User uval = u.get();
			
			List<EvaluationValues> evlist = evaRepository.findRoundsByUser2(uval);
			
			List<EvaluationValues> otherList = evlist.stream().filter( distinctByKey(p -> p.getSchool().getSchId() ) ).collect( Collectors.toList() );
			
			return otherList;
		}
		return null;		
	}
	
	
	public List<EvaluationValues> findSchoolGroups(Long user) {
		Optional<User> u = userRepository.findById(user);
		if ( u.isPresent() ) {
			
			User uval = u.get();
			
			List<EvaluationValues> evlist = evaRepository.findRoundsByUser2(uval);
			
			List<EvaluationValues> otherList = evlist.stream().filter( distinctByKey(p -> p.getSchool().getOwner() ) ).collect( Collectors.toList() );
			
			return otherList;
		}
		return null;		
	}
	
	public List<EvaluationValues> findSchoolZones(Long user) {
		Optional<User> u = userRepository.findById(user);
		if ( u.isPresent() ) {
			
			User uval = u.get();
			
			List<EvaluationValues> evlist = evaRepository.findRoundsByUser2(uval);
			
			List<EvaluationValues> otherList = evlist.stream().filter( distinctByKey(p -> p.getSchool().getJurisdiction() ) ).collect( Collectors.toList() );
			
			return otherList;
		}
		return null;		
	}
	
	public String createRound(Long school, Long user) {	
		long unixTimestamp = Instant.now().getEpochSecond();	
		String roundId = user.toString() + "-" + unixTimestamp;
		List<EvaluatorCore> cloned = evacoreRepository.findAll();
		Optional<School> sch = schRepository.findById(school); 
		Optional<User> u = userRepository.findById(user);
		
		List<EvaluationValues> totalEvaluations = findRounds(school, user, false);

		for (EvaluatorCore ee : cloned) {	
			EvaluationValues ev = new EvaluationValues();
			ev.setEvaluation(ee);
			ev.setPerformance(null);
			ev.setValue(null);
			ev.setSchool(sch.get());
			ev.setUser(u.get());
			
			ev.setRoundId( roundId );
			ev.setRound(  totalEvaluations.size() + 1 );
			
			evaRepository.save(ev);
		}
		
		return roundId;
	}
	
	public Map<String, Object> fetchRound(String id) {	
		List<EvaluationValues> found = evaRepository.findByRoundId(id);
		
		List<EvaluationValues> coreprocesss = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Core Processes")).collect(Collectors.toList());
		List<EvaluationValues> instrutor_resource = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Instructor Resource")).collect(Collectors.toList());
		List<EvaluationValues> learning_env = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Learning Environment")).collect(Collectors.toList());
		List<EvaluationValues> total_student = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Total Student Development")).collect(Collectors.toList());
		List<EvaluationValues> sustainability = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Sustainability")).collect(Collectors.toList());
		List<EvaluationValues> academic = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Academic Performance")).collect(Collectors.toList());
		List<EvaluationValues> sshe = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Safety, Health, Environment, Security")).collect(Collectors.toList());
		
		Map<String, Object> response = new HashMap<>();
		response.put("round", id);
	    response.put("coreprocesss", coreprocesss);
	    response.put("instrutor_resource", instrutor_resource);
	    response.put("learning_env", learning_env);
	    response.put("total_student", total_student);
	    response.put("sustainability", sustainability);
	    response.put("academic", academic);
	    response.put("sshe", sshe);
	    return response;	    
	}
	
	public Map<String, Object> fetchRoundByGroup(String name) {	
		List<EvaluationValues> temp = evaRepository.findByRoundByGroup(name);
		
		List<EvaluationValues> found = temp.stream().filter( p -> p.getComplete() != null ).collect( Collectors.toList() );		
		
		List<EvaluationValues> coreprocesss = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Core Processes")).collect(Collectors.toList());
		List<EvaluationValues> instrutor_resource = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Instructor Resource")).collect(Collectors.toList());
		List<EvaluationValues> learning_env = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Learning Environment")).collect(Collectors.toList());
		List<EvaluationValues> total_student = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Total Student Development")).collect(Collectors.toList());
		List<EvaluationValues> sustainability = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Sustainability")).collect(Collectors.toList());
		List<EvaluationValues> academic = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Academic Performance")).collect(Collectors.toList());
		List<EvaluationValues> sshe = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Safety, Health, Environment, Security")).collect(Collectors.toList());
		
		Map<String, Object> response = new HashMap<>();
		
	    response.put("coreprocesss", coreprocesss);
	    response.put("instrutor_resource", instrutor_resource);
	    response.put("learning_env", learning_env);
	    response.put("total_student", total_student);
	    response.put("sustainability", sustainability);
	    response.put("academic", academic);
	    response.put("sshe", sshe);
	    return response;	    
	}
	
	public Map<String, Object> fetchRoundByZone(String name) {	
		List<EvaluationValues> temp = evaRepository.findByRoundByZone(name);
		
		List<EvaluationValues> found = temp.stream().filter( p -> p.getComplete() != null ).collect( Collectors.toList() );		
		
		List<EvaluationValues> coreprocesss = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Core Processes")).collect(Collectors.toList());
		List<EvaluationValues> instrutor_resource = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Instructor Resource")).collect(Collectors.toList());
		List<EvaluationValues> learning_env = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Learning Environment")).collect(Collectors.toList());
		List<EvaluationValues> total_student = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Total Student Development")).collect(Collectors.toList());
		List<EvaluationValues> sustainability = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Sustainability")).collect(Collectors.toList());
		List<EvaluationValues> academic = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Academic Performance")).collect(Collectors.toList());
		List<EvaluationValues> sshe = found.stream().filter(ev -> ev.getEvaluation().getArea().equals("Safety, Health, Environment, Security")).collect(Collectors.toList());
		
		Map<String, Object> response = new HashMap<>();		
	    response.put("coreprocesss", coreprocesss);
	    response.put("instrutor_resource", instrutor_resource);
	    response.put("learning_env", learning_env);
	    response.put("total_student", total_student);
	    response.put("sustainability", sustainability);
	    response.put("academic", academic);
	    response.put("sshe", sshe);
	    return response;	    
	}
	
	
	public List<EvaluationValues> updateRound(List<EvaluationValues> myObjects) {
        return evaRepository.saveAll(myObjects);
    }
	
	// Helper method to filter distinct by key
	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) 
	{
	    Map<Object, Boolean> map = new ConcurrentHashMap<>();
	    return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	
	
}
