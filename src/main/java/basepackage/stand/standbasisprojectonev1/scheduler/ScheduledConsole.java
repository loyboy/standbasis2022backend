package basepackage.stand.standbasisprojectonev1.scheduler;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import basepackage.stand.standbasisprojectonev1.model.School;
import basepackage.stand.standbasisprojectonev1.model.TimeTable;
import basepackage.stand.standbasisprojectonev1.repository.TimetableRepository;
import basepackage.stand.standbasisprojectonev1.service.SchoolService;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import com.opencsv.CSVWriter;



@Component
public class ScheduledConsole {

    @Autowired		
    private TimetableRepository timeRepository;

    @Autowired
    SchoolService service;

    @Value("${aws.region}")
    private String region;
    
    @Value("${aws.secretKey}")
    private String sk;
    
    @Value("${aws.accessKeyId}")
    private String accesskey;
    
    private final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    //for schools data
    @Scheduled(cron = "0 55 15 * * *")
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
            Timestamp realDate = parseTimestamp(todayDate());
            Optional<Long> optionalOwner = Optional.of(it.getSchool().getOwner().getId());
            Optional<Timestamp> optionalTimestamp = Optional.of(realDate);
            Map<String, Object> response2 = service.getOrdinarySchools("", optionalOwner, optionalTimestamp);
            
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
            String s3FileName = date + "_" + schoolId + "_console_snapshot.zip";

            // Upload to S3
           // AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
           // s3Client.putObject(new PutObjectRequest(bucketName, s3FileName, new File(zipFile)));

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
}
