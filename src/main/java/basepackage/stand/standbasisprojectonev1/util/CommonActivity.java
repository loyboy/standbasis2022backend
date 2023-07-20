package basepackage.stand.standbasisprojectonev1.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import basepackage.stand.standbasisprojectonev1.exception.BadRequestException;

public class CommonActivity {

	 public final static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 
	 public static String todayDate() {
			Date d = new Date();
	        String date = DATE_TIME_FORMAT.format(d);
	        return date;
	 }
	 
	 public static LocalDate dateToLocalDate(Date date) {
	        // Convert Date to Instant
	        Instant instant = date.toInstant();
	        
	        // Convert Instant to LocalDate
	        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
	 }
	 
	 public static Date localDateToDate(LocalDate localDate) {
	        // Convert LocalDate to Instant
	        Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
	        
	        // Convert Instant to Date
	        return Date.from(instant);
	 }
	 
	 public static java.sql.Timestamp parseTimestamp(String timestamp) {
		    try {
		        return new Timestamp(DATE_TIME_FORMAT.parse(timestamp).getTime());
		    } catch (ParseException e) {
		        throw new IllegalArgumentException(e);
		    }
	 }

	 public static void copyNonNullProperties(Object src, Object target) {
	    BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
	}

	public static String[] getNullPropertyNames (Object source) {
	    final BeanWrapper src = new BeanWrapperImpl(source);
	    java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

	    Set<String> emptyNames = new HashSet<String>();
	    for(java.beans.PropertyDescriptor pd : pds) {
	        Object srcValue = src.getPropertyValue(pd.getName());
	        if (srcValue == null) emptyNames.add(pd.getName());
	    }
	    String[] result = new String[emptyNames.size()];
	    return emptyNames.toArray(result);
	}
	
	public static void validatePageNumberAndSize(int page, int size) {
     if(page < 0) {
         throw new BadRequestException("Page number cannot be less than zero.");
     }

     if(size > AppConstants.MAX_PAGE_SIZE) {
         throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
     }
	}
}
