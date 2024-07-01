package basepackage.stand.standbasisprojectonev1.scheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.io.*;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import basepackage.stand.standbasisprojectonev1.model.Calendar;
import basepackage.stand.standbasisprojectonev1.model.ClassStream;
import basepackage.stand.standbasisprojectonev1.model.Enrollment;
import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.Teacher;
import basepackage.stand.standbasisprojectonev1.model.TimeTable;
import basepackage.stand.standbasisprojectonev1.repository.TimetableRepository;
import basepackage.stand.standbasisprojectonev1.service.AttendanceService;
import basepackage.stand.standbasisprojectonev1.service.CalendarService;
import basepackage.stand.standbasisprojectonev1.service.ClassService;
import basepackage.stand.standbasisprojectonev1.service.EnrollmentService;
import basepackage.stand.standbasisprojectonev1.service.MneService;
import basepackage.stand.standbasisprojectonev1.service.SchoolService;
import basepackage.stand.standbasisprojectonev1.service.TeacherService;
import basepackage.stand.standbasisprojectonev1.service.TimetableService;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import com.opencsv.CSVWriter;



@Component
public class ScheduledConsole {

    @Autowired		
    private TimetableRepository timeRepository;

    @Autowired
    SchoolService schoolService;

    @Autowired
    TeacherService teacherService;

    @Autowired
    ClassService classService;

    @Autowired
	TimetableService serviceTimetable;

    @Autowired
	EnrollmentService serviceEnrollment;
	
	@Autowired
	AttendanceService attTimetable;

    @Autowired
	CalendarService calService;

    @Autowired
	MneService mneService;

    @Value("${aws.region}")
    private String region;
    
    @Value("${aws.secretKey}")
    private String sk;
    
    @Value("${aws.accessKeyId}")
    private String accesskey;
    
