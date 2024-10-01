package basepackage.stand.standbasisprojectonev1.controller;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import basepackage.stand.standbasisprojectonev1.model.Attendance;
import basepackage.stand.standbasisprojectonev1.model.AttendanceActivity;
import basepackage.stand.standbasisprojectonev1.model.AttendanceManagement;
import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.EventManager;
import basepackage.stand.standbasisprojectonev1.model.Rowcall;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.Student;
import basepackage.stand.standbasisprojectonev1.model.User;
import basepackage.stand.standbasisprojectonev1.payload.ApiContentResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiDataResponse;
import basepackage.stand.standbasisprojectonev1.payload.ApiResponse;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.AttendanceComposite;
import basepackage.stand.standbasisprojectonev1.payload.onboarding.RowcallRequest;
import basepackage.stand.standbasisprojectonev1.repository.EventManagerRepository;
import basepackage.stand.standbasisprojectonev1.repository.SchoolRepository;
import basepackage.stand.standbasisprojectonev1.repository.TeacherRepository;
import basepackage.stand.standbasisprojectonev1.repository.UserRepository;
import basepackage.stand.standbasisprojectonev1.security.UserPrincipal;
import basepackage.stand.standbasisprojectonev1.service.AttendanceActivityService;
import basepackage.stand.standbasisprojectonev1.service.AttendanceManagementService;
import basepackage.stand.standbasisprojectonev1.service.AttendanceService;
import basepackage.stand.standbasisprojectonev1.service.EnrollmentService;
import basepackage.stand.standbasisprojectonev1.service.MneService;
import basepackage.stand.standbasisprojectonev1.service.UserService;
import basepackage.stand.standbasisprojectonev1.util.AppConstants;
import basepackage.stand.standbasisprojectonev1.util.FileUploadUtil;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

	@Autowired
	AttendanceService service;

	@Autowired
	AttendanceActivityService serviceActivity;

	@Autowired
	AttendanceManagementService serviceManagement;

	@Autowired
	UserService serviceUser;

	@Autowired
	MneService mneService;

	@Autowired
	EnrollmentService serviceEnrollment;

	@Autowired
	private EventManagerRepository eventRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SchoolRepository schRepository;

	@Autowired
	private TeacherRepository teaRepository;

	private final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@GetMapping
	public ResponseEntity<?> getAttendances() {
		List<Attendance> list = service.findAll();
		return ResponseEntity.ok()
				.body(new ApiContentResponse<Attendance>(true, "List of Attendances gotten successfully.", list));
	}

	@GetMapping("/paginateAttendanceManagement")
	public ResponseEntity<?> getPaginatedAttendancesManagement(
			@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			@RequestParam(value = "school", required = false) Optional<Long> school,
			@RequestParam(value = "class", required = false) Optional<Long> classid,
			@RequestParam(value = "calendar", required = false) Optional<Long> calendar,
			@RequestParam(value = "teacher", required = false) Optional<Long> teacher, //
			@RequestParam(value = "attendancedone", required = false) Optional<Integer> done,
			@RequestParam(value = "datefrom", required = false) Optional<Timestamp> datefrom,
			@RequestParam(value = "dateto", required = false) Optional<Timestamp> dateto) {

		try {

			Map<String, Object> response = serviceManagement.getPaginatedTeacherAttendances(page, size, query,
					schoolgroup, school, classid, calendar, teacher, done, datefrom, dateto);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Error encountered."));
		}

	}

	@GetMapping("/paginateAttendanceActivity")
	public ResponseEntity<?> getPaginatedAttendancesActivity(
			@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			@RequestParam(value = "school", required = false) Optional<Long> school,
			@RequestParam(value = "class", required = false) Optional<Long> classid,
			@RequestParam(value = "calendar", required = false) Optional<Long> calendar,
			@RequestParam(value = "teacher", required = false) Optional<Long> teacher,
			@RequestParam(value = "subject", required = false) Optional<Long> subject,
			@RequestParam(value = "status", required = false) Optional<String> status,
			@RequestParam(value = "slip", required = false) Optional<Integer> slip,
			@RequestParam(value = "attendance", required = false) Optional<Long> attendance,
			@RequestParam(value = "datefrom", required = false) Optional<Timestamp> datefrom,
			@RequestParam(value = "dateto", required = false) Optional<Timestamp> dateto) {

		Map<String, Object> response = serviceActivity.getPaginatedTeacherAttendances(page, size, query, schoolgroup,
				school, classid, calendar, teacher, subject, status, slip, attendance, datefrom, dateto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/paginateTeachers")
	public ResponseEntity<?> getPaginatedTeacherAttendances(
			@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			@RequestParam(value = "school", required = false) Optional<Long> school,
			@RequestParam(value = "class", required = false) Optional<Long> classid,
			@RequestParam(value = "calendar", required = false) Optional<Long> calendar,
			@RequestParam(value = "teacher", required = false) Optional<Long> teacher,
			@RequestParam(value = "subject", required = false) Optional<Long> subject,
			@RequestParam(value = "status", required = false) Optional<Integer> status,
			@RequestParam(value = "datefrom", required = false) Optional<String> datefrom,
			@RequestParam(value = "dateto", required = false) Optional<String> dateto) {
		try {
			Date datefrom1 = null;
			Date dateto1 = null;

			if (datefrom.isPresent()) {
				try {
					datefrom1 = DATE_TIME_FORMAT.parse(datefrom.get());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if (dateto.isPresent()) {
				try {
					dateto1 = DATE_TIME_FORMAT.parse(dateto.get());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			Map<String, Object> response = service.getPaginatedTeacherAttendances(page, size, query, schoolgroup,
					school, classid, calendar, teacher, subject, status, datefrom1, dateto1);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Error encountered."));
		}
	}

	@GetMapping("/classToday")
	public ResponseEntity<?> getTeacherClassAttendanceToday(
			@RequestParam(value = "teacher", required = false) Optional<Long> teacher,
			@RequestParam(value = "today", required = false) Optional<String> today) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date parsedDate;
		try {
			parsedDate = dateFormat.parse(today.get());

			Map<String, Object> response = service.getTeacherClassesToday(teacher, parsedDate);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,
					"You do not have access to this resource because your Bearer token is either expired or not set."));
		}
	}

	@GetMapping("/teachers")
	public ResponseEntity<?> getTeacherAttendances(
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			@RequestParam(value = "school", required = false) Optional<Long> school,
			@RequestParam(value = "class", required = false) Optional<Long> classid,
			@RequestParam(value = "calendar", required = false) Optional<Long> calendar,
			@RequestParam(value = "teacher", required = false) Optional<Long> teacher,
			@RequestParam(value = "subject", required = false) Optional<Long> subject,
			@RequestParam(value = "datefrom", required = false) Optional<Timestamp> datefrom,
			@RequestParam(value = "dateto", required = false) Optional<Timestamp> dateto) {

		Map<String, Object> response = service.getOrdinaryTeacherAttendances(query, schoolgroup, school, classid,
				calendar, teacher, subject, datefrom, dateto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/exportTeachers")
	public ResponseEntity<?> getExportTeacherAttendances(
			@RequestParam(value = "school", required = false) Optional<Long> school,
			@RequestParam(value = "class", required = false) Optional<Long> classid,
			@RequestParam(value = "calendar", required = false) Optional<Long> calendar,
			@RequestParam(value = "teacher", required = false) Optional<Long> teacher,
			@RequestParam(value = "subject", required = false) Optional<Long> subject,
			@RequestParam(value = "attendancedone", required = false) Optional<Integer> done,
			@RequestParam(value = "datefrom", required = false) Optional<Timestamp> datefrom,
			@RequestParam(value = "dateto", required = false) Optional<Timestamp> dateto) {

		try {
			Map<String, Object> attManageResponse = serviceManagement.getExportOrdinaryTeacherAttendances(school,
					classid, calendar, teacher, subject, done, datefrom, dateto);

			// Map<String, Object> newResponse = new HashMap<>();
			return new ResponseEntity<>(attManageResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Error encountered."));
		}
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/exportRowcalls")
	public ResponseEntity<?> getExportRowcallAttendances(
			@RequestParam(value = "school", required = false) Optional<Long> school,
			@RequestParam(value = "class", required = false) Optional<Long> classid,
			@RequestParam(value = "calendar", required = false) Optional<Long> calendar,
			@RequestParam(value = "teacher", required = false) Optional<Long> teacher,
			@RequestParam(value = "student", required = false) Optional<Long> student,
			@RequestParam(value = "subject", required = false) Optional<Long> subject,
			@RequestParam(value = "datefrom", required = false) Optional<Timestamp> datefrom,
			@RequestParam(value = "dateto", required = false) Optional<Timestamp> dateto) {

		try {
			Map<String, Object> attResponse = service.getExportOrdinaryStudentAttendances(school, classid, calendar,
					teacher, student, subject, datefrom, dateto);

			// Map<String, Object> newResponse = new HashMap<>();
			return new ResponseEntity<>(attResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Error encountered."));
		}
	}

	@GetMapping("/flagTeachers")
	public ResponseEntity<?> getFlagTeacherAttendances(
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			@RequestParam(value = "school", required = false) Optional<Long> school,
			@RequestParam(value = "class", required = false) Optional<Long> classid,
			@RequestParam(value = "calendar", required = false) Optional<Long> calendar,
			@RequestParam(value = "teacher", required = false) Optional<Long> teacher,
			@RequestParam(value = "subject", required = false) Optional<Long> subject,
			@RequestParam(value = "student", required = false) Optional<Long> student,
			@RequestParam(value = "datefrom", required = false) Optional<Timestamp> datefrom,
			@RequestParam(value = "dateto", required = false) Optional<Timestamp> dateto) {

		Map<String, Object> newResponse = mneService.getOrdinaryAttendanceFlags(query, schoolgroup, school, classid,
				calendar, teacher, subject, student, datefrom, dateto);

		return new ResponseEntity<>(newResponse, HttpStatus.OK);
	}

	@GetMapping("/paginateStudents")
	public ResponseEntity<?> getPaginatedStudentAttendances(
			@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			@RequestParam(value = "school", required = false) Optional<Long> school,
			@RequestParam(value = "class", required = false) Optional<Long> classid,
			@RequestParam(value = "calendar", required = false) Optional<Long> calendar,
			@RequestParam(value = "teacher", required = false) Optional<Long> teacher,
			@RequestParam(value = "subject", required = false) Optional<Long> subject,
			@RequestParam(value = "status", required = false) Optional<Integer> status,
			@RequestParam(value = "student", required = false) Optional<Long> student,
			@RequestParam(value = "attendance", required = false) Optional<Long> attendance,
			@RequestParam(value = "datefrom", required = false) Optional<Timestamp> datefrom,
			@RequestParam(value = "dateto", required = false) Optional<Timestamp> dateto) {

		Map<String, Object> response = service.getPaginatedStudentAttendances(page, size, query, schoolgroup, school,
				classid, calendar, teacher, subject, status, student, attendance, datefrom, dateto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/students")
	public ResponseEntity<?> getStudentAttendances(
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			@RequestParam(value = "school", required = false) Optional<Long> school,
			@RequestParam(value = "class", required = false) Optional<Long> classid,
			@RequestParam(value = "calendar", required = false) Optional<Long> calendar,
			@RequestParam(value = "teacher", required = false) Optional<Long> teacher,
			@RequestParam(value = "datefrom", required = false) Optional<Timestamp> datefrom,
			@RequestParam(value = "dateto", required = false) Optional<Timestamp> dateto) {

		Map<String, Object> response = service.getOrdinaryStudentAttendances(query, schoolgroup, school, classid,
				calendar, teacher, datefrom, dateto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/flagStudents")
	public ResponseEntity<?> getFlagStudentAttendances(
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			@RequestParam(value = "school", required = false) Optional<Long> school,
			@RequestParam(value = "class", required = false) Optional<Long> classid,
			@RequestParam(value = "calendar", required = false) Optional<Long> calendar,
			@RequestParam(value = "teacher", required = false) Optional<Long> teacher,
			@RequestParam(value = "datefrom", required = false) Optional<Timestamp> datefrom,
			@RequestParam(value = "dateto", required = false) Optional<Timestamp> dateto) {

		Map<String, Object> response = service.getOrdinaryStudentAttendances(query, schoolgroup, school, classid,
				calendar, teacher, datefrom, dateto);

		@SuppressWarnings("unchecked")
		List<Rowcall> ordinaryArray = (List<Rowcall>) response.get("attendances");
		Map<String, Object> newResponse = new HashMap<>();

		Integer max = ordinaryArray.size();
		Long studentAttendance = ordinaryArray.stream().filter(o -> o.getStatus() == 1).count();
		Long studentAbsent = ordinaryArray.stream().filter(o -> o.getStatus() == 0).count();
		Long studentExcused = ordinaryArray.stream().filter(o -> o.getStatus() == 2).count();

		newResponse.put("student_attendance", convertPercentage(studentAttendance.intValue(), max));
		newResponse.put("student_absent", convertPercentage(studentAbsent.intValue(), max));
		newResponse.put("student_excused", convertPercentage(studentExcused.intValue(), max));

		return new ResponseEntity<>(newResponse, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getAttendance(@PathVariable(value = "id") Long id) {
		try {
			Attendance val = service.findAttendance(id);
			return ResponseEntity.ok()
					.body(new ApiDataResponse(true, "Attendance has been retrieved successfully.", val));

		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,
					"You do not have access to this resource because your Bearer token is either expired or not set."));
		}
	}

	@GetMapping("/management/{id}")
	public ResponseEntity<?> getAttendanceManagement(@PathVariable(value = "id") Long id) {
		try {
			AttendanceManagement val = serviceManagement.findAttendanceManagementByAttendance(id);
			return ResponseEntity.ok()
					.body(new ApiDataResponse(true, "Attendance Management has been retrieved successfully.", val));

		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,
					"You do not have access to this resource because your Bearer token is either expired or not set."));
		}
	}

	@PutMapping("/management/attendance/{id}")
	public ResponseEntity<?> updateAttendanceManagement(
			@AuthenticationPrincipal UserPrincipal userDetails,
			@PathVariable(value = "id") Long id,
			@RequestBody AttendanceComposite attRequest) {

		try {
			Attendance valAtt = service.update(attRequest.getAttendance(), id);

			AttendanceManagement val = serviceManagement.updateByAttendance(attRequest.getManagement(), id);
			// get Activity ID
			AttendanceActivity val2 = serviceActivity.findAttendanceActivityByAttendance(id);
			// then Update the activity
			AttendanceActivity attval = serviceActivity.update(attRequest.getActivity(), val2.getAttactId());

			if (val.getAtt_id().getTeacher().getTeaId() != null) {

				Optional<User> u = userRepository.findById(userDetails.getId());

				// ------------------------------------
				saveEvent("attendancemanagement", "edit",
						"The Teacher with name: " + val.getAtt_id().getTeacher().getFname() + " "
								+ val.getAtt_id().getTeacher().getLname()
								+ " edited the Attendance Management done by the Principal ",
						new Date(), u.get(), u.get().getSchool());

				saveEvent("attendanceactivity", "edit",
						"The Teacher with name: " + val.getAtt_id().getTeacher().getFname() + " "
								+ val.getAtt_id().getTeacher().getLname()
								+ " edited the Attendance Management and also sent an update into Activity feed. ",
						new Date(), u.get(), u.get().getSchool());
			}
			return ResponseEntity.ok().body(new ApiDataResponse(true,
					"Attendance Management and Activity has been updated successfully.", val));

		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,
					"You do not have access to this resource because your Bearer token is either expired or not set."));
		}
	}

	@GetMapping("/activity/{id}")
	public ResponseEntity<?> getAttendanceActivity(@PathVariable(value = "id") Long id) {
		try {
			AttendanceActivity val = serviceActivity.findAttendanceActivityByAttendance(id);
			return ResponseEntity.ok()
					.body(new ApiDataResponse(true, "Attendance Activity has been retrieved successfully.", val));

		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,
					"You do not have access to this resource because your Bearer token is either expired or not set."));
		}
	}

	// Teacher only
	@PutMapping("/{id}")
	public ResponseEntity<?> updateAttendance(
			@AuthenticationPrincipal UserPrincipal userDetails,
			@PathVariable(value = "id") Long id,
			@RequestBody AttendanceComposite attRequest) {
		try {
			Attendance val = service.update(attRequest.getAttendance(), id);
			if (val != null) {

				attRequest.getActivity().setOwnertype("Principal");
				attRequest.getActivity().setExpected(addDays(parseTimestamp(todayDate()), 2));

				serviceActivity.saveOne(attRequest.getActivity(), val);

				serviceManagement.saveOne(attRequest.getManagement(), val);
			}
			if (val != null) {
				Optional<User> u = userRepository.findById(userDetails.getId());

				// ------------------------------------
				saveEvent("attendance", "edit",
						"The Teacher with name: " + val.getTeacher().getFname() + " " + val.getTeacher().getLname()
								+ " edited the Attendance Values done by this Teacher ",
						new Date(), u.get(), u.get().getSchool());

				saveEvent("attendanceactivity", "create",
						"The Teacher with name: " + val.getTeacher().getFname() + " " + val.getTeacher().getLname()
								+ "has his attendance submitted & the Activity has been submitted too. ",
						new Date(), u.get(), u.get().getSchool());

				saveEvent("attendancemanagement", "create",
						"The Teacher with name: " + val.getTeacher().getFname() + " " + val.getTeacher().getLname()
								+ "has his attendance submitted & the Management has been submitted too. ",
						new Date(), u.get(), u.get().getSchool());
				return ResponseEntity.ok()
						.body(new ApiDataResponse(true, "Attendance has been updated successfully.", val));
			}
			return ResponseEntity.ok().body(
					new ApiDataResponse(true, "Attendance update was halted.", "Attendance has been taken already."));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,
					"You do not have access to this resource because your Bearer token is either expired or not set."));
		}
	}

	@PutMapping("/rowcall")
	public ResponseEntity<?> updateRowcall(

			@RequestBody List<RowcallRequest> rowcallRequest

	) {
		Long getOneAtt = rowcallRequest.get(0).getAtt_id();
		Rowcall rcCheck = service.findAllByRowcall(getOneAtt);
		if (rcCheck != null) {
			return ResponseEntity.status(400)
					.body(new ApiDataResponse(true, "Attendance rowcall is already in the database", false));
		}
		try {
			ModelMapper modelMapper = new ModelMapper();
			List<Rowcall> rclist = rowcallRequest.stream().map(t -> {
				Rowcall rc = modelMapper.map(t, Rowcall.class);
				Attendance att = new Attendance();
				Student stu = new Student();
				att.setAttId(t.getAtt_id());
				stu.setPupId(t.getStu_id());
				rc.setAttendance(att);
				rc.setStudent(stu);
				return rc;

			}).collect(Collectors.toList());

			List<Rowcall> rc = service.saveRowCall(rclist);

			/*
			 * Optional<User> u = userRepository.findById( userDetails.getId() );
			 * 
			 * //------------------------------------
			 * saveEvent("attendance", "edit", "The Teacher with name: " +
			 * rc.get(0).getAttendance().getTeacher().getFname() + " " +
			 * rc.get(0).getAttendance().getTeacher().getLname() +
			 * " edited the Attendance Rowcall values done by this Teacher ",
			 * new Date(), u.get(), u.get().getSchool()
			 * );
			 */

			return ResponseEntity.ok().body(new ApiDataResponse(true, "Attendance rowcall has been updated",
					"Attendance rowcall size updated is " + rc.size()));
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,
					"You do not have access to this resource because your Bearer token is either expired or not set."));
		}
	}

	@GetMapping("/rowcall/{id}")
	public ResponseEntity<?> getRowcallAttendance(@PathVariable(value = "id") Long id) {
		try {
			Rowcall val = service.findRowcall(id);
			return ResponseEntity.ok().body(new ApiDataResponse(true, "Rowcall has been retrieved successfully.", val));

		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,
					"You do not have access to this resource because your Bearer token is either expired or not set."));
		}
	}

	@PutMapping("/rowcall/obs/{id}")
	public ResponseEntity<?> updateRowcallObs(@PathVariable(value = "id") Long id,
			@RequestBody RowcallRequest attRequest) {
		try {

			Rowcall val = service.update(attRequest, id);
			return ResponseEntity.ok().body(new ApiDataResponse(true, "Rowcall has been updated successfully.", val));

		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,
					"You do not have access to this resource because your Bearer token is either expired or not set."));
		}
	}

	// Done by Principal
	@PutMapping("/approve/{activity}")
	public ResponseEntity<?> approveAttendance(
			@AuthenticationPrincipal UserPrincipal userDetails,
			@PathVariable(value = "activity") Long attactivityId,
			@RequestBody AttendanceComposite attRequest) {
		try {

			AttendanceActivity attval = serviceActivity.update(attRequest.getActivity(), attactivityId);
			// update the attendance management

			Optional<User> u = userRepository.findById(userDetails.getId());

			// ------------------------------------
			saveEvent("attendance", "edit",
					"The Teacher with name: " + attval.getAtt_id().getTeacher().getFname() + " "
							+ attval.getAtt_id().getTeacher().getLname()
							+ "has his attendance approved & this is done by the School Principal ",
					new Date(), u.get(), u.get().getSchool());

			saveEvent("attendanceactivity", "edit",
					"The Teacher with name: " + attval.getAtt_id().getTeacher().getFname() + " "
							+ attval.getAtt_id().getTeacher().getLname()
							+ "has his attendance approved  by the School Principal & the Activity with ID: "
							+ attval.getAttactId() + " has been added",
					new Date(), u.get(), u.get().getSchool());

			return ResponseEntity.ok().body(
					new ApiDataResponse(true, "Attendance activity has been approved/denied successfully.", attval));
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,
					"You do not have access to this resource because your Bearer token is either expired or not set."));
		}
	}

	@RequestMapping(path = "/file/{id}", method = PUT, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> updateAttendancePhoto(@PathVariable(value = "id") Long id,
			@RequestPart("att") MultipartFile multipartFile) {
		try {
			String fileOriginalName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String fileName = "0" + id.toString() + "." + FileUploadUtil.findExtension(fileOriginalName).get();

			String uploadDir = "teacher-attendance/" + id;
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

			String val = service.updatePhoto(id, fileName);
			return ResponseEntity.ok().body(
					new ApiDataResponse(true, "Teacher Attendance Photo has been added/updated successfully.", val));
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Update Attendance " + ex.getLocalizedMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,
					"You do not have access to this resource because your Bearer token is either expired or not set."));
		}
	}

	@GetMapping("/created-per-day")
	public ResponseEntity<Map<String, Object>> getTeachersCreatedPerDay(
			@RequestParam(value = "days", defaultValue = "7") int numberOfDays) {

		Map<String, Object> attCreatedPerDay = service.getAttendancesCreatedWithinDays(numberOfDays);

		return new ResponseEntity<>(attCreatedPerDay, HttpStatus.OK);
	}

	@GetMapping("/created-per-day-2")
	public ResponseEntity<Map<String, Object>> getTeachersCreatedPerDayTwo(
			@RequestParam(value = "days", defaultValue = "7") int numberOfDays) {

		Map<String, Object> teachersCreatedPerDay = service.getAttendancesManagementCreatedWithinDays(numberOfDays);

		return new ResponseEntity<>(teachersCreatedPerDay, HttpStatus.OK);
	}

	@GetMapping("/created-per-day-generic")
	public ResponseEntity<Map<String, Object>> getSchoolsCreatedPerDayTwo(
			@RequestParam(value = "days", defaultValue = "7") int numberOfDays,
			@RequestParam(value = "schoolgroup") Optional<Long> schoolgroup,
			@RequestParam(value = "school", required = false) Optional<Long> school,
			@RequestParam(value = "teacher", required = false) Optional<Long> teacher) {

		Map<String, Object> teachersCreatedPerDay = service.getAttendancesForSchoolCreatedWithinDays(numberOfDays,
				schoolgroup, school, teacher);

		return new ResponseEntity<>(teachersCreatedPerDay, HttpStatus.OK);
	}

	// ------------------------------------------------------------------------------------------------------------------
	private EventManager saveEvent(String module, String action, String comment, Date d, User u, School sch) {

		EventManager _event = new EventManager();

		_event.setModule(module);
		_event.setAction(action);
		_event.setComment(comment);
		_event.setDateofevent(d);
		_event.setUser(u);
		_event.setSchool(sch);

		return eventRepository.save(_event);
	}

	private Integer convertPercentage(Integer actual, Integer max) {
		return actual == 0 ? 0 : ((actual / max) * 100);
	}

	private String todayDate() {
		Date d = new Date();
		String date = DATE_TIME_FORMAT.format(d);
		return date;
	}

	private java.sql.Timestamp parseTimestamp(String timestamp) {
		try {
			return new Timestamp(DATE_TIME_FORMAT.parse(timestamp).getTime());
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private Timestamp addDays(Timestamp date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); // minus number would decrement the days
		return new Timestamp(cal.getTime().getTime());
	}

}