    private final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    //for schools data
    @Transactional
    @Scheduled(cron = "0 35 18 * * *")
    public void schoolsSnapshot() {
        System.setProperty("aws.accessKeyId", accesskey);
        System.setProperty("aws.secretAccessKey", sk);
        System.setProperty("aws.region", region);
        
        // Get Timetable data for this day with Current calendar
	    List<TimeTable> tt = timeRepository.findByActiveCalendarInConsole(1, 1, "government", 1);
	    
	    // Get Unique timetable data based on school values
	    List<TimeTable> ttnew = tt.stream()
	            .collect(Collectors.toMap(
	                    obj -> obj.getSchool(),  // Composite key
	                    Function.identity(),  // Keep the original object
	                    (obj1, obj2) -> obj1  // Merge function (in case of duplicate keys)
	            ))
	            .values()
	            .stream()
	            .collect(Collectors.toList());

       
        for (TimeTable it : ttnew) {
            //Timestamp realDate = parseTimestamp(todayDate());
            Optional<Long> optionalOwner = Optional.of(it.getSchool().getOwner().getId());
           // Optional<Timestamp> optionalTimestamp = Optional.of(realDate);
            Map<String, Object> response2 = schoolService.getOrdinarySchools("", optionalOwner);
            
            @SuppressWarnings("unchecked")
            List<School> listSchoolTotal = (List<School>) response2.get("schools");
            
            List<School> countSecondary = listSchoolTotal.stream()
                    .filter(ss -> ss.getType_of().equals("secondary") )
                    .collect(Collectors.toList());
            List<School> countPrimary = listSchoolTotal.stream()
                    .filter(ss -> ss.getType_of().equals("primary") )
                    .collect(Collectors.toList());

            // Create CSV
            String csvFile = "data.csv";
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
                String[] header = {"Total Count", "Total Primary", "Total Secondary"};
                writer.writeNext(header);
                
                String[] data = {String.valueOf(listSchoolTotal.size()), String.valueOf(countPrimary.size()), String.valueOf(countSecondary.size())};
                writer.writeNext(data);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Zip the CSV
            String zipFile = "data.zip";
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
                FileInputStream fis = new FileInputStream(csvFile)) {
                ZipEntry zipEntry = new ZipEntry(csvFile);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Generate file name for S3
            String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String bucketName = "standb670";
            String schoolId = String.valueOf(it.getSchool().getId());
            String s3FileName = "schools" + "/" + date + "_" + "schools" + "_" + schoolId + "_console_snapshot.zip";

            S3Client client = S3Client.builder().build();
		        
			PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3FileName)
                        .acl("public-read")
                        .build();

            client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(zipFile)));
          
            // Clean up local files
            new File(csvFile).delete();
            new File(zipFile).delete();

            /////////////////////////////////////////////////////////////////////////////////////////////////////
            String csvlogFile = "logs.csv";
            String ziplogFile = "logs.zip";
    
            // Create CSV
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvlogFile))) {
                // Write headers
                writer.writeNext(getHeaders(School.class));
    
                // Write data
                for (School school : listSchoolTotal) {
                    writer.writeNext(getSchoolData(school));
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }

            // Zip the CSV
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(ziplogFile));
                    FileInputStream fis = new FileInputStream(csvlogFile)) {
                ZipEntry zipEntry = new ZipEntry(csvlogFile);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }

            // Generate file name for S3
            String datelogs = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String bucketNameLog = "standb670";
            String schoolIdLog = String.valueOf(it.getSchool().getId());
            String s3FileNameLog = "schools" + "/" + datelogs + "_" + "schools" + "_" + schoolIdLog + "_logs_snapshot.zip";

            S3Client clientLog = S3Client.builder().build();
		        
			PutObjectRequest requestLog = PutObjectRequest.builder()
                        .bucket(bucketNameLog)
                        .key(s3FileNameLog)
                        .acl("public-read")
                        .build();

            clientLog.putObject(requestLog, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(ziplogFile)));

             // Clean up local files
             new File(csvlogFile).delete();
             new File(ziplogFile).delete();
        }
    }

    @Transactional
    @Scheduled(cron = "0 35 18 * * *")
    public void teachersSnapshot() {
        System.setProperty("aws.accessKeyId", accesskey);
        System.setProperty("aws.secretAccessKey", sk);
        System.setProperty("aws.region", region);
        
        int underdeployed = 0;
		int deployed = 0; 
		int overdeployed = 0;

        // Get Timetable data for this day with Current calendar
	    List<TimeTable> tt = timeRepository.findByActiveCalendarInConsole(1, 1, "government", 1);
	    
	    // Get Unique timetable data based on school values
	    List<TimeTable> ttnew = tt.stream()
	            .collect(Collectors.toMap(
	                    obj -> obj.getSchool(),  // Composite key
	                    Function.identity(),  // Keep the original object
	                    (obj1, obj2) -> obj1  // Merge function (in case of duplicate keys)
	            ))
	            .values()
	            .stream()
	            .collect(Collectors.toList());

       
        for (TimeTable it : ttnew) {
           
            Optional<Long> optionalGroup = Optional.of(it.getSchool().getOwner().getId());
            Optional<Long> optionalOwner = Optional.of(it.getSchool().getSchId());

            Map<String, Object> response2 = teacherService.getOrdinaryTeachers("",optionalGroup, optionalOwner);
            
            Map<String, Object> response3 = serviceTimetable.getOrdinaryTimeTables("", optionalOwner, optionalGroup);
               
            @SuppressWarnings("unchecked")
            List<Teacher> listTeacher = (List<Teacher>) response2.get("teachers");
            
            @SuppressWarnings("unchecked")
            List<TimeTable> listTimetable = (List<TimeTable>) response3.get("timetables");	
            
            for (Teacher teaT : listTeacher) {
                List<TimeTable> countTimetableTeacherHas = listTimetable.stream()
					.filter(ttv -> ttv.getTeacher().getTeaId() == teaT.getTeaId() && ttv.getStatus() == 1 && ttv.getCalendar().getStatus() == 1 )
			        .filter(distinctByKey(pr -> Arrays.asList(pr.getSubject(), pr.getTeacher(), pr.getClass_stream() )))
			        .collect(Collectors.toList());

                    if (countTimetableTeacherHas.size() > 0) {
                        deployed++;
                    }
                    
                    if (countTimetableTeacherHas.size() > 0 && countTimetableTeacherHas.size() >= 3) {
                        overdeployed++;
                    }
                    
                    if (countTimetableTeacherHas.size() > 0 && countTimetableTeacherHas.size() < 2) {
                        underdeployed++;
                    }
                    
            }

            // Create CSV
            String csvFile = "data.csv";
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
                String[] header = {"Total Count", "Total Deployed", "Total Under-Deployed", "Over-Deployed"};
                writer.writeNext(header);
                
                String[] data = {String.valueOf(listTeacher.size()), String.valueOf(deployed), String.valueOf(underdeployed), String.valueOf(overdeployed)};
                writer.writeNext(data);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Zip the CSV
            String zipFile = "data.zip";
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
                FileInputStream fis = new FileInputStream(csvFile)) {
                ZipEntry zipEntry = new ZipEntry(csvFile);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Generate file name for S3
            String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String bucketName = "standb670";
            String schoolId = String.valueOf(it.getSchool().getId());
            String s3FileName = "teachers" + "/" + date + "_" + "teachers" + "_" + schoolId + "_console_snapshot.zip";

            S3Client client = S3Client.builder().build();
		        
			PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3FileName)
                        .acl("public-read")
                        .build();

            client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(zipFile)));
          
            // Clean up local files
            new File(csvFile).delete();
            new File(zipFile).delete();

            /////////////////////////////////////////////////////////////////////////////////////////////////////
            String csvlogFile = "logs.csv";
            String ziplogFile = "logs.zip";
    
            // Create CSV
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvlogFile))) {
                // Write headers
                writer.writeNext(getHeaders(Teacher.class));
    
                // Write data
                for (Teacher tea : listTeacher) {
                    writer.writeNext(getTeacherData(tea));
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }

            // Zip the CSV
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(ziplogFile));
                    FileInputStream fis = new FileInputStream(csvlogFile)) {
                ZipEntry zipEntry = new ZipEntry(csvlogFile);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }

            // Generate file name for S3
            String datelogs = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String bucketNameLog = "standb670";
            String schoolIdLog = String.valueOf(it.getSchool().getId());
            String s3FileNameLog = "teachers" + "/" + datelogs + "_" + "teachers" + "_" + schoolIdLog + "_logs_snapshot.zip";

            S3Client clientLog = S3Client.builder().build();
		        
			PutObjectRequest requestLog = PutObjectRequest.builder()
                        .bucket(bucketNameLog)
                        .key(s3FileNameLog)
                        .acl("public-read")
                        .build();

            clientLog.putObject(requestLog, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(ziplogFile)));

             // Clean up local files
             new File(csvlogFile).delete();
             new File(ziplogFile).delete();
        }
    }

    @Transactional
    @Scheduled(cron = "0 35 18 * * *")
    public void enrollmentsSnapshot() {
        System.setProperty("aws.accessKeyId", accesskey);
        System.setProperty("aws.secretAccessKey", sk);
        System.setProperty("aws.region", region);

        // Get Timetable data for this day with Current calendar
	    List<TimeTable> tt = timeRepository.findByActiveCalendarInConsole(1, 1, "government", 1);
	    
	    // Get Unique timetable data based on school values
	    List<TimeTable> ttnew = tt.stream()
	            .collect(Collectors.toMap(
	                    obj -> obj.getSchool(),  // Composite key
	                    Function.identity(),  // Keep the original object
	                    (obj1, obj2) -> obj1  // Merge function (in case of duplicate keys)
	            ))
	            .values()
	            .stream()
	            .collect(Collectors.toList());

       
        for (TimeTable it : ttnew) {
           
            Optional<Long> optionalGroup = Optional.of(it.getSchool().getOwner().getId());
            Optional<Long> optionalOwner = Optional.of(it.getSchool().getSchId());
               
            Map<String, Object> response2 = serviceEnrollment.getOrdinaryEnrollments("", optionalGroup, optionalOwner);
        
            @SuppressWarnings("unchecked")
            List<Enrollment> listEnrollment = (List<Enrollment>) response2.get("enrollments");
            
            List<Enrollment> totalEnrollment = listEnrollment.stream()
                    .filter( en -> en.getStatus() == 1 )
                    .collect(Collectors.toList());            
          
            List<Enrollment> countJuniorFemale = listEnrollment.stream()
                    .filter(en -> en.getStatus() == 1 && en.getStudent().getGender().equals("F") && en.getClassstream().getClass_index() <= 9 && en.getClassstream().getClass_index() >= 7 )
                    .collect(Collectors.toList());
            
            List<Enrollment> countJuniorMale = listEnrollment.stream()
                    .filter(en -> en.getStatus() == 1 && en.getStudent().getGender().equals("M") && en.getClassstream().getClass_index() <= 9 && en.getClassstream().getClass_index() >= 7 )
                    .collect(Collectors.toList());
            
            List<Enrollment> countSeniorMale = listEnrollment.stream()
                    .filter(en -> en.getStatus() == 1 && en.getStudent().getGender().equals("M") && en.getClassstream().getClass_index() <= 12 && en.getClassstream().getClass_index() >= 10 )
                    .collect(Collectors.toList());
            
            List<Enrollment> countSeniorFemale = listEnrollment.stream()
                    .filter(en -> en.getStatus() == 1 && en.getStudent().getGender().equals("F") && en.getClassstream().getClass_index() <= 12 && en.getClassstream().getClass_index() >= 10 )
                    .collect(Collectors.toList());	
            
            // Create CSV
            String csvFile = "data.csv";
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
                String[] header = {"Total Count", "Total JSS Male", "Total JSS Female", "Total SSS Male", "Total SSS Female"};
                writer.writeNext(header);
                
                String[] data = {String.valueOf(totalEnrollment.size()), String.valueOf(countJuniorMale.size()), String.valueOf(countJuniorFemale.size()), String.valueOf(countSeniorMale.size()), String.valueOf(countSeniorFemale.size())};
                writer.writeNext(data);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Zip the CSV
            String zipFile = "data.zip";
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
                FileInputStream fis = new FileInputStream(csvFile)) {
                ZipEntry zipEntry = new ZipEntry(csvFile);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Generate file name for S3
            String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String bucketName = "standb670";
            String schoolId = String.valueOf(it.getSchool().getId());
            String s3FileName = "enrollments" + "/" + date + "_" + "enrollments" + "_" + schoolId + "_console_snapshot.zip";

            S3Client client = S3Client.builder().build();
		        
			PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3FileName)
                        .acl("public-read")
                        .build();

            client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(zipFile)));
          
            // Clean up local files
            new File(csvFile).delete();
            new File(zipFile).delete();

            /////////////////////////////////////////////////////////////////////////////////////////////////////
            String csvlogFile = "logs.csv";
            String ziplogFile = "logs.zip";
    
            // Create CSV
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvlogFile))) {
                // Write headers
                writer.writeNext(getHeaders(Teacher.class));
    
                // Write data
                for (Enrollment en : listEnrollment) {
                    writer.writeNext(getEnrollmentData(en));
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }

            // Zip the CSV
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(ziplogFile));
                    FileInputStream fis = new FileInputStream(csvlogFile)) {
                ZipEntry zipEntry = new ZipEntry(csvlogFile);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }

            // Generate file name for S3
            String datelogs = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String bucketNameLog = "standb670";
            String schoolIdLog = String.valueOf(it.getSchool().getId());
            String s3FileNameLog = "enrollments" + "/" + datelogs + "_" + "enrollments" + "_" + schoolIdLog + "_logs_snapshot.zip";

            S3Client clientLog = S3Client.builder().build();
		        
			PutObjectRequest requestLog = PutObjectRequest.builder()
                        .bucket(bucketNameLog)
                        .key(s3FileNameLog)
                        .acl("public-read")
                        .build();

            clientLog.putObject(requestLog, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(ziplogFile)));

             // Clean up local files
             new File(csvlogFile).delete();
             new File(ziplogFile).delete();
        }
    }

    @Transactional
    @Scheduled(cron = "0 35 18 * * *")
    public void classroomsSnapshot() {
        System.setProperty("aws.accessKeyId", accesskey);
        System.setProperty("aws.secretAccessKey", sk);
        System.setProperty("aws.region", region);
        int undeployedSeconday = 0;
        // Get Timetable data for this day with Current calendar
	    List<TimeTable> tt = timeRepository.findByActiveCalendarInConsole(1, 1, "government", 1);
	    
	    // Get Unique timetable data based on school values
	    List<TimeTable> ttnew = tt.stream()
	            .collect(Collectors.toMap(
	                    obj -> obj.getSchool(),  // Composite key
	                    Function.identity(),  // Keep the original object
	                    (obj1, obj2) -> obj1  // Merge function (in case of duplicate keys)
	            ))
	            .values()
	            .stream()
	            .collect(Collectors.toList());

       
        for (TimeTable it : ttnew) {
           
            Optional<Long> optionalGroup = Optional.of(it.getSchool().getOwner().getId());
            Optional<Long> optionalOwner = Optional.of(it.getSchool().getSchId());
               
            Map<String, Object> response2 = classService.getOrdinaryClassStreams("", optionalGroup, optionalOwner);
        
            Map<String, Object> response3 = serviceTimetable.getOrdinaryTimeTables("", optionalOwner, optionalGroup);        
            
            @SuppressWarnings("unchecked")
            List<ClassStream> listClassrooms = (List<ClassStream>) response2.get("classrooms");
            
            @SuppressWarnings("unchecked")
            List<TimeTable> listTimetable = (List<TimeTable>) response3.get("timetables");        
            
            List<ClassStream> countSecondaryJunior = listClassrooms.stream()
                    .filter(en -> en.getStatus() == 1 && en.getSchool().getType_of().equals("secondary") && en.getClass_index() <= 9 && en.getClass_index() >= 7 )
                    .collect(Collectors.toList());
            
            List<ClassStream> countSecondarySenior = listClassrooms.stream()
                    .filter(en -> en.getStatus() == 1 && en.getSchool().getType_of().equals("secondary") && en.getClass_index() <= 12 && en.getClass_index() >= 10 )
                    .collect(Collectors.toList());
            
            for (ClassStream clsT : listClassrooms) {
                
                List<TimeTable> countTimetableTeacherSecondaryHas = listTimetable.stream()
                        .filter(ttv -> ttv.getClass_stream().getClsId() == clsT.getClsId() && ttv.getClass_stream().getSchool().getType_of().equals("secondary")  && ttv.getStatus() == 1 && ttv.getCalendar().getStatus() == 1 )
                        .filter(distinctByKey(pr -> Arrays.asList( pr.getSubject(),  pr.getClass_stream() )))
                        .collect(Collectors.toList());
                
                if (countTimetableTeacherSecondaryHas.size() < 4) {
                    undeployedSeconday++;
                }
                
            }	
            
            // Create CSV
            String csvFile = "data.csv";
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
                String[] header = {"Total Count", "Total JSS", "Total SSS", "Total Undeployed"};
                writer.writeNext(header);
                
                String[] data = { String.valueOf(listClassrooms.size()), String.valueOf(countSecondaryJunior.size()), String.valueOf(countSecondarySenior.size()), String.valueOf(undeployedSeconday) };
                writer.writeNext(data);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Zip the CSV
            String zipFile = "data.zip";
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
                FileInputStream fis = new FileInputStream(csvFile)) {
                ZipEntry zipEntry = new ZipEntry(csvFile);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Generate file name for S3
            String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String bucketName = "standb670";
            String schoolId = String.valueOf(it.getSchool().getId());
            String s3FileName = "classrooms" + "/" + date + "_" + "classrooms" + "_" + schoolId + "_console_snapshot.zip";

            S3Client client = S3Client.builder().build();
		        
			PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3FileName)
                        .acl("public-read")
                        .build();

            client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(zipFile)));
          
            // Clean up local files
            new File(csvFile).delete();
            new File(zipFile).delete();

            /////////////////////////////////////////////////////////////////////////////////////////////////////
            String csvlogFile = "logs.csv";
            String ziplogFile = "logs.zip";
    
            // Create CSV
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvlogFile))) {
                // Write headers
                writer.writeNext(getHeaders(Teacher.class));
    
                // Write data
                for (ClassStream en : listClassrooms) {
                    writer.writeNext(getClassStreamData(en));
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }

            // Zip the CSV
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(ziplogFile));
                    FileInputStream fis = new FileInputStream(csvlogFile)) {
                ZipEntry zipEntry = new ZipEntry(csvlogFile);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }

            // Generate file name for S3
            String datelogs = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String bucketNameLog = "standb670";
            String schoolIdLog = String.valueOf(it.getSchool().getId());
            String s3FileNameLog = "classrooms" + "/" + datelogs + "_" + "classrooms" + "_" + schoolIdLog + "_logs_snapshot.zip";

            S3Client clientLog = S3Client.builder().build();
		        
			PutObjectRequest requestLog = PutObjectRequest.builder()
                        .bucket(bucketNameLog)
                        .key(s3FileNameLog)
                        .acl("public-read")
                        .build();

            clientLog.putObject(requestLog, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(ziplogFile)));

             // Clean up local files
             new File(csvlogFile).delete();
             new File(ziplogFile).delete();
        }
    }

    @Transactional
    @Scheduled(cron = "0 35 18 * * *")
    public void timetablesSnapshot() {
        System.setProperty("aws.accessKeyId", accesskey);
        System.setProperty("aws.secretAccessKey", sk);
        System.setProperty("aws.region", region);
      
        // Get Timetable data for this day with Current calendar
	    List<TimeTable> tt = timeRepository.findByActiveCalendarInConsole(1, 1, "government", 1);
	    
	    // Get Unique timetable data based on school values
	    List<TimeTable> ttnew = tt.stream()
	            .collect(Collectors.toMap(
	                    obj -> obj.getSchool(),  // Composite key
	                    Function.identity(),  // Keep the original object
	                    (obj1, obj2) -> obj1  // Merge function (in case of duplicate keys)
	            ))
	            .values()
	            .stream()
	            .collect(Collectors.toList());

       
        for (TimeTable it : ttnew) {
           
            Optional<Long> optionalGroup = Optional.of(it.getSchool().getOwner().getId());
            Optional<Long> optionalOwner = Optional.of(it.getSchool().getSchId());
               
            Map<String, Object> response3 = serviceTimetable.getOrdinaryTimeTables("", optionalOwner, optionalGroup); 
            @SuppressWarnings("unchecked")
            List<TimeTable> listTimetable = (List<TimeTable>) response3.get("timetables"); 

            long activeTimeTables   = listTimetable.stream().filter(sch -> sch.getStatus() == 1).count();       
            long inactiveTimeTables = listTimetable.stream().filter(sch -> sch.getStatus() == 0).count();
            
            // Create CSV
            String csvFile = "data.csv";
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
                String[] header = {"Total Count", "Total JSS", "Total Active Timetables", "Total Inactive Timetables"};
                writer.writeNext(header);
                
                String[] data = { String.valueOf(listTimetable.size()), String.valueOf(activeTimeTables), String.valueOf(inactiveTimeTables)};
                writer.writeNext(data);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Zip the CSV
            String zipFile = "data.zip";
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
                FileInputStream fis = new FileInputStream(csvFile)) {
                ZipEntry zipEntry = new ZipEntry(csvFile);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Generate file name for S3
            String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String bucketName = "standb670";
            String schoolId = String.valueOf(it.getSchool().getId());
            String s3FileName = "timetables" + "/" + date + "_" + "timetables" + "_" + schoolId + "_console_snapshot.zip";

            S3Client client = S3Client.builder().build();
		        
			PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3FileName)
                        .acl("public-read")
                        .build();

            client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(zipFile)));
          
            // Clean up local files
            new File(csvFile).delete();
            new File(zipFile).delete();

            /////////////////////////////////////////////////////////////////////////////////////////////////////
            String csvlogFile = "logs.csv";
            String ziplogFile = "logs.zip";
    
            // Create CSV
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvlogFile))) {
                // Write headers
                writer.writeNext(getHeaders(Teacher.class));
    
                // Write data
                for (TimeTable timme : listTimetable) {
                    writer.writeNext(getTimetableData(timme));
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }

            // Zip the CSV
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(ziplogFile));
                    FileInputStream fis = new FileInputStream(csvlogFile)) {
                ZipEntry zipEntry = new ZipEntry(csvlogFile);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }

            // Generate file name for S3
            String datelogs = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String bucketNameLog = "standb670";
            String schoolIdLog = String.valueOf(it.getSchool().getId());
            String s3FileNameLog = "timetables" + "/" + datelogs + "_" + "timetables" + "_" + schoolIdLog + "_logs_snapshot.zip";

            S3Client clientLog = S3Client.builder().build();
		        
			PutObjectRequest requestLog = PutObjectRequest.builder()
                        .bucket(bucketNameLog)
                        .key(s3FileNameLog)
                        .acl("public-read")
                        .build();

            clientLog.putObject(requestLog, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(ziplogFile)));

             // Clean up local files
             new File(csvlogFile).delete();
             new File(ziplogFile).delete();
        }
    }

    @Transactional
    @Scheduled(cron = "0 35 18 * * *")
    public void calendarsSnapshot() {
        System.setProperty("aws.accessKeyId", accesskey);
        System.setProperty("aws.secretAccessKey", sk);
        System.setProperty("aws.region", region);
      
        // Get Timetable data for this day with Current calendar
	    List<TimeTable> tt = timeRepository.findByActiveCalendarInConsole(1, 1, "government", 1);
	    
	    // Get Unique timetable data based on school values
	    List<TimeTable> ttnew = tt.stream()
	            .collect(Collectors.toMap(
	                    obj -> obj.getSchool(),  // Composite key
	                    Function.identity(),  // Keep the original object
	                    (obj1, obj2) -> obj1  // Merge function (in case of duplicate keys)
	            ))
	            .values()
	            .stream()
	            .collect(Collectors.toList());

       
        for (TimeTable it : ttnew) {
           
            Optional<Long> optionalGroup = Optional.of(it.getSchool().getOwner().getId());
            Optional<Long> optionalOwner = Optional.of(it.getSchool().getSchId());
               
            Map<String, Object> response2 = calService.getOrdinaryCalendars("", optionalOwner, optionalGroup);
        
            @SuppressWarnings("unchecked")
            List<Calendar> listCalendar = (List<Calendar>) response2.get("calendars");
            
            long activeCalendars = listCalendar.stream().filter(sch -> sch.getStatus() == 1).count();       
            long inactiveCalendars = listCalendar.stream().filter(sch -> sch.getStatus() == 0).count();
            
            // Create CSV
            String csvFile = "data.csv";
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
                String[] header = {"Total Count", "Total Active Calendars", "Total Inactive Calendars"};
                writer.writeNext(header);
                
                String[] data = { String.valueOf(listCalendar.size()), String.valueOf(activeCalendars), String.valueOf(inactiveCalendars)};
                writer.writeNext(data);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Zip the CSV
            String zipFile = "data.zip";
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
                FileInputStream fis = new FileInputStream(csvFile)) {
                ZipEntry zipEntry = new ZipEntry(csvFile);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Generate file name for S3
            String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String bucketName = "standb670";
            String schoolId = String.valueOf(it.getSchool().getId());
            String s3FileName = "calendars" + "/" + date + "_" + "calendars" + "_" + schoolId + "_console_snapshot.zip";

            S3Client client = S3Client.builder().build();
		        
			PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3FileName)
                        .acl("public-read")
                        .build();

            client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(zipFile)));
          
            // Clean up local files
            new File(csvFile).delete();
            new File(zipFile).delete();

            /////////////////////////////////////////////////////////////////////////////////////////////////////
            String csvlogFile = "logs.csv";
            String ziplogFile = "logs.zip";
    
            // Create CSV
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvlogFile))) {
                // Write headers
                writer.writeNext(getHeaders(Calendar.class));
    
                // Write data
                for (Calendar cal : listCalendar) {
                    writer.writeNext(getCalendarData(cal));
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }

            // Zip the CSV
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(ziplogFile));
                    FileInputStream fis = new FileInputStream(csvlogFile)) {
                ZipEntry zipEntry = new ZipEntry(csvlogFile);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }

            // Generate file name for S3
            String datelogs = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String bucketNameLog = "standb670";
            String schoolIdLog = String.valueOf(it.getSchool().getId());
            String s3FileNameLog = "calendars" + "/" + datelogs + "_" + "calendars" + "_" + schoolIdLog + "_logs_snapshot.zip";

            S3Client clientLog = S3Client.builder().build();
		        
			PutObjectRequest requestLog = PutObjectRequest.builder()
                        .bucket(bucketNameLog)
                        .key(s3FileNameLog)
                        .acl("public-read")
                        .build();

            clientLog.putObject(requestLog, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(ziplogFile)));

             // Clean up local files
             new File(csvlogFile).delete();
             new File(ziplogFile).delete();
        }
    }

    @Transactional
    @Scheduled(cron = "0 35 18 * * *")
    public void lessonnoteMneSnapshot() {
        System.setProperty("aws.accessKeyId", accesskey);
        System.setProperty("aws.secretAccessKey", sk);
        System.setProperty("aws.region", region);
      
        // Get Timetable data for this day with Current calendar
	    List<TimeTable> tt = timeRepository.findByActiveCalendarInConsole(1, 1, "government", 1);
	    
	    // Get Unique timetable data based on school values
	    List<TimeTable> ttnew = tt.stream()
	            .collect(Collectors.toMap(
	                    obj -> obj.getSchool(),  // Composite key
	                    Function.identity(),  // Keep the original object
	                    (obj1, obj2) -> obj1  // Merge function (in case of duplicate keys)
	            ))
	            .values()
	            .stream()
	            .collect(Collectors.toList());

       
        for (TimeTable it : ttnew) {
           
            Optional<Long> optionalGroup = Optional.of(it.getSchool().getOwner().getId());
            Optional<Long> optionalOwner = Optional.of(it.getSchool().getSchId());
               
            Map<String, Object> response = mneService.getOrdinaryLessonnoteMneProprietor( optionalGroup, optionalOwner, null, null );
            
            String teacher_management = (String) response.get("teacher_management");
            String head_admin = (String) response.get("head_admin");
            
            // Create CSV
            String csvFile = "data.csv";
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
                String[] header = {"Date of Mne", "Teacher Management", "Admin/Head Management"};
                writer.writeNext(header);
                
                String[] data = { String.valueOf(todayDate()), teacher_management, head_admin };
                writer.writeNext(data);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Zip the CSV
            String zipFile = "data.zip";
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
                FileInputStream fis = new FileInputStream(csvFile)) {
                ZipEntry zipEntry = new ZipEntry(csvFile);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Generate file name for S3
            String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String bucketName = "standb670";
            String schoolId = String.valueOf(it.getSchool().getId());
            String s3FileName = "lessonnoteMNE" + "/" + date  + "_" + schoolId + "_console_snapshot.zip";

            S3Client client = S3Client.builder().build();
		        
			PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3FileName)
                        .acl("public-read")
                        .build();

            client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(zipFile)));
          
            // Clean up local files
            new File(csvFile).delete();
            new File(zipFile).delete();

            /////////////////////////////////////////////////////////////////////////////////////////////////////
        }
    }

    @Transactional
    @Scheduled(cron = "0 35 18 * * *")
    public void attendanceMneSnapshot() {
        System.setProperty("aws.accessKeyId", accesskey);
        System.setProperty("aws.secretAccessKey", sk);
        System.setProperty("aws.region", region);
      
        // Get Timetable data for this day with Current calendar
	    List<TimeTable> tt = timeRepository.findByActiveCalendarInConsole(1, 1, "government", 1);
	    
	    // Get Unique timetable data based on school values
	    List<TimeTable> ttnew = tt.stream()
	            .collect(Collectors.toMap(
	                    obj -> obj.getSchool(),  // Composite key
	                    Function.identity(),  // Keep the original object
	                    (obj1, obj2) -> obj1  // Merge function (in case of duplicate keys)
	            ))
	            .values()
	            .stream()
	            .collect(Collectors.toList());

       
        for (TimeTable it : ttnew) {
           
            Optional<Long> optionalGroup = Optional.of(it.getSchool().getOwner().getId());
            Optional<Long> optionalOwner = Optional.of(it.getSchool().getSchId());
               
            Map<String, Object> response = mneService.getOrdinaryAttendanceMneProprietor( optionalGroup, optionalOwner, null, null );
            
            String teacher_attendance = (String) response.get("teacher_attendance");
            String teacher_management = (String) response.get("teacher_management");
            String student_att = (String) response.get("student_att");
            String student_att_excused = (String) response.get("student_att_excused");
            String head_admin = (String) response.get("head_admin");
            
            // Create CSV
            String csvFile = "data.csv";
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
                String[] header = {"Date of Mne", "Teacher Attendance", "Teacher Management", "Student Attendance", "Student Attendance Excused", "Head Management"};
                writer.writeNext(header);
                
                String[] data = { String.valueOf(todayDate()), teacher_attendance, teacher_management, student_att, student_att_excused, head_admin  };
                writer.writeNext(data);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Zip the CSV
            String zipFile = "data.zip";
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
                FileInputStream fis = new FileInputStream(csvFile)) {
                ZipEntry zipEntry = new ZipEntry(csvFile);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Generate file name for S3
            String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String bucketName = "standb670";
            String schoolId = String.valueOf(it.getSchool().getId());
            String s3FileName = "attendanceMNE" + "/" + date  + "_" + schoolId + "_console_snapshot.zip";

            S3Client client = S3Client.builder().build();
		        
			PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3FileName)
                        .acl("public-read")
                        .build();

            client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(zipFile)));
          
            // Clean up local files
            new File(csvFile).delete();
            new File(zipFile).delete();

            /////////////////////////////////////////////////////////////////////////////////////////////////////
        }
    }

    @Transactional
    @Scheduled(cron = "0 35 18 * * *")
    public void lessonnoteFlagsSnapshot() {
        System.setProperty("aws.accessKeyId", accesskey);
        System.setProperty("aws.secretAccessKey", sk);
        System.setProperty("aws.region", region);
      
        // Get Timetable data for this day with Current calendar
	    List<TimeTable> tt = timeRepository.findByActiveCalendarInConsole(1, 1, "government", 1);
	    
	    // Get Unique timetable data based on school values
	    List<TimeTable> ttnew = tt.stream()
	            .collect(Collectors.toMap(
	                    obj -> obj.getSchool(),  // Composite key
	                    Function.identity(),  // Keep the original object
	                    (obj1, obj2) -> obj1  // Merge function (in case of duplicate keys)
	            ))
	            .values()
	            .stream()
	            .collect(Collectors.toList());

       
        for (TimeTable it : ttnew) {
           
            Optional<Long> optionalGroup = Optional.of(it.getSchool().getOwner().getId());
            Optional<Long> optionalOwner = Optional.of(it.getSchool().getSchId());
            Optional<String> optionalYear = Optional.of( getYearFromTimestamp(it.getCalendar().getStartdate()));
            Optional<Integer> optionalTerm = Optional.of( it.getCalendar().getTerm() );
            Optional<Timestamp> optionalDatefrom = Optional.of( parseTimestamp(todayDate()) );

            Optional<Integer> nullVal  = Optional.ofNullable(null);     
            Optional<Long>    nullVal3 = Optional.ofNullable(null);     
            Optional<Timestamp>    nullVal4 = Optional.ofNullable(null);     

            Map<String, Object> response = mneService.getOrdinaryLessonnoteFlags( "", optionalGroup, optionalOwner, optionalYear, optionalTerm, nullVal, nullVal, nullVal3, nullVal3, optionalDatefrom, nullVal4 );
            
            int teacher_management = (int) response.get("total_lessonnotes");
            int teacher_submitted = (int) response.get("teacher_submitted");
            int teacher_late_submitted = (int) response.get("teacher_late_submitted");
            int teacher_late_approval = (int) response.get("teacher_late_approval");
            int teacher_no_approval = (int) response.get("teacher_no_approval");
            int teacher_queried = (int) response.get("teacher_queried");
            int teacher_late_closure = (int) response.get("teacher_late_closure");
            int teacher_bad_cycles = (int) response.get("teacher_bad_cycles");
            int teacher_no_closure = (int) response.get("teacher_no_closure");

            // Create CSV
            String csvFile = "data.csv";
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
                String[] header = {"Date of Flags", "Total Lessonnotes", "Submitted", "Late Submitted", "Late Approval", "No Approval", "Queried", "Late Closure", "Bad Cycles", "No Closure"};
                writer.writeNext(header);
                
                String[] data = { String.valueOf(todayDate()), String.valueOf(teacher_management), String.valueOf(teacher_submitted), String.valueOf(teacher_late_submitted), String.valueOf(teacher_late_approval), String.valueOf(teacher_no_approval), String.valueOf(teacher_queried), String.valueOf(teacher_late_closure), String.valueOf(teacher_bad_cycles), String.valueOf(teacher_no_closure) };
                writer.writeNext(data);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Zip the CSV
            String zipFile = "data.zip";
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
                FileInputStream fis = new FileInputStream(csvFile)) {
                ZipEntry zipEntry = new ZipEntry(csvFile);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Generate file name for S3
            String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String bucketName = "standb670";
            String schoolId = String.valueOf(it.getSchool().getId());
            String s3FileName = "lessonnoteFlags" + "/" + date + "_" + schoolId + "_console_snapshot.zip";

            S3Client client = S3Client.builder().build();
		        
			PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3FileName)
                        .acl("public-read")
                        .build();

            client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(zipFile)));
          
            // Clean up local files
            new File(csvFile).delete();
            new File(zipFile).delete();

            /////////////////////////////////////////////////////////////////////////////////////////////////////
        }
    }

    @Transactional
    @Scheduled(cron = "0 35 18 * * *")
    public void attendanceFlagsSnapshot() {
        System.setProperty("aws.accessKeyId", accesskey);
        System.setProperty("aws.secretAccessKey", sk);
        System.setProperty("aws.region", region);
      
        // Get Timetable data for this day with Current calendar
	    List<TimeTable> tt = timeRepository.findByActiveCalendarInConsole(1, 1, "government", 1);
	    
	    // Get Unique timetable data based on school values
	    List<TimeTable> ttnew = tt.stream()
	            .collect(Collectors.toMap(
	                    obj -> obj.getSchool(),  // Composite key
	                    Function.identity(),  // Keep the original object
	                    (obj1, obj2) -> obj1  // Merge function (in case of duplicate keys)
	            ))
	            .values()
	            .stream()
	            .collect(Collectors.toList());

       
        for (TimeTable it : ttnew) {
           
            Optional<Long> optionalGroup = Optional.of(it.getSchool().getOwner().getId());
            Optional<Long> optionalOwner = Optional.of(it.getSchool().getSchId());           
            Optional<Long> optionalCalendar = Optional.of( it.getCalendar().getCalendarId() );
            Optional<Timestamp> optionalDatefrom = Optional.of( parseTimestamp(todayDate()) );
               
            Optional<Long>    nullVal3 = Optional.ofNullable(null);     
            Optional<Timestamp>    nullVal4 = Optional.ofNullable(null);

            Map<String, Object> response = mneService.getOrdinaryAttendanceFlags( "", optionalGroup, optionalOwner, nullVal3, optionalCalendar, nullVal3, nullVal3, nullVal3, optionalDatefrom, nullVal4 );
      
            int student_absence = (int) response.get("student_absence");
            int student_excused_absence = (int) response.get("student_excused_absence");
            int queried_attendance = (int) response.get("queried_attendance");
            int late_attendance = (int) response.get("late_attendance");
            int void_attendance = (int) response.get("void_attendance");
            int approval_delays = (int) response.get("approval_delays");
            int approval_done = (int) response.get("approval_done");
            int teacher_absent = (int) response.get("teacher_absent");
            int teacher_expected = (int) response.get("teacher_expected");
            int student_expected = (int) response.get("student_expected");
            int endorsement_expected = (int) response.get("endorsement_expected");

            // Create CSV
            String csvFile = "data.csv";
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
                String[] header = {"Date of Flags", "Student Absence", "Student Excused Absence", "Queried Attendance", "Late Attendance", "Void Attendance", "Approval Delays", "Approval Done", "Teacher Absent", "Teacher Expected", "Student Expected", "Endorsement Expected"};
                writer.writeNext(header);
                
                String[] data = { String.valueOf(todayDate()), String.valueOf(student_absence), String.valueOf(student_excused_absence), String.valueOf(queried_attendance), String.valueOf(late_attendance), String.valueOf(void_attendance), String.valueOf(approval_delays), String.valueOf(approval_done), String.valueOf(teacher_absent), String.valueOf(teacher_expected), String.valueOf(student_expected), String.valueOf(endorsement_expected) };
                writer.writeNext(data);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Zip the CSV
            String zipFile = "data.zip";
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
                FileInputStream fis = new FileInputStream(csvFile)) {
                ZipEntry zipEntry = new ZipEntry(csvFile);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }	

            // Generate file name for S3
            String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String bucketName = "standb670";
            String schoolId = String.valueOf(it.getSchool().getId());
            String s3FileName = "attendanceFlags" + "/" + date + "_" + schoolId + "_console_snapshot.zip";

            S3Client client = S3Client.builder().build();
		        
			PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3FileName)
                        .acl("public-read")
                        .build();

            client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromFile(new File(zipFile)));
          
            // Clean up local files
            new File(csvFile).delete();
            new File(zipFile).delete();

            /////////////////////////////////////////////////////////////////////////////////////////////////////
        }
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
    
    private String[] getHeaders(Class<?> clazz) {
        List<String> headers = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            headers.add(field.getName());
        }
        return headers.toArray(new String[0]);
    }

    private String[] getSchoolData(School school) {
        List<String> data = new ArrayList<>();
        for (Field field : School.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(school);
                data.add(value != null ? value.toString() : "");
            } catch (IllegalAccessException e) {
                data.add("");
            }
        }
        return data.toArray(new String[0]);
    }

    private String[] getTeacherData(Teacher teacher) {
        List<String> data = new ArrayList<>();
        for (Field field : Teacher.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(teacher);
                data.add(value != null ? value.toString() : "");
            } catch (IllegalAccessException e) {
                data.add("");
            }
        }
        return data.toArray(new String[0]);
    }

    private String[] getEnrollmentData(Enrollment enroll) {
        List<String> data = new ArrayList<>();
        for (Field field : Enrollment.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(enroll);
                data.add(value != null ? value.toString() : "");
            } catch (IllegalAccessException e) {
                data.add("");
            }
        }
        return data.toArray(new String[0]);
    }

    private String[] getClassStreamData(ClassStream cls) {
        List<String> data = new ArrayList<>();
        for (Field field : ClassStream.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(cls);
                data.add(value != null ? value.toString() : "");
            } catch (IllegalAccessException e) {
                data.add("");
            }
        }
        return data.toArray(new String[0]);
    }

    private String[] getTimetableData(TimeTable timme) {
        List<String> data = new ArrayList<>();
        for (Field field : TimeTable.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(timme);
                data.add(value != null ? value.toString() : "");
            } catch (IllegalAccessException e) {
                data.add("");
            }
        }
        return data.toArray(new String[0]);
    }

    private String[] getCalendarData(Calendar cal) {
        List<String> data = new ArrayList<>();
        for (Field field : Calendar.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(cal);
                data.add(value != null ? value.toString() : "");
            } catch (IllegalAccessException e) {
                data.add("");
            }
        }
        return data.toArray(new String[0]);
    }

    private String getYearFromTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        
        LocalDateTime dateTime = timestamp.toLocalDateTime();
        return String.valueOf(dateTime.getYear());
    }
	
	private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}
